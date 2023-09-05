package com.ruoyi.system.domain;

import lombok.Data;

import java.util.Date;
@Data
public class ConvertFile {
    private Long fileId;
    private String originalFilePath;
    private String processedFilePath;
    private Date createTime;

    public ConvertFile() {
    }

    public ConvertFile( String originalFilePath, String processedFilePath) {
        this.originalFilePath = originalFilePath;
        this.processedFilePath = processedFilePath;
    }

    public ConvertFile(String filename, String filepath, String filepath1) {

    }

    @Override
    public String toString() {
        return "ConvertFile{" +
                "fileId=" + fileId +
                ", originalFilePath='" + originalFilePath + '\'' +
                ", processedFilePath='" + processedFilePath + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
