package com.peterzhangrui.demo.coder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.DexFile;

public class Coder {

    private static final String CURRENT_NODE = "currentNode";
    private static CodeNode rootNode;
    private CodeNode currentNode;
    private WeakReference<Activity> activityWeakReference;

    public void show(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        initData(activity);
        initView(activity);
    }

    private void initData(Activity activity) {
        Intent intent = activity.getIntent();
        if (rootNode == null) {
            String pkgName = activity.getPackageName();
            rootNode = new CodeNode(pkgName, CodeNode.DIR);
            new CodeNodeLoader().load(rootNode, activity.getApplicationContext());
        } else {
            if (intent != null) {
                Bundle bundle = intent.getBundleExtra(CURRENT_NODE);
                if (bundle != null) {
                    currentNode = bundle.getParcelable(CURRENT_NODE);
                }
            }
        }
        if (currentNode == null) {
            currentNode = rootNode;
        }
    }

    private void initView(Activity activity) {
        activity.setTitle(currentNode.name);
        switch (currentNode.type) {
            case CodeNode.DIR:
                installListView(activity);
                break;
            case CodeNode.CLASS:
                installEmptyContent(activity);
                try {
                    invokeMethod(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private void installEmptyContent(Activity activity) {
        TextView textView = new TextView(activity);
        textView.setText("no content!");
        activity.setContentView(textView);
    }

    private void installListView(Activity activity) {
        if (currentNode.mSubNodeList != null) {
            ListView listView = new ListView(activity);
            activity.setContentView(listView);
            listView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return currentNode.mSubNodeList.size();
                }

                @Override
                public CodeNode getItem(int position) {
                    return currentNode.mSubNodeList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Holder holder;
                    if (convertView == null) {
                        holder = new Holder();
                        convertView = holder.textView = new TextView(parent.getContext());
                        holder.textView.setTextSize(37);
                        convertView.setTag(holder);
                    }
                    holder = (Holder) convertView.getTag();
                    holder.textView.setText(getItem(position).name);
                    return convertView;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CodeNode node = currentNode.mSubNodeList.get(position);
                    startSimpleCodeActivity(node);
                }
            });
        } else {
            installEmptyContent(activity);
        }
    }

    private void startSimpleCodeActivity(CodeNode node) {
        if (activityWeakReference.get() != null) {
            Activity activity = activityWeakReference.get();
            Intent intent = new Intent(activity, activity.getClass());
            Bundle bundle = new Bundle();
            bundle.putParcelable(CURRENT_NODE, node);
            intent.putExtra(CURRENT_NODE, bundle);
            activity.startActivityForResult(intent, Activity.RESULT_OK);
        }
    }

    private void invokeMethod(Activity activity) throws Exception {
        Object obj = activity.getClassLoader().loadClass(currentNode.className).newInstance();
        if (currentNode != null && currentNode.name != null) {
            Method method = obj.getClass().getDeclaredMethod(AutoCreator.ENTER_METHOD_NAME, Activity.class);
            method.setAccessible(true);
            method.invoke(obj, activity);
        }
    }

    // ====================================== view holder ===========================================
    private static final class Holder {
        TextView textView;
    }

    // ====================================== code enter ============================================
    public static abstract class AutoCreator{
        public static final String ENTER_METHOD_NAME = "onCreate";
        public abstract void onCreate(Activity activity);
    }

    // ====================================== code loader ============================================
    private static final class CodeNodeLoader {

        public void load(CodeNode rootNode, Context context) {
            String pkgName = context.getPackageName();
            try {
                String apkDir = context.getPackageManager().getApplicationInfo(pkgName, 0).sourceDir;
                DexFile dexFile = new DexFile(apkDir);
                Enumeration<String> apkClassNames = dexFile.entries();
                while (apkClassNames.hasMoreElements()) {
                    String className = apkClassNames.nextElement();
                    if (className.startsWith(pkgName) && isPlayClass(className) & !className.contains("$") &
                            !className.endsWith(".R") & !className.contains("BuildConfig")) {
                        String fileName = className.substring(pkgName.length() + 1);
                        String[] fileNames = fileName.split("\\.");
                        loadCodeBagNode(className, fileNames, 0, rootNode);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        protected boolean isPlayClass(String className) {
            try {
                Class<?> clazz = Class.forName(className);
                return AutoCreator.class.isAssignableFrom(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * @param className   class全路径名
         * @param fileNames   class全路径名除去根路径，剩下的字符以“.”为划分记号，划分成的数组
         * @param index       游标在fileNames数组中的位置
         * @param currentNode 当前节点（作为父节点）
         */
        protected void loadCodeBagNode(String className, String[] fileNames, int index, CodeNode currentNode) {
            if (index > fileNames.length - 1) {
                return;
            }
            String nodeName = fileNames[index];
            if (index == fileNames.length - 1) {//数组的最后一个元素为class
                createAndAddSubNode(className, nodeName, CodeNode.CLASS, currentNode);
            } else {//数组中其他元素为目录
                CodeNode subNode = createAndAddSubNode(className, nodeName, CodeNode.DIR, currentNode);
                index++;
                loadCodeBagNode(className, fileNames, index, subNode);
            }

        }

        /**
         * @param className   class全路径名
         * @param nodeName    子节点名字（游标所在数组的元素名字）--- 是区分各个子节点的关键字
         * @param type        子节点类型（目录/类）
         * @param currentNode 父节点
         * @return CodeNode
         */
        CodeNode createAndAddSubNode(String className, String nodeName, int type, CodeNode currentNode) {
            if (currentNode.mSubNodeList == null) {//创建子节点列表
                currentNode.mSubNodeList = new ArrayList<>();
            } else {
                for (CodeNode n : currentNode.mSubNodeList) {//父节点有子节点列表，则遍历一下
                    if (TextUtils.equals(nodeName, n.name)) {
                        return n;
                    }
                }
            }
            return createSubNode(className, nodeName, type, currentNode);
        }

        /**
         * @param className   class全路径名
         * @param nodeName    新建的节点Name
         * @param type        新建的节点类型
         * @param currentNode 当前节点
         * @return 节点
         */
        private CodeNode createSubNode(String className, String nodeName, int type, CodeNode currentNode) {
            CodeNode node = new CodeNode(nodeName, type, className);
            currentNode.mSubNodeList.add(node);
            return node;
        }
    }

    // ====================================== code node ============================================
    public static final class CodeNode implements Parcelable {
        public static final int DIR = 0;
        public static final int CLASS = 1;
        public final int type;
        public final String name;
        public String className;
        public ArrayList<CodeNode> mSubNodeList;

        public CodeNode(String name, int type) {
            this.name = name;
            this.type = type;
        }

        public CodeNode(String name, int type, String className) {
            this(name, type);
            this.className = className;
        }

        private CodeNode(Parcel in) {
            type = in.readInt();
            name = in.readString();
            className = in.readString();
            mSubNodeList = in.createTypedArrayList(CodeNode.CREATOR);
        }

        public static final Creator<CodeNode> CREATOR = new Creator<CodeNode>() {
            @Override
            public CodeNode createFromParcel(Parcel in) {
                return new CodeNode(in);
            }

            @Override
            public CodeNode[] newArray(int size) {
                return new CodeNode[size];
            }
        };

        @Override
        public String toString() {
            return "CodeNode{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", className='" + className + '\'' +
                    ", mSubNodeList=" + mSubNodeList +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(type);
            dest.writeString(name);
            dest.writeString(className);
            dest.writeTypedList(mSubNodeList);
        }
    }

}
