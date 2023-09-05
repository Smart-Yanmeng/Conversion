package com.ruoyi.web.controller.system;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.snowflake.SnowFlake;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.Pathway;
import com.ruoyi.system.service.IMultipleToolsService;
import com.ruoyi.system.service.impl.MultipleToolsServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/compress")
@Api(tags = "PNG和PDF处理")
public class MultipleToolsController {
    @Autowired
    private IMultipleToolsService multipleToolsService;

    @Autowired
    private ServerConfig serverConfig;

    @ApiOperation("压缩PDF")
    @PostMapping("/CompressPDF")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "PDF文件"),
            @ApiImplicitParam(name = "level", value = "压缩程度（1~10）")
    })
    public AjaxResult compressPDF(MultipartFile file, Integer level)  {
        try {
            Pathway pathway = updateUrl(file,1);
            multipleToolsService.compressPDF(pathway.getSrcPath(),pathway.getDesPath(),pathway.getNewPath(),level);
            return new AjaxResult(200,"操作成功",pathway.getNewPath());
        }catch (IOException e){
            return AjaxResult.error("文件压缩出现异常：" + e.getMessage());
        }
    }

    @ApiOperation("压缩PNG")
    @PostMapping("/CompressPNG")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "PNG文件"),
            @ApiImplicitParam(name = "desFileSize", value = "压缩程度（压缩后图片大小KB）")
    })
    public AjaxResult compressPNG(MultipartFile file,long desFileSize) {
        try {
            Pathway pathway = updateUrl(file,3);
            multipleToolsService.compressPNG(pathway.getSrcPath(),pathway.getDesPath(),pathway.getNewPath(),desFileSize);
            return new AjaxResult(200,"操作成功",pathway.getNewPath());
        }catch (IOException e){
            return AjaxResult.error("文件压缩出现异常：" + e.getMessage());
        }
    }
    @ApiOperation("PDF添加水印")
    @PostMapping("/addWatermark")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "PDF文件"),
            @ApiImplicitParam(name = "text", value = "文案"),
            @ApiImplicitParam(name = "fontSize", value = "大小")
    })
    public AjaxResult addWatermark(MultipartFile file, String text,float fontSize)  {
        try {
            Pathway pathway = updateUrl(file,1);
            multipleToolsService.addWatermark(pathway.getSrcPath(),pathway.getDesPath(),pathway.getNewPath(),text,fontSize);
            return new AjaxResult(200,"操作成功",pathway.getNewPath());
        }catch (IOException e){
            return AjaxResult.error("文件添加水印出现异常：" + e.getMessage());
        }
    }
    @ApiOperation("PNG转Text")
    @PostMapping("/PngtoText")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "PNG文件"),
            @ApiImplicitParam(name = "language", value = "图片语种")
    })
    public AjaxResult saveTextToFile(MultipartFile file, String language)  {
        if (!(language.equals("中文") || language.equals("英文"))){
            return AjaxResult.error("暂不支持："+ language);
        }
        try {
            Pathway pathway = updateUrl(file,2);
            multipleToolsService.saveTextToFile(pathway.getSrcPath(),pathway.getDesPath(),pathway.getNewPath(),language);
            return new AjaxResult(200,"操作成功",pathway.getNewPath());
        }catch (IOException | TesseractException e){
            return AjaxResult.error("文件转换出现异常：" + e.getMessage());
        }
    }

    /**
     * 传入文件返回所需路径
     *
     * @param file 文件
     * @param s 类型
     * @return 所需路径
     * @throws IOException
     */
    public Pathway updateUrl(MultipartFile file,int s) throws IOException {
        // 上传文件路径
        String filePath = RuoYiConfig.getUploadPath();
        // 上传并返回新文件名称
        String fileName = FileUploadUtils.upload(filePath, file);
        String url = serverConfig.getUrl() + fileName;
        //保存到本地
        String srcPath = filePath + fileName.substring(15,fileName.length());
        long id = SnowFlake.nextId();
        String newPath = "";
        String[] arr1 = url.split("/");
        arr1[arr1.length - 1] = id + "";
        for (int i = 0 ;i < arr1.length - 1;i++){
            newPath = newPath +arr1[i] + "/" ;
        }
        newPath = newPath + id + type(s);
        String desPath = "";
        String[] arr2 = srcPath.split("/");
        arr2[arr2.length - 1] = id + "";
        for (int i = 0 ;i < arr2.length - 1;i++){
            desPath = desPath  +arr2[i]+ "/" ;
        }
        desPath = desPath + id + type(s);
        return new Pathway(srcPath,desPath,newPath);
    }
    public String type(int rs){
        if (rs == 1){
            return ".pdf";
        }else if (rs == 2){
            return ".text";
        }else {
            return ".png";
        }
    }
}
