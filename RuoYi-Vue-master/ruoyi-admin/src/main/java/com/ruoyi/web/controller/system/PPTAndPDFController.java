package com.ruoyi.web.controller.system;

import com.itextpdf.text.DocumentException;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.ConvertFile;
import com.ruoyi.system.service.IConvertFileService;
import com.ruoyi.system.service.IPPTAndPDFService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * PPT转PDF PDF转PPT
 *
 * @author YuFjxi
 */
@Api(tags = "PPT-PDF")
@RestController
@RequestMapping("/convert")
public class PPTAndPDFController extends BaseController {
    @Autowired
    private IPPTAndPDFService pptAndPdfService;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private IConvertFileService convertFileService;
    /**
     * PDF转PPT
     *
     * @param file 上传文件
     * @return 输出路径
     */
    @ApiOperation("PDF转PPT")
    @PostMapping("/pdfToPpt")
    public AjaxResult CovertPdfToPpt(MultipartFile file)
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            String inPath = (filePath + fileName.replace("/profile/upload", ""));
            pptAndPdfService.covertPDFToPPT(inPath, null);
            String outUrl = url.replace(".pdf", ".pptx");
            addConvertFile(url, outUrl);
            return success(outUrl);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
    @PostMapping("/pathPdfToPpt")
    public AjaxResult CovertPdfToPpt(String inPath, String outPath){
        String filePath = RuoYiConfig.getUploadPath() + inPath.replace("/profile/upload", "");
        pptAndPdfService.covertPDFToPPT(filePath, outPath);
        String fileName = inPath.replace(".pdf", ".pptx");
        String outUrl = serverConfig.getUrl() + fileName;
        addConvertFile(serverConfig.getUrl() + inPath, outUrl);
        return success(outUrl);
    }

    /**
     * PPT转PDF
     *
     * @param file 上传文件
     * @return 输出路径
     */
    @ApiOperation("PPT转PDF接口1")
    @PostMapping("/pptToPdf")
    public AjaxResult CovertPptToPdf (MultipartFile file)
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            String inPath = (filePath + fileName.replace("/profile/upload", ""));
            pptAndPdfService.covertPPTToPDF(inPath, null);
            String outUrl = url.replace(".pptx", ".pdf").replace(".ppt", ".pdf");
            addConvertFile(url, outUrl);
            return success(outUrl);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
    @PostMapping("/pathPptToPdf")
    public AjaxResult CovertPptToPdf (String inPath, String outPath) throws DocumentException, IOException {
        String filePath = RuoYiConfig.getUploadPath() + inPath.replace("/profile/upload", "");
        pptAndPdfService.covertPPTToPDF(filePath, outPath);
        String fileName = inPath.replace(".pptx", ".pdf").replace(".ppt", ".pdf");
        String outUrl = serverConfig.getUrl() + fileName;
        addConvertFile(serverConfig.getUrl() + inPath, outUrl);
        return success(outUrl);
    }

    /**
     * PPT转PDF
     *
     * @param file 上传文件
     * @return 输出路径
     */
    @ApiOperation("PPT转PDF接口2")
    @PostMapping("/pptToPdf2")
    public AjaxResult CovertPptToPdf2 (MultipartFile file) {
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            System.out.println(fileName);
            String url = serverConfig.getUrl() + fileName;
            String inPath = (filePath + fileName.replace("/profile/upload", ""));
            pptAndPdfService.covertPPTToPDF2(inPath, null);
            String outUrl = url.replace(".pptx", ".pdf").replace(".ppt", ".pdf");
            addConvertFile(url, outUrl);
            return success(outUrl);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
    @PostMapping("/pathPptToPdf2")
    public AjaxResult CovertPptToPdf2 (String inPath, String outPath){
        String filePath = RuoYiConfig.getUploadPath() + inPath.replace("/profile/upload", "");
        pptAndPdfService.covertPPTToPDF2(filePath, outPath);
        String fileName = inPath.replace(".pptx", ".pdf").replace(".ppt", ".pdf");
        String outUrl = serverConfig.getUrl() + fileName;
        addConvertFile(serverConfig.getUrl() + inPath, outUrl);
        return success(outUrl);
    }

    /*
    * 保存文件转换信息到数据库
    * */
    public int addConvertFile(String inUrl, String outUrl){
        ConvertFile convertFile = new ConvertFile();
        convertFile.setOriginalFilePath(inUrl);
        convertFile.setProcessedFilePath(outUrl);
        return  convertFileService.addConvertFile(convertFile);
    }
}
