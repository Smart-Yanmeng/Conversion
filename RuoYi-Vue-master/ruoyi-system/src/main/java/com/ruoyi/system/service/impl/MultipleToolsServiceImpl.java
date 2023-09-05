package com.ruoyi.system.service.impl;


import com.aspose.pdf.Document;
import com.aspose.pdf.optimization.OptimizationOptions;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.snowflake.SnowFlake;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.ConvertFile;
import com.ruoyi.system.domain.Pathway;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.IMultipleToolsService;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

@Service
public class MultipleToolsServiceImpl implements IMultipleToolsService {

    @Autowired
    private ConvertFileMapper convertFileMapper;

    /**
     * PDF压缩
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param level 压缩程度
     */
    @Override
    public void compressPDF(String srcPath, String desPath,String newPath, int level) {
        Document document = new Document(srcPath);
//        创建优化选项
        OptimizationOptions opt = new OptimizationOptions();
//        压缩成原来的百分之几
        opt.setImageQuality(level);
//        允许压缩图像
        opt.getImageCompressionOptions().setCompressImages(true);
//        设置图片的分辨率
        opt.getImageCompressionOptions().setMaxResolution(170);
//        启用图像调整大小
        opt.getImageCompressionOptions().setResizeImages(true);
//        保留所使用字体,其他删除
        opt.setSubsetFonts(true);
//        禁用重复的链接
        opt.setLinkDuplcateStreams(true);
//        移除未使用的流
        opt.setRemoveUnusedStreams(true);
//        删除未使用的对象
        opt.setRemoveUnusedObjects(true);
        document.optimizeResources(opt);
        document.save(desPath);
        ConvertFile convertFile = new ConvertFile(srcPath,newPath);
        convertFileMapper.addConvertFile(convertFile);
    }

    /**
     * 根据指定大小和指定精度压缩图片
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param desFileSize 图片大小
     */
    @Override
    public void compressPNG(String srcPath, String desPath,String newPath,long desFileSize) throws IOException {
        if (!new File(srcPath).exists()) {
            return ;
        }
        double accuracy = 0.7;
        File srcFile = new File(srcPath);
        long srcFileSize = srcFile.length();
        System.out.println("源图片：" + srcPath + "，大小：" + srcFileSize / 1024 + "kb");
        //获取图片信息
        BufferedImage bim = ImageIO.read(srcFile);
        int srcWidth = bim.getWidth();
        int srcHeight = bim.getHeight();

        //先转换成jpg
        Thumbnails.Builder<File> builder = Thumbnails.of(srcFile).outputFormat("jpg");

//            // 指定大小（宽或高超出会才会被缩放）
//            if (srcWidth > desMaxWidth || srcHeight > desMaxHeight) {
//                builder.size(desMaxWidth, desMaxHeight);
//            } else {
//                //宽高均小，指定原大小
//                builder.size(srcWidth, srcHeight);
//            }
        builder.size(srcWidth, srcHeight);

        // 写入到内存
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        builder.toOutputStream(bos);

        // 递归压缩，直到目标文件大小小于desFileSize
        byte[] bytes = compressPicCycle(bos.toByteArray(), desFileSize, accuracy);

        // 输出到文件
        File desFile = new File(desPath);
        FileOutputStream fos = new FileOutputStream(desFile);
        fos.write(bytes);
        fos.close();

        System.out.println("目标图片：" + desPath + "，大小" + desFile.length() / 1024 + "kb");
        System.out.println("图片压缩完成！");
        ConvertFile convertFile = new ConvertFile(srcPath,newPath);
        //保存到数据库
        convertFileMapper.addConvertFile(convertFile);
    }

    //递归压缩图片
    private byte[] compressPicCycle(byte[] bytes, long desFileSize, double accuracy) throws IOException {
        // File srcFileJPG = new File(desPath);
        long srcFileSizeJPG = bytes.length;
        // 2、判断大小，如果小于500kb，不压缩；如果大于等于500kb，压缩
        if (srcFileSizeJPG <= desFileSize * 1024) {
            return bytes;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int srcWidth = bim.getWidth();
        int srcHeight = bim.getHeight();
        int desWidth = new BigDecimal(srcWidth).multiply(new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(srcHeight).multiply(new BigDecimal(accuracy)).intValue();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        Thumbnails.of(new ByteArrayInputStream(bytes)).size(desWidth, desHeight).outputQuality(accuracy).toOutputStream(bos);
        return compressPicCycle(bos.toByteArray(), desFileSize, accuracy);
    }

    /**
     * pdf添加水印
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param text 文字
     * @param fontSize 大小
     */
    @Override
    public void addWatermark(String srcPath, String desPath,String newPath,String text,float fontSize) throws IOException {
        // 加载源 PDF 文档
        com.aspose.pdf.Document document = new com.aspose.pdf.Document(srcPath);

        // 创建文本对象
        com.aspose.pdf.TextStamp textStamp = new com.aspose.pdf.TextStamp(text);
        textStamp.setHorizontalAlignment(com.aspose.pdf.HorizontalAlignment.Center);
        textStamp.setVerticalAlignment(com.aspose.pdf.VerticalAlignment.Center);
        textStamp.setOpacity(0.2);
        textStamp.getTextState().setFontSize(fontSize);
        textStamp.getTextState().setFont(com.aspose.pdf.FontRepository.findFont("Arial"));

        //为每个页面添加水印
        for (int pageIndex = 1; pageIndex <= document.getPages().size(); pageIndex++) {
            com.aspose.pdf.Page page = document.getPages().get_Item(pageIndex);
            page.addStamp(textStamp);
        }

        // 保存修改后的 PDF 文档
        document.save(desPath);
        ConvertFile convertFile = new ConvertFile(srcPath,newPath);
        convertFileMapper.addConvertFile(convertFile);
    }

    /**
     * 图片转文字
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param language 语言
     */
    @Override
    public void saveTextToFile(String srcPath, String desPath,String newPath, String language) throws IOException, TesseractException {
        if (language.equals("中文")) {
            language = "chi_sim";
        } else {
            language = "eng";
        }
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(RuoYiConfig.getAddFile());
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_DEFAULT);
        tesseract.setLanguage(language);
        File srcFile = new File(srcPath);
        String result = tesseract.doOCR(srcFile);
        File desFile = new File(desPath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(desFile));
        writer.write(result);
        writer.close();
        ConvertFile convertFile = new ConvertFile(srcPath, newPath);
        convertFileMapper.addConvertFile(convertFile);
    }
}
