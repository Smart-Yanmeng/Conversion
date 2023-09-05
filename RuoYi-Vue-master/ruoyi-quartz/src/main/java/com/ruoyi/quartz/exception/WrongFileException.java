package com.ruoyi.quartz.exception;

public class WrongFileException extends RuntimeException {
    public WrongFileException() {
        super("您上传的文件不合规！");
    }
}
