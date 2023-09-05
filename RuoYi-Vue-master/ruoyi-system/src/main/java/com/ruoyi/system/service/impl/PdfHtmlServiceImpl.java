package com.ruoyi.system.service.impl;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.ruoyi.system.domain.ConvertFile;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.IPdfHtmlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


@Slf4j
@Service
public class PdfHtmlServiceImpl implements IPdfHtmlService {

    @Resource
    private ConvertFileMapper convertFileMapper;

    @Resource
    private ConvertFileMapper mapper;


    public static BufferedImage pdfStreamToPng(InputStream pdfFileInputStream) {
        PDDocument doc = null;
        PDFRenderer renderer = null;
        try {
            doc = PDDocument.load(pdfFileInputStream);
            renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            BufferedImage image = null;
            for (int i = 0; i < pageCount; i++) {
                if (image != null) {
                    image = combineBufferedImages(image, renderer.renderImageWithDPI(i, 144));
                }

                if (i == 0) {
                    image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                }
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图

            }
            return combineBufferedImages(image);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (doc != null) {
                    doc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getHtmlString(String base64, String title) {
        StringBuilder stringHtml = new StringBuilder();
        // 输入HTML文件内容
        stringHtml.append("<html><head>");
        stringHtml.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        stringHtml.append("<title>").append(title).append("</title>");
        stringHtml.append("</head>");
        stringHtml.append("<body style=\"" + "text-align: center;\">");
        stringHtml.append("<img src=\"data:image/png;base64,").append(base64).append("\"/>");
        stringHtml.append("</body></html>");
        return stringHtml.toString();
    }

    public static void createHtmlByBase64(String base64, String htmlPath, String title) {
        PrintStream printStream = null;
        try {
            // 打开文件
            printStream = new PrintStream(new FileOutputStream(htmlPath));
        } catch (FileNotFoundException e) {
            log.error("create file error!", e);
            return;
        }
        try {
            // 将HTML文件内容写入文件中
            String htmlString = getHtmlString(base64, title);
            printStream.println(htmlString);
        } catch (Exception e) {
            log.error("createHtmlByBase64 error!", e);
        } finally {
            printStream.close();
        }

    }

    public static BufferedImage combineBufferedImages(BufferedImage... images) {
        int height = 0;
        int width = 0;
        for (BufferedImage image : images) {
            //height += Math.max(height, image.getHeight());
            height += image.getHeight();
            width = image.getWidth();
        }
        BufferedImage combo = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = combo.createGraphics();
        int x = 0;
        int y = 0;
        for (BufferedImage image : images) {
            //int y = (height - image.getHeight()) / 2;
            g2.setStroke(new BasicStroke(2.0f));// 线条粗细
            g2.setColor(new Color(193, 193, 193));// 线条颜色
            g2.drawLine(x, y, width, y);// 线条起点及终点位置

            g2.drawImage(image, x, y, null);
            //x += image.getWidth();
            y += image.getHeight();

        }
        return combo;
    }

    public static String bufferedImageToBase64(BufferedImage bufferedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  //它继承自OutputStream抽象类，并提供了一种方便的方式来将数据写入到字节数组中
        String png_base64 = "";
        try {
            //ImageIO提供了一种方便的方式来读取和写入图像数据，支持多种图像格式
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);// 写入流中
            byte[] bytes = byteArrayOutputStream.toByteArray();// 转换成字节
            BASE64Encoder encoder = new BASE64Encoder(); //BASE64Encoder用于将数据编码为Base64字符串
            // 转换成base64串 删除 \r\n
            png_base64 = encoder.encodeBuffer(bytes).trim()
                    .replaceAll("\n", "")
                    .replaceAll("\r", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return png_base64;
    }


    /**
     *
     * @param pdfurl pdf传入路径
     * @param keepurl html保存路径
     * @return
     * @throws Exception 文件转换失败
     */
    public void pdfToHtml(String pdfurl, String keepurl,java.util.List<String> paths) throws Exception {

        File file = new File(pdfurl);
        String htmlPath = keepurl;  //保存html路径
        InputStream inputStream = null;
        BufferedImage bufferedImage = null;  //用于表示图像数据，通过使用BufferedImage类，可以进行一系列的图像操作
        try {
            inputStream = new FileInputStream(file);
            bufferedImage = pdfStreamToPng(inputStream);  //合并图
            String base64_png = bufferedImageToBase64(bufferedImage);  //编码
            createHtmlByBase64(base64_png, htmlPath, "HTML文件"); //编写html代码并且写入文件

            //保存数据到数据库
            mapper.addConvertFile(new ConvertFile(paths.get(2),paths.get(3).replace("pdf","html")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param htmlurl html文件上传路径
     * @param pdfurl  pdf文件保存路径
     * @return pdf文件
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void htmlToPdf(String htmlurl, String pdfurl,java.util.List<String> paths) {
        try {
            // 读取HTML文件
            File htmlFile = new File(htmlurl);
            System.out.println("htmlFile  " + htmlurl);
            FileInputStream fis = new FileInputStream(htmlFile);

            // 创建PDF文件
            File pdfFile = new File(pdfurl);
            FileOutputStream fos = new FileOutputStream(pdfFile);

            // 创建自定义样式
            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider();
            fontProvider.addDirectory("C:\\Windows\\Fonts"); // 指定字体文件目录
            converterProperties.setFontProvider(fontProvider);

            // 将HTML转换为PDF
            HtmlConverter.convertToPdf(fis, fos, converterProperties);

            // 关闭流
            fis.close();
            fos.close();

            //存入数据库
            mapper.addConvertFile(new ConvertFile(paths.get(2),paths.get(3).replace("pdf","html")));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
