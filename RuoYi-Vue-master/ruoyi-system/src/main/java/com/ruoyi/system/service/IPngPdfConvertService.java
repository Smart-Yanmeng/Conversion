package com.ruoyi.system.service;

import com.ruoyi.system.domain.ConvertFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * PND PDF 文件互转服务接口
 */
public interface IPngPdfConvertService {
    ConvertFile pdfToPng(MultipartFile file) throws Exception;

    ConvertFile pngToPdf(List<MultipartFile> files) throws Exception;
}
