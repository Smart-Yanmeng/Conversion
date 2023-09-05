package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ConvertFile;
import com.ruoyi.system.mapper.ConvertFileMapper;
import com.ruoyi.system.service.IConvertFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvertFileServiceImpl implements IConvertFileService {
    @Autowired
    private ConvertFileMapper convertFileMapper;

    @Override
    public ConvertFile selectConvertFileByFileId(Long fileId) {
        return convertFileMapper.selectConvertFileByFileId(fileId);
    }

    @Override
    public List<ConvertFile> queryConvertFileList() {
        return convertFileMapper.queryConvertFileList();
    }

    @Override
    public int addConvertFile(ConvertFile convertFile) {
        return convertFileMapper.addConvertFile(convertFile);
    }

    @Override
    public int deleteConvertFile(Long fileId) {
        return convertFileMapper.deleteConvertFile(fileId);
    }

    @Override
    public int deleteConvertFiles(Long[] fileIds) {
        return convertFileMapper.deleteConvertFiles(fileIds);
    }

    @Override
    public int updateConvertFile(ConvertFile convertFile) {
        return convertFileMapper.updateConvertFile(convertFile);
    }
}
