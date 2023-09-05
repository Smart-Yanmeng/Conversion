package com.ruoyi.system.service.impl;

import com.aspose.words.*;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class WordAndPdfServiceImpl {

    /**
     * word2pdf
     */
    //public void docx4jWordToPdf(String sourcePath) {
    //    try {
    //        WordprocessingMLPackage pkg = Docx4J.load(new File(sourcePath));
    //        Mapper fontMapper = new IdentityPlusMapper();
    //        //解决宋体（正文）和宋体（标题）的乱码问题
    //        PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
    //        //PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
    //        PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
    //        //PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
    //        PhysicalFonts.put("仿宋", PhysicalFonts.get("SimSun"));
    //        PhysicalFonts.put("黑体", PhysicalFonts.get("SimSun"));
    //        PhysicalFonts.put("等线", PhysicalFonts.get("SimSun"));
    //        //将字体替换为服务器中存在的字体
    //        fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
    //        fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
    //        fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
    //        fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
    //        fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
    //        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
    //        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
    //        fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
    //        fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
    //        fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
    //        fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
    //        fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
    //        fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
    //        fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
    //        fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
    //        fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
    //        fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
    //        fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
    //        fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
    //        fontMapper.put("仿宋_GB2312", PhysicalFonts.get("STFangsong"));
    //        //fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));
    //        PhysicalFont simsunFont = PhysicalFonts.get("SimSun");
    //        fontMapper.put("SimSun", simsunFont);
    //        //设置字体
    //        pkg.setFontMapper(fontMapper);
    //        String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".pdf";
    //        Docx4J.toPDF(pkg, new FileOutputStream(targetPath));
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //
    //}

    public void docx4jWordToPdf(String sourcePath) {
        String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".pdf";
        LoadOptions opts = new LoadOptions();
        opts.getLanguagePreferences().setDefaultEditingLanguage(EditingLanguage.CHINESE_PRC);
        Document doc = null;
        try {
            doc = new Document(sourcePath, opts);
            ParagraphFormat pf = doc.getStyles().getDefaultParagraphFormat();
            pf.clearFormatting();
            PdfSaveOptions options = new PdfSaveOptions();
            // 文字和图像压缩
            options.setExportDocumentStructure(true);
            options.setTextCompression(PdfTextCompression.FLATE);
            options.setImageCompression(PdfImageCompression.AUTO);
            // 接收修订
            doc.acceptAllRevisions();
            // 去掉批注
            NodeCollection nc = doc.getChildNodes(NodeType.COMMENT,true);
            if (nc != null && nc.getCount() > 0) {
                for(int i=0;i<nc.getCount();i++){
                    Node comment =nc.get(i);
                    comment.getParentNode().removeChild(comment);
                }
            }
            // 将Word另存为PDF
            doc.save(targetPath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * pdf2word
     */
    public void spirePdfToWord(String sourcePath) {
        com.spire.pdf.PdfDocument doc = new PdfDocument();
        //加载 PDF 文件
        doc.loadFromFile(sourcePath);
        //将 PDF 转换为流动形态的Word
        doc.getConvertOptions().setConvertToWordUsingFlow(true);
        //将PDF转换为Docx格式文件并保存
        String pfdName = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".docx";
        doc.saveToFile(pfdName, FileFormat.DOCX);
        doc.close();
    }

    /**
     * doc2docx
     */
    public void spireDocToDocx(String sourcePath) {
        try {
            String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".docx";
            File file = new File(targetPath);
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(sourcePath);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(os, SaveFormat.DOCX);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * docx2doc
     */
    public void spireDocxToDoc(String sourcePath) {
        try {
            String targetPath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".doc";
            File file = new File(targetPath);
            FileOutputStream os = new FileOutputStream(file);
            Document docx = new Document(sourcePath);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            docx.save(os, SaveFormat.DOC);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
