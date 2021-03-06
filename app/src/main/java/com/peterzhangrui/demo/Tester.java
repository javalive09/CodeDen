package com.peterzhangrui.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * Copyright (c) 2021 Beijing Didi Inc.
 * <p>
 * class for API test
 * <p>
 * Author: peterzhangrui
 */
public abstract class Tester {

    private static CodeNode currentNode;
    private static WeakReference<Activity> mActivity;
    private static CodeNodeLoader mCodeLoader;

    /**
     * show tester view.
     *
     * @param activity enter activity
     */
    public static void show(Activity activity) {
        mActivity = new WeakReference<>(activity);
        initData(activity);
        showView(activity);
    }

    /**
     * init tester data.
     *
     * @param activity activity
     */
    private static void initData(Activity activity) {
        String pkgName = activity.getPackageName();
        CodeNode rootNode = new CodeNode(pkgName, CodeNode.DIR);
        currentNode = rootNode;
        mCodeLoader = new CodeNodeLoader();
        mCodeLoader.load(rootNode, activity.getApplicationContext());
    }

    /**
     * real show tester view.
     *
     * @param activity activity
     */
    private static void showView(Activity activity) {
        switch (currentNode.type) {
            case CodeNode.DIR:
                installListView(activity);
                break;
            case CodeNode.CLASS:
                try {
                    Class<?> cls = Class.forName(currentNode.className);
                    for (Method m : cls.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(Test.class) && Modifier.PUBLIC == m
                                .getModifiers()) {
                            String methodName = m.getName();
                            mCodeLoader.createAndAddSubNode(currentNode.className, methodName,
                                    CodeNode.METHOD, currentNode);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                installListView(activity);
                break;
            case CodeNode.METHOD:
                try {
                    invokeMethod(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * installListView
     *
     * @param activity activity
     */
    private static void installListView(Activity activity) {
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
            new AlertDialog.Builder(activity).setTitle(currentNode.name).setSingleChoiceItems(items, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentNode = map.get(items[which]);
                            showView(activity);
                        }
                    }).setCancelable(false).setNegativeButton("back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("peter", "");
                }
            }).show();

        }
    }

    /**
     * show log dialog
     *
     * @param tag tag
     * @param msg message
     */
    private static void showLogDialog(String tag, String msg) {
        if (mActivity != null && mActivity.get() != null) {
            new AlertDialog.Builder(mActivity.get()).setTitle("LogTag:" + tag).setMessage(msg).show();
        }
    }

    public static void logD(String tag, String msg) {
        Log.d(tag, msg);
        showLogDialog(tag, msg);
    }

    public static void logW(String tag, String msg) {
        Log.w(tag, msg);
        showLogDialog(tag, msg);
    }

    public static void logI(String tag, String msg) {
        Log.i(tag, msg);
        showLogDialog(tag, msg);
    }

    public void logE(String tag, String msg) {
        Log.e(tag, msg);
        showLogDialog(tag, msg);
    }

    /**
     * invoke test method
     *
     * @param activity activity
     * @throws Exception
     */
    private static void invokeMethod(Activity activity) throws Exception {
        Object obj = activity.getClassLoader().loadClass(currentNode.className).newInstance();
        if (currentNode != null && currentNode.name != null) {
            Method method = obj.getClass().getDeclaredMethod(currentNode.name, Activity.class);
            method.setAccessible(true);
            method.invoke(obj, activity);
        }
    }

    // ====================================== tester ============================================

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface Test {

    }

    // ====================================== code loader ============================================

    /**
     * class for load all test method
     */
    private static final class CodeNodeLoader {

        public void load(CodeNode rootNode, Context context) {
            String pkgName = context.getPackageName();
            try {
                String apkDir = context.getPackageManager().getApplicationInfo(pkgName, 0).sourceDir;
                DexFile dexFile = new DexFile(apkDir);
                Enumeration<String> apkClassNames = dexFile.entries();
                while (apkClassNames.hasMoreElements()) {
                    String className = apkClassNames.nextElement();
                    if(className.contains("$")) {
                        continue;
                    }
                    if(className.contains("BuildConfig")) {
                        continue;
                    }
                    if(className.contains(".R")) {
                        continue;
                    }
                    if(!className.startsWith(pkgName)) {
                        continue;
                    }
                    Log.d("load()", "pkgName className: " + className);
                    if(isTestClass(className)) {
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

        private boolean isTestClass(String className) {
            try {
                Log.d("isTestClass()", "className: " + className);
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Test.class)) {
                        return true;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * @param className   class????????????
         * @param fileNames   class???????????????????????????????????????????????????.???????????????????????????????????????
         * @param index       ?????????fileNames??????????????????
         * @param currentNode ?????????????????????????????????
         */
        protected void loadCodeBagNode(String className, String[] fileNames, int index, CodeNode currentNode) {
            if (index > fileNames.length - 1) {
                return;
            }
            String nodeName = fileNames[index];
            if (index == fileNames.length - 1) {//??????????????????????????????class
                createAndAddSubNode(className, nodeName, CodeNode.CLASS, currentNode);
            } else {//??????????????????????????????
                CodeNode subNode = createAndAddSubNode(className, nodeName, CodeNode.DIR, currentNode);
                index++;
                loadCodeBagNode(className, fileNames, index, subNode);
            }

        }

        /**
         * @param className   class????????????
         * @param nodeName    ??????????????????????????????????????????????????????--- ????????????????????????????????????
         * @param type        ????????????????????????/??????
         * @param currentNode ?????????
         * @return CodeNode
         */
        CodeNode createAndAddSubNode(String className, String nodeName, int type, CodeNode currentNode) {
            if (currentNode.mSubNodeList == null) {//?????????????????????
                currentNode.mSubNodeList = new ArrayList<>();
            } else {
                for (CodeNode n : currentNode.mSubNodeList) {//?????????????????????????????????????????????
                    if (TextUtils.equals(nodeName, n.name)) {
                        return n;
                    }
                }
            }
            return createSubNode(className, nodeName, type, currentNode);
        }

        /**
         * @param className   class????????????
         * @param nodeName    ???????????????Name
         * @param type        ?????????????????????
         * @param currentNode ????????????
         * @return ??????
         */
        private CodeNode createSubNode(String className, String nodeName, int type, CodeNode currentNode) {
            CodeNode node = new CodeNode(nodeName, type, className);
            currentNode.mSubNodeList.add(node);
            return node;
        }
    }

    // ====================================== code node ============================================

    /**
     * class for test node info
     */
    private static final class CodeNode implements Parcelable {
        public static final int DIR = 0;
        public static final int CLASS = 1;
        public static final int METHOD = 2;
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
