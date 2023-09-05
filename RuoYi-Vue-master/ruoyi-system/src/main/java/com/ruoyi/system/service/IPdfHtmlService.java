package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPdfHtmlService {

    public void pdfToHtml(String pdfurl, String keepurl, List<String> paths) throws Exception;

    public void htmlToPdf(String htmlurl,String pdfurl,List<String> paths);


}
