package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.service.IPngPdfConvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "PDF-PNG")
@RestController
@RequestMapping("/pngPdfConverter")
public class PngPdfConverterController {
    @Resource
    IPngPdfConvertService pngPdfConvertService;

    @Resource
    ServerConfig serverConfig;

    @ApiOperation("PDF 转为 PNG")
    @PostMapping("/convertToPng")
    public AjaxResult convertToPng(@RequestParam MultipartFile file) {
        try {
            return AjaxResult.success(serverConfig.getUrl() + pngPdfConvertService.pdfToPng(file).getProcessedFilePath());
        } catch (Exception e) {
            return AjaxResult.error("文件转换出现错误，错误详情为：" + e.getMessage());
        }
    }

    @ApiOperation("PNG 转为 PDF")
    @PostMapping("/convertToPdf")
    public AjaxResult convertToPdf(@RequestParam List<MultipartFile> files) {
        try {
            return AjaxResult.success(serverConfig.getUrl() + pngPdfConvertService.pngToPdf(files).getProcessedFilePath());
        } catch (Exception e) {
            return AjaxResult.error("文件转换出现错误，错误详情为：" + e.getMessage());
        }
    }
}
