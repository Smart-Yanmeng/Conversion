package com.ruoyi.quartz.util;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.quartz.exception.EmptyFileException;
import com.ruoyi.quartz.exception.GenerateFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileSavingUtil {
    // 总路径
    protected static final String DIR = RuoYiConfig.getUploadPath();
    protected static final String HTTP_DIR = "/profile/upload";

    /**
     * 保存单个文件并返回
     *
     * @param file 需要保存的文件
     * @return 文件下载 Http 链接
     */
    public static String savingFile(MultipartFile file) {
        // 生成文件名 -> 假定 UUID 永不重复
        String filename = UUID.randomUUID().toString();

        // 获取文件拓展名，不区分大小写，最后全转化为小写
        String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        // 生成文件保存路径
        String savingDir = "/" + fileExtension + "s";

        // 生成文件夹（如果不存在的话）
        try {
            Path savingPath = Paths.get(DIR + savingDir);
            Files.createDirectories(savingPath);
        } catch (IOException e) {
            throw new GenerateFileException();
        }

        // 生成文件
        if (!file.isEmpty()) {
            String filepath = DIR + savingDir + "/" + filename + "." + fileExtension;
            try {
                file.transferTo(new File(filepath));
            } catch (IOException e) {
                throw new GenerateFileException();
            }

            System.out.println("文件保存成功，文件地址为：" + filepath);
        } else {
            throw new EmptyFileException();
        }

        return HTTP_DIR + savingDir + "/" + filename + "." + fileExtension;
    }

    /**
     * 保存多个文件并返回
     *
     * @param files 需要保存的文件集合
     * @return 文件下载 Http 链接
     */
    public static String savingFile(List<MultipartFile> files) {
        // 生成文件名 -> 假定 UUID 永不重复
        String filename = UUID.randomUUID().toString();

        // 获取文件拓展名，不区分大小写，最后全转化为小写
        String fileExtension = Objects.requireNonNull(files.get(0).getOriginalFilename()).substring(Objects.requireNonNull(files.get(0).getOriginalFilename()).lastIndexOf(".") + 1);

        // 生成文件保存路径
        String savingDir = "/" + fileExtension + "s" + "/" + filename;

        // 生成文件夹（如果不存在的话）
        try {
            Path savingPath = Paths.get(DIR + savingDir);
            Files.createDirectories(savingPath);
        } catch (IOException e) {
            throw new GenerateFileException();
        }

        // 生成文件
        try {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (!file.isEmpty()) {
                    String filePath = DIR + savingDir + "/" + "file_" + i + "." + fileExtension;
                    file.transferTo(new File(filePath));
                } else {
                    throw new EmptyFileException();
                }
            }
        } catch (IOException e) {
            throw new GenerateFileException();
        }

        System.out.println("文件保存成功，文件夹地址为：" + DIR + savingDir);

        return HTTP_DIR + savingDir + "/" + filename;
    }

    /**
     * 保存单个文件并返回
     *
     * @param fileData      需要保存的文件的字节流
     * @param fileExtension 需要保存的文件的拓展名，不区分大小写
     * @return 文件下载 Http 链接
     */
    public static String savingFile(byte[] fileData, String fileExtension) {
        // 生成文件名 -> 假定 UUID 永不重复
        String filename = UUID.randomUUID().toString();

        // 将文件拓展名全改为小写
        fileExtension = fileExtension.toLowerCase();

        // 生成文件保存路径
        String savingDir = "/" + fileExtension + "s";

        // 生成文件夹（如果不存在的话）
        try {
            Path savingPath = Paths.get(DIR + savingDir);
            Files.createDirectories(savingPath);
        } catch (IOException e) {
            throw new GenerateFileException();
        }

        // 生成文件
        try {
            String savingPath = DIR + savingDir + "/" + filename + "." + fileExtension;
            Path filePath = Paths.get(savingPath);
            Files.write(filePath, fileData);

            System.out.println("文件保存成功，文件地址为：" + filePath);

            return HTTP_DIR + savingDir + "/" + filename + "." + fileExtension;
        } catch (IOException e) {
            throw new GenerateFileException();
        }
    }
}
