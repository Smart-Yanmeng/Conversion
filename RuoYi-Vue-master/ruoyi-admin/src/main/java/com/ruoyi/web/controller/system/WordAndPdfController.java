package com.ruoyi.web.controller.system;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.ConvertFile;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.impl.WordAndPdfServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(tags = "word和pdf转换")
@RequestMapping("/wordAndPdf")
public class WordAndPdfController {

    @Resource
    private WordAndPdfServiceImpl wordAndPdfService;

    @Resource
    private ConvertFileMapper mapper;

    @Resource
    private ServerConfig serverConfig;

    /**
     * word转pdf
     */
    @PostMapping("/wordToPdf")
    @ApiOperation("word转pdf")
    public AjaxResult wordToPdf(MultipartFile file) {
        try {
            List<String> list = getPath(file);
            wordAndPdfService.docx4jWordToPdf(list.get(2));
            mapper.addConvertFile(new ConvertFile(list.get(2), transfer(list.get(3))));
            return AjaxResult.success(list.get(3).substring(0, list.get(3).lastIndexOf(".")) + ".pdf");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件转换出现异常：" + e.getMessage());
        }
    }

    /**
     * pdf转word
     */
    @PostMapping("/pdfToWord")
    @ApiOperation("pdf转docx")
    public AjaxResult pdfToWord(MultipartFile file) {
        try {
            List<String> list = getPath(file);
            wordAndPdfService.spirePdfToWord(list.get(2));
            mapper.addConvertFile(new ConvertFile(list.get(2), transfer(list.get(3))));
            return AjaxResult.success(list.get(3).substring(0, list.get(3).lastIndexOf(".")) + ".docx");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件转换出现异常：" + e.getMessage());
        }
    }

    /**
     * doc转docx
     */
    @PostMapping("/docToDocx")
    @ApiOperation("doc转docx")
    public AjaxResult docToDocx(MultipartFile file) {
        try {
            List<String> list = getPath(file);
            wordAndPdfService.spireDocToDocx(list.get(2));
            mapper.addConvertFile(new ConvertFile(list.get(2), transferWord(list.get(3))));
            return AjaxResult.success(list.get(3).substring(0, list.get(3).lastIndexOf(".")) + ".docx");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件转换出现异常：" + e.getMessage());
        }
    }

    /**
     * docx转doc
     */
    @PostMapping("/docxToDoc")
    @ApiOperation("docx转doc")
    public AjaxResult docxToDoc(MultipartFile file) {
        try {
            List<String> list = getPath(file);
            wordAndPdfService.spireDocxToDoc(list.get(2));
            mapper.addConvertFile(new ConvertFile(list.get(2), transferWord(list.get(3))));
            return AjaxResult.success(list.get(3).substring(0, list.get(3).lastIndexOf(".")) + ".doc");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件转换出现异常：" + e.getMessage());
        }
    }


    /**
     * 获取路径
     */
    public List<String> getPath(MultipartFile file) throws IOException {
        String filePath = RuoYiConfig.getUploadPath(); //ruoYi上传文件路径
        String fileName = FileUploadUtils.upload(filePath, file); //文件新路径
        String source = filePath + fileName.substring(15); //文件源路径
        String returnPath = serverConfig.getUrl() + fileName; //文件返回路径
        for (String s : Arrays.asList(filePath, fileName, source, returnPath))
            System.out.println(s);
        return Arrays.asList(filePath, fileName, source, returnPath);
    }

    /**
     * 转换文件后缀pdf和docx
     */
    public String transfer(String path) {
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        if ("pdf".equals(suffix))
            return path.substring(0, path.lastIndexOf(".")) + ".docx";
        else if ("docx".equals(suffix)||"doc".equals(suffix))
            return path.substring(0, path.lastIndexOf(".")) + ".pdf";
        else
            return "该文件格式不支持转换";
    }

    /**
     * 转换文件后缀doc和docx
     */
    public String transferWord(String path) {
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        if ("doc".equals(suffix))
            return path.substring(0, path.lastIndexOf(".")) + ".docx";
        else if ("docx".equals(suffix))
            return path.substring(0, path.lastIndexOf(".")) + ".doc";
        else
            return "该文件格式不支持转换";
    }
}
