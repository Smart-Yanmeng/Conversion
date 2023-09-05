package com.ruoyi.quartz.util;

import com.ruoyi.quartz.exception.EmptyFileException;
import com.ruoyi.quartz.exception.WrongFileException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PNG文件和PDF文件互转工具类
 */
public class PngPdfConverterUtil {
    /**
     * PDF文件转换为PNG文件
     *
     * @param file PDF文件
     * @return PNG文件字节码集合
     * @throws IOException *
     */
    public static List<byte[]> pdfToPng(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() == 0) throw new EmptyFileException();
        if (!Objects.requireNonNull(file.getOriginalFilename()).substring(Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".") + 1).equals("pdf"))
            throw new WrongFileException();

        List<byte[]> resourceList = new ArrayList<>();

        InputStream is = file.getInputStream();

        PDDocument document = PDDocument.load(is);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        int numPages = document.getNumberOfPages();

        for (int pageNum = 0; pageNum < numPages; pageNum++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageNum, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);

            resourceList.add(outputStream.toByteArray());
        }

        document.close();

        return resourceList;
    }

    /**
     * PNG文件转换为PDF文件
     *
     * @param files PNG文件集合
     * @return PDF文件字节码
     * @throws IOException *
     */
    public static byte[] pngToPdf(List<MultipartFile> files) throws IOException {
        boolean flag = false;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();

        for (MultipartFile file : files) {
            // 判断文件是否合规
            if (file.isEmpty() || file.getSize() == 0) throw new EmptyFileException();
            if (!Objects.requireNonNull(file.getOriginalFilename()).substring(Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".") + 1).equals("png"))
                continue;

            flag = true;

            InputStream is = file.getInputStream();

            BufferedImage image = ImageIO.read(is);
            PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
            }
        }

        if (!flag) throw new WrongFileException();

        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}
