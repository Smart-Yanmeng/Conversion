package com.ruoyi.web.controller.system;

import com.itextpdf.text.DocumentException;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.service.IPPTAndPDFService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class PPTAndPDFTest {
    @Autowired
    private IPPTAndPDFService pptAndPdfService;
    @Autowired
    private ServerConfig serverConfig;
    @Test
    public void CovertPdfToPpt(){
        String inPath = "/profile/upload/2023/08/11/SpringBoot3教程_20230811111146A001.pdf";
        String outPath;
        String filePath = RuoYiConfig.getUploadPath() + inPath.replace("/profile/upload", "");
        pptAndPdfService.covertPDFToPPT(filePath, null);
        String fileName = inPath.replace(".pdf", ".pptx");
        System.out.println(fileName);
        String url = serverConfig.getUrl();
        System.out.println(url);
        System.out.println(url + fileName);
    }
    @Test
    public void CovertPptToPdf() throws DocumentException, IOException {
        String inPath = "C:\\Users\\jiuxi\\Desktop\\项目介绍.pptx";
        String outPath = "C:\\Users\\jiuxi\\Desktop";
        System.out.println(pptAndPdfService.covertPPTToPDF(inPath, outPath));
    }
    @Test
    public void CovertPptToPdf2(){
        String inPath = "C:\\Users\\jiuxi\\Desktop\\项目介绍.pptx";
        String outPath = "C:\\Users\\jiuxi\\Desktop";
        System.out.println(pptAndPdfService.covertPPTToPDF2(inPath, outPath));
    }

}
