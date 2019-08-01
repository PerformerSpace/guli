package com.guli.vod.service;

import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    String uploadVideo(MultipartFile file);

    void removeVideo(String videoId);

    CreateUploadVideoResponse getUploadAuthAndAddress(String title, String fileName);

    RefreshUploadVideoResponse refreshUploadAuthAndAddress(String videoId);

    String getVideoPlayAuth(String videoId);
}
