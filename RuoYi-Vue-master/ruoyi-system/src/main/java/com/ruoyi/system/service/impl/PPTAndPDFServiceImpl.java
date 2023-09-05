package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.IPPTAndPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import com.itextpdf.text.DocumentException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.sl.usermodel.*;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.xslf.usermodel.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Image;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import cn.hutool.core.util.StrUtil;

import com.aspose.pdf.PptxSaveOptions;
import java.io.IOException;
import java.nio.file.Paths;
@Service
public class PPTAndPDFServiceImpl implements IPPTAndPDFService {
    @Autowired
    private ConvertFileMapper pptAndPDFMapper;

    /**
    *
    * */
    @Override
    public String covertPDFToPPT(String inPath, String outPath){
        String fileType = FileUtils.judgeFiletype(inPath);
        if (!fileType.equals("pdf")){
//            throw new RuntimeException("暂不支持该" + fileType + "文件类型");
            return "暂不支持该" + fileType + "文件类型";
        }
        String fileName = FileUtils.getNameNotSuffix(inPath);
        if (outPath == null){
            outPath = FileUtils.getFilePath(inPath);
        }
        String pdfDocumentFileName = Paths.get(outPath, fileName + ".pdf").toString();
        String pptxDocumentFileName = Paths.get(outPath, fileName + ".pptx").toString();
        // 下载 PDF文件
        com.aspose.pdf.Document doc = new com.aspose.pdf.Document(pdfDocumentFileName);
        // 实例化 PptxSaveOptions 实例
        PptxSaveOptions pptx_save = new PptxSaveOptions();
        // 将输出保存为 PPTX 格式
        pptx_save.setSlidesAsImages(true);
        doc.save(pptxDocumentFileName, pptx_save);
        return outPath + ".pptx";
    }

    @Override
    public String covertPPTToPDF(String inPath, String outPath) throws IOException, DocumentException {
        String fileType = FileUtils.judgeFiletype(inPath);
        if (!fileType.equals("ppt") && !fileType.equals("pptx")){
//            throw new RuntimeException("暂不支持该" + fileType + "文件类型");
            return "暂不支持该" + fileType + "文件类型";
        }
        if (outPath == null){
            outPath = FileUtils.getFilePath(inPath);
        }
        List<BufferedImage> images = pptToBufferedImages(inPath);
        if(CollectionUtils.isEmpty(images)){
            return "转换失败";
        }
        outPath = FileUtils.getNewFileFullPath(inPath, outPath, "pdf");
        try (FileOutputStream fileOutputStream = new FileOutputStream(outPath)){
            BufferedImage firstImage = images.get(0);
            com.itextpdf.text.Rectangle rectangle = new com.itextpdf.text.Rectangle((float) firstImage.getWidth(), (float) firstImage.getHeight());
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(rectangle, 0, 0, 0, 0);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            for (BufferedImage bufferedImage : images) {
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(bufferedImage, null);
                //image.scaleToFit((float) image.getWidth(), (float) image.getHeight());
                document.add(image);
                document.newPage();
            }
            document.close();
            pdfWriter.close();
        }
        return outPath;
    }

    @Override
    public String covertPPTToPDF2(String inPath, String outPath) {
        String fileType = FileUtils.judgeFiletype(inPath);
        if (!fileType.equals("ppt") && !fileType.equals("pptx")){
//            throw new RuntimeException("暂不支持该" + fileType + "文件类型");
            return "暂不支持该" + fileType + "文件类型";
        }
        if (StrUtil.isEmpty(inPath)) {
//            throw new RuntimeException("word文档路径不能为空");
            return "word文档路径不能为空";
        }
        if (outPath == null){
            outPath = FileUtils.getFilePath(inPath);
        }
/*        if (StrUtil.isEmpty(outPath)) {
            throw new RuntimeException("pdf目录不能为空");
        }*/
        String pdfPath = outPath + StrUtil.sub(inPath, inPath.lastIndexOf(StrUtil.BACKSLASH), inPath.lastIndexOf(StrUtil.DOT)) + StrUtil.DOT + "pdf";
        com.itextpdf.text.Document document = null;
        XMLSlideShow slideShow = null;
        FileOutputStream fileOutputStream = null;
        PdfWriter pdfWriter = null;
        try {
            FileInputStream is = new FileInputStream(inPath);
            slideShow = new XMLSlideShow(is);
            Dimension dimension = slideShow.getPageSize();
            fileOutputStream = new FileOutputStream(pdfPath);
            document = new com.itextpdf.text.Document();
            pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            PdfPTable pdfPTable = new PdfPTable(1);
            List<XSLFSlide> slideList = slideShow.getSlides();
            for (int i = 0, row = slideList.size(); i < row; i++) {
                XSLFSlide slide = slideList.get(i);
//                // 设置字体, 解决中文乱码
//                for (XSLFShape shape : slide.getShapes()) {
//                    XSLFTextShape textShape = (XSLFTextShape) shape;
//
//                    for (XSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
//                        for (XSLFTextRun textRun : textParagraph.getTextRuns()) {
//                            textRun.setFontFamily("宋体");
//                        }
//                    }
//                }
                BufferedImage bufferedImage = new BufferedImage((int)dimension.getWidth(), (int)dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2d = bufferedImage.createGraphics();
                graphics2d.setPaint(Color.white);
                graphics2d.setFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 12));
                slide.draw(graphics2d);
                graphics2d.dispose();
                Image image = Image.getInstance(bufferedImage, null);
                image.scalePercent(50f);
                // 写入单元格
                pdfPTable.addCell(new PdfPCell(image, true));
                document.add(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "转换失败";
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (pdfWriter != null) {
                    pdfWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outPath + ".pdf";
    }

    private static List<BufferedImage> pptToBufferedImages(String pptPath) {
        List<BufferedImage> images = new ArrayList<>();
        try (SlideShow<?, ?> slideShow = SlideShowFactory.create(new File(pptPath));) {
            Dimension dimension = slideShow.getPageSize();
            for (Slide<?, ?> slide : slideShow.getSlides()) {
                // 设置字体, 解决中文乱码
                setPPTFont(slide, "宋体");
                BufferedImage bufferedImage = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2d = bufferedImage.createGraphics();
                graphics2d.setPaint(Color.white);
                graphics2d.setFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 12));
                slide.draw(graphics2d);
                graphics2d.dispose();
                images.add(bufferedImage);
            }
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //设置ppt字体
    private static void setPPTFont(Slide<?, ?> slide, String fontFamily) {
        // 设置字体, 解决中文乱码
        for (Shape<?, ?> shape : slide.getShapes()) {
            if (shape instanceof TextShape) {
                TextShape textShape = (TextShape) shape;
                List<TextParagraph> textParagraphs = textShape.getTextParagraphs();
                for (TextParagraph textParagraph : textParagraphs) {
                    List<TextRun> textRuns = textParagraph.getTextRuns();
                    for (TextRun textRun : textRuns) {
                        textRun.setFontFamily(fontFamily);
                    }
                }
            }
        }
    }
}
