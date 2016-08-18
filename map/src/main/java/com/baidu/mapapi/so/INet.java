package com.baidu.mapapi.so;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/17.
 * 文件获取接口
 */
public interface INet {
    /**
     * 获取文件流
     * */
    InputStream getFile(String type, String name);
}
