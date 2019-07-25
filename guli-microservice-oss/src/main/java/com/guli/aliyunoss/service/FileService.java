package com.guli.aliyunoss.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 文件上传至阿里云
     * @param file 上传的文件
     * @return 返回文件的URL地址
     */
    String upload(MultipartFile file);
}
