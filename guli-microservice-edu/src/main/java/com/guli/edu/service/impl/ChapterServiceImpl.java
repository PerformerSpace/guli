package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.edu.entity.Chapter;
import com.guli.edu.entity.Video;
import com.guli.edu.mapper.ChapterMapper;
import com.guli.edu.mapper.VideoMapper;
import com.guli.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.ChapterVo;
import com.guli.edu.vo.VideoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author lyy
 * @since 2019-07-12
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public void removeChapterById(String id) {
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("chapter_id", id);
        videoMapper.delete(videoQueryWrapper);

        //根据章节id删除章节
        baseMapper.deleteById(id);

    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {
        List<ChapterVo> chapterVoArrayList = new ArrayList<>();

        //获取章节信息
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", courseId);
        chapterQueryWrapper.orderByAsc("sort", "id");
        List<Chapter> chapters = baseMapper.selectList(chapterQueryWrapper);

        //获取课时信息
        QueryWrapper<Video> queryWrapperVideo = new QueryWrapper<>();
        queryWrapperVideo.eq("course_id", courseId);
        queryWrapperVideo.orderByAsc("sort", "id");
        List<Video> videos = videoMapper.selectList(queryWrapperVideo);

        if (chapters.size()>0){
            for (int i = 0; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);

                //创建章节vo对象
                ChapterVo chapterVo = new ChapterVo();
                BeanUtils.copyProperties(chapter, chapterVo);
                chapterVoArrayList.add(chapterVo);

                //填充视频vo数据
                ArrayList<VideoVo> videoVoArrayList = new ArrayList<>();
                for (int j = 0; j < videos.size(); j++) {

                    Video video = videos.get(j);
                    if(chapter.getId().equals(video.getChapterId())){

                        //创建视频vo对象
                        VideoVo videoVo = new VideoVo();
                        BeanUtils.copyProperties(video, videoVo);
                        videoVoArrayList.add(videoVo);
                    }
                }
                chapterVo.setChildren(videoVoArrayList);

            }
        }

        return chapterVoArrayList;
    }
}
