package com.ruoyi.quartz.exception;

public class GenerateFileException extends RuntimeException {
    public GenerateFileException() {
        super("文件路径创建失败！");
    }
}
