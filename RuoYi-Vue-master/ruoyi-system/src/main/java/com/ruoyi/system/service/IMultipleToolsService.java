package com.ruoyi.system.service;

import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;

public interface IMultipleToolsService {
    /**
     * PDF压缩
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param level 压缩程度
     */
    public void compressPDF(String srcPath, String desPath,String newPath, int level);

    /**
     * 根据指定大小和指定精度压缩图片
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param desFileSize 图片大小
     */
    public void compressPNG(String srcPath, String desPath,String newPath,long desFileSize) throws IOException;
    /**
     * pdf添加水印
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param text 文字
     * @param fontSize 大小
     */
    public void addWatermark(String srcPath, String desPath,String newPath,String text,float fontSize) throws IOException;
    /**
     * 图片转文字
     *
     * @param srcPath 传入地址
     * @param desPath 传出地址
     * @param newPath 路径
     * @param language 语言
     */
    public void saveTextToFile(String srcPath, String desPath,String newPath, String language) throws IOException, TesseractException;
}
