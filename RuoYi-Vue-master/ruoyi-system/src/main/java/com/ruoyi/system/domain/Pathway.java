package com.ruoyi.system.domain;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
public class Pathway {
    private String srcPath;
    private String desPath;
    private String newPath;

    public Pathway() {
    }

    public Pathway(String srcPath, String desPath, String newPath) {
        this.srcPath = srcPath;
        this.desPath = desPath;
        this.newPath = newPath;
    }

}
