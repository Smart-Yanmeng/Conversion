package com.ruoyi.system.service;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

public interface IPPTAndPDFService {
    /**
     * PDF转PPT
     *
     * @param inPath 输入路径
     * @param outPath 输出路径
     * @return 输出路径
     */
    public String covertPDFToPPT(String inPath, String outPath);

    /**
     * PPT转PDF
     *
     * @param inPath 输入路径
     * @param outPath 输出路径
     * @return 输出路径
     */
    public String covertPPTToPDF(String inPath, String outPath) throws IOException, DocumentException;

    /**
     * PPT转PDF
     *
     * @param inPath 输入路径
     * @param outPath 输出路径
     * @return 输出路径
     */
    public String covertPPTToPDF2(String inPath, String outPath);

}
