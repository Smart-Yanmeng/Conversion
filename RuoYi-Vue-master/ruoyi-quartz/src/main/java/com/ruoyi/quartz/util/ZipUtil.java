package com.ruoyi.quartz.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 将多个文件压缩到一个 ZIP 文件中
 */
public class ZipUtil {
    /**
     * 将多个文件压缩到一个 ZIP 文件中并返回字节流
     *
     * @param fileDataList 字节流集合
     * @return 字节流
     * @throws IOException *
     */
    public static byte[] packToZip(List<byte[]> fileDataList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (int i = 0; i < fileDataList.size(); i++) {
                String entryName = "page_" + (i + 1) + ".png";
                zipOutputStream.putNextEntry(new ZipEntry(entryName));
                zipOutputStream.write(fileDataList.get(i));
                zipOutputStream.closeEntry();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }
}
