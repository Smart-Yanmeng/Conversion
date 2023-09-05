package com.ruoyi.system.service;

import com.ruoyi.system.domain.ConvertFile;

import java.util.List;

public interface IConvertFileService {
    /**
     * 根据文件Id查询文件转换信息
     *
     * @param fileId 文件Id
     * @return 文件转换信息
     */
    public ConvertFile selectConvertFileByFileId(Long fileId);

    /**
     * 查询转换文件列表
     *
     * @return 文件转换信息列表
     */
    public List<ConvertFile> queryConvertFileList();

    /*
     * 增加转换文件
     * */
    /**
     * 根据文件Id查询文件转换信息
     *
     * @param convertFile 文件转换信息
     * @return 文件转换信息
     */
    public int addConvertFile(ConvertFile convertFile);

    /**
     * 删除转换文件信息
     *
     * @param fileId 文件Id
     * @return 文件转换信息
     */
    public int deleteConvertFile(Long fileId);

    /**
     * 批量删除转换文件信息
     *
     * @param fileIds 文件Id
     * @return 文件转换信息
     */
    public int deleteConvertFiles(Long[] fileIds);

    /**
     * 更改转换文件信息
     *
     * @param convertFile 文件转换信息
     * @return 文件转换信息
     */
    public int updateConvertFile(ConvertFile convertFile);
}
