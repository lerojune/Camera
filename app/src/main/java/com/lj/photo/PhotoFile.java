package com.lj.photo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/10.
 *
 * 相册图片文件数据
 */
public class PhotoFile implements Serializable{
    String origenPath;//真实图
    String thumbPath;//缩略图
    int degree;//图片偏转角度

    public int getDegree() {
        return degree;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getOrigenPath() {
        return origenPath;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void setOrigenPath(String origenPath) {
        this.origenPath = origenPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
