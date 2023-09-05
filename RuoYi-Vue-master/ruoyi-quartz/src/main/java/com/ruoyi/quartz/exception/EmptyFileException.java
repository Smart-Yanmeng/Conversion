package com.ruoyi.quartz.exception;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super("您上传的文件为空！");
    }
}
