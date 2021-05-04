package com.qiqilm.server.admin.im;


public enum MessageEnum {
    TIMTextElem("TIMTextElem"),  //文本
    TIMLocationElem("TIMLocationElem") ,  //位置
    TIMFaceElem("TIMFaceElem"), //表情
    TIMCustomElem("TIMCustomElem"),//自定义
    TIMSoundElem("TIMSoundElem"), //语言
    TIMImageElem("TIMImageElem"),//图像
    TIMFileElem("TIMFileElem"),//文件
    TIMVideoFileElem("TIMVideoFileElem") //视频
    ;

    private String val;

    MessageEnum(String str) {
        val  = str;
    }

    @Override
    public String toString() {
        return val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
