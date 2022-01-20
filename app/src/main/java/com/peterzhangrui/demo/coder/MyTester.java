package com.peterzhangrui.demo.coder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public abstract class MyTester<T> {

    private static CodeNode rootNode;
    private static CodeNode currentNode;
    private static Context mApplicationContext;

    public abstract T test(Context appContext);

    public static void show(Context context) {
        mApplicationContext = context;
        initData(context);
        initView(context);
    }

    private static void initData(Context context) {
        if (rootNode == null) {
            String pkgName = context.getPackageName();
            currentNode = rootNode = new CodeNode(pkgName, CodeNode.DIR);
            new CodeNodeLoader().load(rootNode, context.getApplicationContext());
        }
    }

    private static void initView(Context context) {
        switch (currentNode.type) {
            case CodeNode.DIR:
                installListView(context);
                break;
            case CodeNode.CLASS:
                try {
                    invokeMethod(context);
                } catch (Exception e) {
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
                    show(context);
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
        if (mApplicationContext != null) {
            new AlertDialog.Builder(mApplicationContext).setTitle("LogTag:" + tag).setMessage(msg).show();
        }
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


    private static void invokeMethod(Context context) throws Exception {
        Object obj = context.getClassLoader().loadClass(currentNode.className).newInstance();
        if (currentNode != null && currentNode.name != null) {
            Method method = obj.getClass().getDeclaredMethod(Tester.METHOD_NAME, Context.class);
            method.setAccessible(true);
            method.invoke(obj, mApplicationContext);
        }
    }

    // ====================================== tester ============================================
    public static interface Tester{
        public void test();
        public static final String METHOD_NAME = "test";
    }

    // ====================================== code loader ============================================
    private static final class CodeNodeLoader {


        private Object[] getDexElementsFromClassLoader(BaseDexClassLoader loader) throws Exception {
            Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            // This is a DexPathList, but that class is package private.
            Object pathList = pathListField.get(loader);
            Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            // The objects in this array are Elements, but that class is package private.
            return (Object[]) dexElementsField.get(pathList);
        }

        public void load(CodeNode rootNode, Context context) {
            String pkgName = context.getPackageName();
            try {
                String apkDir = context.getPackageManager().getApplicationInfo(pkgName, 0).sourceDir;

                Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources("com/peterzhangrui");

                PathClassLoader pathClassLoader = new PathClassLoader(apkDir, context.getClassLoader());
                Enumeration<URL> enumeration = pathClassLoader.getResources("classpath*:com/peterzhangrui/demo/**");
                Enumeration<URL> enumeration1 = pathClassLoader.getResources("demo");
                Enumeration<URL> enumeration2 = mApplicationContext.getClass().getClassLoader().getResources("");

                Object[] objects = getDexElementsFromClassLoader(pathClassLoader);

                ApplicationInfo info = context.getApplicationInfo();
                String dexPath=info.sourceDir;
                String dexOutputDir=info.dataDir;
                String libPath=info.nativeLibraryDir;
                DexClassLoader dl= new DexClassLoader(dexPath, dexOutputDir,
                        libPath, context.getClass().getClassLoader());
                try {
                    Enumeration<URL> gaga = dl.getResources("");

                    Log.i("peter", gaga.toString());
                    System.out.println(1);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                while(enumeration.hasMoreElements()) {
                    URL url = enumeration.nextElement();
                    Log.d("url", url.toString());
                }

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
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected boolean isPlayClass(String className) {
            try {
                Class<?> clazz = Class.forName(className);
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    return MyTester.class.isAssignableFrom(clazz);
                }
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
