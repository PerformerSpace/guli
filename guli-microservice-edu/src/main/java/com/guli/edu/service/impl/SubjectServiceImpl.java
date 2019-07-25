package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guli.common.util.ExcelImportUtil;

import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.vo.SubjectVo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author lyy
 * @since 2019-07-12
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public List<SubjectNestedVo> nestedList() {
        List<SubjectNestedVo> subjectNestedVoArrayLis =   new ArrayList<>();

        //获取一级分类数据记录
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",0);
        wrapper.orderByAsc("sort","id");
        List<Subject> subjects = baseMapper.selectList(wrapper);

        //获取二级分类数据记录
        QueryWrapper<Subject> wrapper2 = new QueryWrapper<>();
        wrapper2.ne("parent_id",0);
        wrapper2.orderByAsc("sort","id");
        List<Subject> subjects2 = baseMapper.selectList(wrapper2);


        //填充一级分类vo数据
        int count1 = subjects.size();
        for (int i = 0; i < count1; i++) {
            Subject subject = subjects.get(i);
            //创建一级类别vo对象
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
            BeanUtils.copyProperties(subject,subjectNestedVo);
            subjectNestedVoArrayLis.add(subjectNestedVo);

            //填充二级分类vo数据
            ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
            int count2 = subjects2.size();
            for (int j = 0; j < count2; j++) {
                Subject subject2 = subjects2.get(j);
                if (subject2.getParentId().equals(subject.getId())){
                    //创建二级类别vo对象
                    SubjectVo subjectVo = new SubjectVo();
                    BeanUtils.copyProperties(subject2,subjectVo);
                    subjectVoArrayList.add(subjectVo);
                }
            }
            subjectNestedVo.setChildren(subjectVoArrayList);



        }

        return subjectNestedVoArrayLis;
    }


    @Override
    public List<String> batchImport(MultipartFile file) throws Exception {

        List<String> errorMsg = new ArrayList<>();

        //创建工具类
        ExcelImportUtil importUtil = new ExcelImportUtil(file.getInputStream());
        //获取工作表
        HSSFSheet sheet = importUtil.getSheet();
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows <= 1){

            errorMsg.add("表中数据为空，请填写数据~");
            return errorMsg;
        }

        for (int rowNum = 1; rowNum < rows; rowNum++) {
            HSSFRow row = sheet.getRow(rowNum);
            if (row != null){

                //获取一级分类
                String levelOneValue = "";
                Cell levelOneCell = row.getCell(0);
                if(levelOneCell != null){
                    levelOneValue = importUtil.getCellValue(levelOneCell).trim();
                    if (StringUtils.isEmpty(levelOneValue)) {
                        errorMsg.add("第" + rowNum + "行一级分类为空");
                        continue;
                    }
                }

                // 判断一级分类是否重复
                Subject subject = getByTitle(levelOneValue);
                String parentId = null;
                if (subject == null){

                Subject subjectLevelOne = new Subject();
                subjectLevelOne.setTitle(levelOneValue);
                subjectLevelOne.setSort(rowNum);
                baseMapper.insert(subjectLevelOne);
                parentId = subjectLevelOne.getId();

                }else{
                    parentId = subject.getId();
                }

                //获取二级分类
                //获取一级分类
                String levelTwoValue = "";
                Cell levelTwoCell = row.getCell(1);
                if(levelOneCell != null){
                    levelTwoValue = importUtil.getCellValue(levelTwoCell).trim();
                    if (StringUtils.isEmpty(levelOneValue)) {
                        errorMsg.add("第" + rowNum + "行二级分类为空");
                        continue;
                    }
                }

                //判断二级分类是否重复
                Subject subByTitle = getSubByTitle(levelTwoValue, parentId);
                if (subByTitle == null){
                    Subject subjectLevelTwo= new Subject();
                    subjectLevelTwo.setTitle(levelTwoValue);
                    subjectLevelTwo.setSort(rowNum);
                    subjectLevelTwo.setParentId(parentId);
                    //将二级分类存入数据库
                    baseMapper.insert(subjectLevelTwo);

                }




            }
        }



        return errorMsg;
    }


    /**
     * 根据分类名称查询这个一级分类中否存在
     * @param title
     * @return
     */
    private Subject getByTitle(String title){
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",title);
        wrapper.eq("parent_id","0");
        Subject subject = baseMapper.selectOne(wrapper);
        return subject;

    }

    /**
     * 根据分类名称查询这个二级分类中否存在
     * @param title
     * @return
     */
    private Subject getSubByTitle(String title,String parentId){
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",title);
        wrapper.eq("parent_id",parentId);
        Subject subject = baseMapper.selectOne(wrapper);
        return subject;

    }
}
