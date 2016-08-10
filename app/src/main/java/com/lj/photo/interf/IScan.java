package com.lj.photo.interf;

import com.lj.photo.PhotoFile;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
public interface IScan {
    /**
     * 发出扫描事件，扫描本地所有的文件数据
     */
    public void scan();

    /**
     * 获取所有的文件夹
     * */
    public Map<String,List<PhotoFile>> getFolders();

    /**
     * 获取固定相册内的文件
     * @param folder 文件夹名称
     * @return  文件列表
     * */
    public List<PhotoFile> getPhotos(String folder);
}