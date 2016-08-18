package com.baidu.mapapi.so;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/17.
 * 文件检测，获取
 */
public class SoFile {
    Context context;

    static {
        cpuInfo = Platform.arm;
    }

    static Platform cpuInfo;

    public SoFile(@NonNull Context context) {
        this.context = context;
    }

    String[] libNames = {"libBaiduMapSDK_base_v4_0_0.so", "libBaiduMapSDK_map_v4_0_0.so",
            "libBaiduMapSDK_search_v4_0_0.so", "libBaiduMapSDK_util_v4_0_0.so"};

    /**是否包含
     * */
    private boolean contain(String[] array, String name){
        if (array == null || array.length < 1)
            return false;
        for (String a:array){
            if (a.equals(name))
                return true;
        }
        return false;
    }

    /**
     * 是否已经初始化
     */
    public boolean hasInitialize() {
        String[] files = context.getFilesDir().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".so");
            }
        });
        if (files == null)
            return false;
        for (String name : libNames) {
            if (!contain(files,  name)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 安装插件,需要在子线程中执行
     */
    public synchronized boolean plugInstall(INet net,@NonNull IProgress progress) {
        //检测本地插件
        String[] files = context.getFilesDir().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".so");
            }
        });

        ArrayList<String> needInstall = new ArrayList<String>();
        for (String name : libNames) {
            if (!contain(files,  name)) {
                needInstall.add(name);
            }
        }
        progress.onStart(needInstall.size());
        //下载插件
        int index = 1;
        for (String name : needInstall) {
            progress.onProgress(index++);
            FileOutputStream out = null;
            InputStream input = net.getFile(cputype(), name);
            if (input == null){
                progress.onEnd();
                return false;
            }
            try {
                File outFile = new File(context.getFilesDir(), name);
                out = new FileOutputStream(outFile);
                if (!copyStream(input, out)){
                    progress.onEnd();
                    return false;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                progress.onEnd();
                return false;
            }
        }
        progress.onEnd();
        return true;
    }


    /**
     * 将文件拷贝到指定文件
     */
    protected final boolean copyStream(InputStream var1, FileOutputStream var2) {
        byte[] var3 = new byte[4096];
        try {
            int var4;
            while ((var4 = var1.read(var3)) != -1) {
                var2.write(var3, 0, var4);
            }
            var2.flush();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                var1.close();
            } catch (IOException var14) {
                return false;
            }

            try {
                var2.close();
            } catch (IOException var13) {
                return false;
            }
            return true;
        }
    }


    private static enum Platform {
        arm("armeabi"),
        armv7("armeabi-v7a"),
        arm64("arm64-v8a"),
        x86("x86"),
        x64("x86_64");

        private String type;

        private Platform(String var3) {
            this.type = var3;
        }

        public String cpu() {
            return this.type;
        }
    }

    /**
     * cpu 型号
     */
    @TargetApi(21)
    private String cputype() {
        String var0 = null;
        if (Build.VERSION.SDK_INT < 21) {
            var0 = Build.CPU_ABI;
        } else {
            var0 = Build.SUPPORTED_ABIS[0];
        }

        if (var0 == null) {
            return cpuInfo.cpu();
        } else {
            if (var0.contains("arm") && var0.contains("v7")) {
                cpuInfo = Platform.armv7;
            }

            if (var0.contains("arm") && var0.contains("64")) {
                cpuInfo = Platform.arm64;
            }

            if (var0.contains("x86")) {
                if (var0.contains("64")) {
                    cpuInfo = Platform.x64;
                } else {
                    cpuInfo = Platform.x86;
                }
            }
            return cpuInfo.cpu();
        }
    }
}
