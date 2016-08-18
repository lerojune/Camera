package com.baidu.mapapi.so;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

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
        File[] files1 = (File[])((Field) nativeLibraryDirectories).get(pathList);
        Object filesss = Array.newInstance(File.class, files1.length + 1);
        //添加自定义.so路径
        Array.set(filesss, 0, new File(context.getFilesDir().getAbsolutePath()));
        //将系统自己的追加上
        for(int i = 1;i<files1.length+1;i++){
            Array.set(filesss,i,files1[i-1]);
        }
//        File[] filesss = new File[file.length+ files1.length];
//        filesss[0] = file[0];
//        for(int i = 1;i < files1.length+1;i++){
//            filesss[i] = files1[i];
//        }
        ((Field) nativeLibraryDirectories).set(pathList, filesss);
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
     *  仅对4.0以上做支持
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
