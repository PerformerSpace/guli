package com.guli.aliyunoss.controller;


import com.guli.aliyunoss.service.FileService;
import com.guli.aliyunoss.util.ConstantPropertiesUtil;
import com.guli.common.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(description = "阿里云文件管理")
@CrossOrigin  //跨域
@RequestMapping("/admin/oss/file")
public class FileUploadController {

    @Autowired
   private FileService fileService;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public R upload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(name = "host", value = "文件上传路径", required = false)
            @RequestParam(value = "host",required = false) String host){

        if (!StringUtils.isEmpty(host)){
             ConstantPropertiesUtil.FILE_HOST = host;
        }

        String url = fileService.upload(file);


        return R.ok().message("文件上传成功").data("url" , url);

    }


}
