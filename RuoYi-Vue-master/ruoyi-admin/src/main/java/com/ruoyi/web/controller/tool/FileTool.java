package com.ruoyi.web.controller.tool;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Configuration
public class FileTool {

    public FileTool(){
        System.out.println("创建了");
    }


    @Autowired
    private ServerConfig serverConfig;

    /**
     * 获取文件信息
     *
     * @param file 文件
     * @return
     * @throws Exception
     */
    public List<String> getPaths(MultipartFile file) throws Exception {
        HashMap<String,String> map = new HashMap<>();
        String filePath = RuoYiConfig.getUploadPath();  //文件上传路径
        String fileName = FileUploadUtils.upload(filePath, file);  //新文件名称
        String originUrl = filePath + fileName.substring(15, fileName.length()); //文件源路径
        String newUrl = serverConfig.getUrl() + fileName; //文件http路径
        String name = originUrl.substring(originUrl.lastIndexOf("/") + 1); //文件名称【不含路径】
        List<String> pathList = Arrays.asList(filePath, fileName, originUrl, newUrl, name);
        return pathList;
    }


}
