package com.guli.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.*;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.vod.service.VideoService;
import com.guli.vod.util.AliyunVodSDKUtils;
import com.guli.vod.util.ConstantPropertiesUtil;
import com.guli.vod.util.DeleteVideoUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.guli.vod.util.AliyunVodSDKUtils.initVodClient;

@Service
public class VideoServiceImpl implements VideoService {
    @Override
    public String uploadVideo(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String title = fileName.substring(0,fileName.lastIndexOf("."));
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }

        UploadStreamRequest request = new UploadStreamRequest(
                ConstantPropertiesUtil.ACCESS_KEY_ID,
                ConstantPropertiesUtil.ACCESS_KEY_SECRET,
                title,
                fileName,
                inputStream);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        String videoId = response.getVideoId();
        if (!response.isSuccess()){

            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
             if (videoId == null){
                 throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
             }
        }

        return videoId;
    }

    @Override
    public void removeVideo(String videoId) {

        DeleteVideoResponse response = null;
        DefaultAcsClient client = null;
        try {
            client = initVodClient(ConstantPropertiesUtil.ACCESS_KEY_ID, ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            response = new DeleteVideoResponse();

            response = DeleteVideoUtil.deleteVideo(videoId,client);
            System.out.print("RequestId = " + response.getRequestId() + "\n");
        } catch (Exception e) {
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }

    }

    @Override
    public CreateUploadVideoResponse getUploadAuthAndAddress(String title, String fileName) {

        try {
            //初始化
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            //创建请求对象
            CreateUploadVideoRequest request = new CreateUploadVideoRequest();
            request.setTitle(title);
            request.setFileName(fileName);

            //获取响应
            CreateUploadVideoResponse response = client.getAcsResponse(request);

            return response;

        } catch (ClientException e) {
            throw new GuliException(ResultCodeEnum.REFRESH_VIDEO_PLAYAUTH_ERROR);
        }
    }

    @Override
    public RefreshUploadVideoResponse refreshUploadAuthAndAddress(String videoId) {

        try {
            //初始化
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            //创建请求对象
            RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
            request.setVideoId(videoId);

            //获取响应
            RefreshUploadVideoResponse response = client.getAcsResponse(request);

            return response;

        } catch (ClientException e) {
            throw new GuliException(ResultCodeEnum.REFRESH_VIDEO_PLAYAUTH_ERROR);
        }
    }

    @Override
    public String getVideoPlayAuth(String videoId) {
        DefaultAcsClient client = null;
        try {
            client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            //请求
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            request.setVideoId(videoId);

            //响应
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);

            //得到播放凭证
            String playAuth = response.getPlayAuth();

            return playAuth;
        } catch (ClientException e) {
            throw new GuliException(ResultCodeEnum.REFRESH_VIDEO_PLAYAUTH_ERROR);
        }
    }
}
