package com.baidu.mapapi.so;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * Created by Administrator on 2016/8/17.
 */
public class SOLoader {
    public static void initNativeDirectory(Application application) {
        if (hasDexClassLoader()) {
            try {
                createNewNativeDir(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void createNewNativeDir(Context context) throws Exception {
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        //获取当前类的属性
        Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
        ((Field) nativeLibraryDirectories).setAccessible(true);
        //获取 DEXPATHList中的属性值
        Object list = ((Field) nativeLibraryDirectories).get(pathList);
        Object tlist,blist;
        if (list instanceof ArrayList) {
            Object nativeLibraryPathElements = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
            ((Field) nativeLibraryPathElements).setAccessible(true);
            //获取 DEXPATHList中的属性值
            Object libs = ((Field) nativeLibraryPathElements).get(pathList);
            Object elements = getElement(context,libs);
            if (elements != null){
                ((Field) nativeLibraryPathElements).set(pathList, elements);
            }
            String t = libs.toString();
            Class cls = null;
        } else {
            File[] files1 = (File[]) list;
            Object filesss = Array.newInstance(File.class, files1.length + 1);
            //添加自定义.so路径
            Array.set(filesss, 0, new File(context.getFilesDir().getAbsolutePath()));
            //将系统自己的追加上
            for (int i = 1; i < files1.length + 1; i++) {
                Array.set(filesss, i, files1[i - 1]);
            }
            ((Field) nativeLibraryDirectories).set(pathList, filesss);
        }
    }



    private static Object getElement(Context context, Object obj){
        try {
            int length = Array.getLength(obj);
            Object filesss = Array.newInstance(Class.forName("dalvik.system.DexPathList$Element"), length + 1);
            //添加自定义.so路径
            Class[] argtype = new Class[] {File.class, boolean.class, File.class, DexFile.class};
            Object[] argparam = new Object[] {context.getFilesDir(),true,null,null};
            Class classType = Class.forName("dalvik.system.DexPathList$Element");
            Constructor constructor = classType.getDeclaredConstructor(argtype);
            constructor.setAccessible(true);
            Object element = constructor.newInstance(argparam);
            Array.set(filesss, 0, element);
            //将系统自己的追加上
            for (int i = 1; i < length + 1; i++) {
                Array.set(filesss, i, Array.get(obj, i - 1));
            }
            return filesss;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Object getPathList(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(obj, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class cls, String str) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    /**
     * 仅对4.0以上做支持
     *
     * @return
     */
    private static boolean hasDexClassLoader() {
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
            return true;
        } catch (ClassNotFoundException var1) {
            return false;
        }
    }
}
