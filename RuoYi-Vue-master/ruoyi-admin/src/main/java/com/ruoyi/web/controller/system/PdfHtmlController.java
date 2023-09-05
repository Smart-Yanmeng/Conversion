package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.IPdfHtmlService;
import com.ruoyi.web.controller.tool.FileTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "PDF-HTML")
@RestController
@RequestMapping("/files")
public class PdfHtmlController {

    @Resource
    private IPdfHtmlService pdfHtmlService;

    @Resource
    private ConvertFileMapper mapper;

    @Resource
    private FileTool fileTool;

    @ApiOperation("PDF转网页")
    @PostMapping("/pdf-html")
    public AjaxResult pdfToHtml(MultipartFile file) {
        try {

            // 获取文件信息
            List<String> paths = fileTool.getPaths(file);

            //判断是不是pdf文件
            if (!file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).equals("pdf")) return AjaxResult.error(403,"该文件不是PDF文件");

            //文件转换  param(PDF传入路径，HTML保存路径)
            pdfHtmlService.pdfToHtml(paths.get(2), paths.get(2).replace("pdf","html"),paths);

            //返回HTML的http地址
            return AjaxResult.success(paths.get(3).replace("pdf","html"));
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件转换失败" + e.getMessage());
        }
    }

    @ApiOperation("网页转PDF")
    @PostMapping("/html-pdf")
    public AjaxResult htmlToPdf(MultipartFile file) throws Exception {
        try {

            //获取文件信息
            List<String> paths = fileTool.getPaths(file);

            //判断是不是html文件
            if (!file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).equals("html")) return AjaxResult.error(403,"该文件不是PDF文件");

            //文件转换
            pdfHtmlService.htmlToPdf(paths.get(2), paths.get(2).replace("html","pdf"),paths);

            return AjaxResult.success(paths.get(3).replace("html","pdf"));
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("文件转换错误" + e.getMessage());
        }
    }

}
