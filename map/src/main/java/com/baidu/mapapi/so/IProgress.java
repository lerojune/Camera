package com.baidu.mapapi.so;

/**
 * Created by Administrator on 2016/8/17.
 */
public interface IProgress {
    //初始
    void onStart(int count);
    //当前
    void onProgress(int index);
    //结束
    void onEnd();
}
