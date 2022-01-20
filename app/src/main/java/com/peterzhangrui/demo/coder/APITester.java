package com.peterzhangrui.demo.coder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class APITester<T> extends Activity {

    private static CodeNode rootNode;
    private static CodeNode currentNode;
    public abstract T test(Activity activity);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("not content!");
        setContentView(textView);
        test(this);
    }

    public static void init(Context context) {
        initData(context);
    }

    private static void initData(Context context) {
        String pkgName = context.getPackageName();
        currentNode = rootNode = new CodeNode(pkgName, CodeNode.DIR);
        new CodeNodeLoader().load(rootNode, context.getApplicationContext());
        showView(context);
    }

    private static void showView(Context context) {
        switch (currentNode.type) {
            case CodeNode.DIR:
                installListView(context);
                break;
            case CodeNode.CLASS:
                try {
                    Class clazz = context.getClassLoader().loadClass(currentNode.className);
                    Intent intent = new Intent(context, clazz).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

    private static void installListView(Context context) {
        if (currentNode.mSubNodeList != null) {
            ArrayMap<CharSequence, CodeNode> map = new ArrayMap<>();
            for (CodeNode codeNode : currentNode.mSubNodeList) {
                if (codeNode.type == CodeNode.DIR) {
                    map.put("[f] " + codeNode.name, codeNode);
                } else {
                    map.put("[t] " + codeNode.name, codeNode);
                }
            }
            CharSequence[] items = new String[map.size()];
            map.keySet().toArray(items);
            new AlertDialog.Builder(context).setTitle(currentNode.name).setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    currentNode = map.get(items[which]);
                    showView(context);
                }
            }).setCancelable(false).setNegativeButton("back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("peter", "");
                }
            }).show();

        }
    }

    private static void showLogDialog(String tag, String msg) {
//        if (activity != null) {
//            new AlertDialog.Builder(activity).setTitle("LogTag:" + tag).setMessage(msg).show();
//        }
    }

    protected void logD(String tag, String msg) {
        Log.d(tag, msg);
        showLogDialog(tag, msg);
    }

    protected void logW(String tag, String msg) {
        Log.w(tag, msg);
        showLogDialog(tag, msg);
    }

    protected void logI(String tag, String msg) {
        Log.i(tag, msg);
        showLogDialog(tag, msg);
    }

    protected void logE(String tag, String msg) {
        Log.e(tag, msg);
        showLogDialog(tag, msg);
    }


    // ====================================== code loader ============================================
    private static final class CodeNodeLoader {

        public void load(CodeNode rootNode, Context context) {
            String pkgName = context.getPackageName();
            Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_TEST);
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);

            for (ResolveInfo info : list) {
                if (TextUtils.equals(info.activityInfo.packageName, pkgName)) {
                    String name = info.activityInfo.name;
                    String fileName = name.substring(pkgName.length() + 1);
                    String[] fileNames = fileName.split("\\.");
                    loadCodeBagNode(name, fileNames, 0, rootNode);
                }
            }
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
