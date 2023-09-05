package com.ruoyi.system.service.impl;

import com.ruoyi.quartz.util.FileSavingUtil;
import com.ruoyi.quartz.util.PngPdfConverterUtil;
import com.ruoyi.quartz.util.ZipUtil;
import com.ruoyi.system.domain.ConvertFile;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.IPngPdfConvertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class PngPdfConvertServiceImpl implements IPngPdfConvertService {
    @Resource
    private ConvertFileMapper mapper;

    @Override
    public ConvertFile pdfToPng(MultipartFile file) throws IOException {
        // 转换 PDF 文件为 PNG 文件
        List<byte[]> files = PngPdfConverterUtil.pdfToPng(file);
        byte[] zipData = ZipUtil.packToZip(files);

        // 保存 PDF 文件到服务器上
        String originalFile = FileSavingUtil.savingFile(file);

        // 保存 ZIP 文件到服务器上
        String processedFile = FileSavingUtil.savingFile(zipData, "zip");

        // 保存文件名和路径到数据库
        mapper.addConvertFile(new ConvertFile(originalFile, processedFile));

        // 返回文件实体类
        return new ConvertFile(originalFile, processedFile);
    }

    @Override
    public ConvertFile pngToPdf(List<MultipartFile> files) throws IOException {
        // 转换 PNG 文件为 PDF 文件
        byte[] pdfData = PngPdfConverterUtil.pngToPdf(files);

        // 保存 PNG 文件到服务器上
        String  originalFile = FileSavingUtil.savingFile(files);

        // 保存 PDF 文件到服务器上
        String processedFile = FileSavingUtil.savingFile(pdfData, "pdf");

        // 保存文件名和路径到数据库
        mapper.addConvertFile(new ConvertFile(originalFile, processedFile));

        // 返回文件实体类
        return new ConvertFile(originalFile, processedFile);
    }
}
