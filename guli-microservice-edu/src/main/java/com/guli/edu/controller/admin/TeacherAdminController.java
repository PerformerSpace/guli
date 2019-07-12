package com.guli.edu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.common.vo.R;
import com.guli.edu.entity.Teacher;
import com.guli.edu.query.TeacherQuery;
import com.guli.edu.service.TeacherService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "讲师管理")
@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/edu/teacher")
public class TeacherAdminController {

    @Autowired
    private TeacherService teacherService;
    @GetMapping
    @ApiOperation(value = "所有讲师列表")
    public R list(){

        List<Teacher> list = teacherService.list(null);
        System.out.println(list.get(0));
        return R.ok().data("items",list);

    }

    @ApiOperation(value = "根据ID删除讲师")
    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "id",value = "讲师ID",required = true,paramType = "path")
    public R removeById(@PathVariable(value = "id") String id){

      teacherService.removeById(id);
      return R.ok();
    }

    /*@ApiOperation(value = "查询分页信息")
    @GetMapping("/{page}/{limit}")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "page",value = "当前页码",required = true,paramType = "path"),
                    @ApiImplicitParam(name = "limit",value = "每页记录数",required = true,paramType = "path")}
    )
    public R pageList(@PathVariable Long page,
                      @PathVariable Long limit){
        Page<Teacher> teacherPage = new Page<>(page,limit);
        teacherService.page(teacherPage,null);
        List<Teacher> records = teacherPage.getRecords();
        long total = teacherPage.getTotal();
        return R.ok().data("rows",records).data("total",total);

    }*/
    @ApiOperation(value = "分页讲师列表")
    @GetMapping("/{page}/{limit}")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "page",value = "当前页码",required = true,paramType = "path"),
             @ApiImplicitParam(name = "limit",value = "每页记录数",required = true,paramType = "path")}
    )
    public R pageList(@PathVariable Long page,
                      @PathVariable Long limit,
                      TeacherQuery teacherQuery){
        Page<Teacher> teacherPage = new Page<>(page,limit);
        teacherService.pageQuery(teacherPage,teacherQuery);
        List<Teacher> records = teacherPage.getRecords();
        long total = teacherPage.getTotal();
        return R.ok().data("rows",records).data("total",total);

    }

    @ApiOperation(value = "新增讲师")
    @PostMapping
    @ApiImplicitParam(name = "teacher",value = "讲师对象",required = true)
    public R saveTeacher(@RequestBody Teacher teacher){

        teacherService.save(teacher);
        return R.ok();
    }

    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping("/{id}")
    @ApiImplicitParam(name = "id",value = "讲师ID",required = true,paramType = "path")
    public R getTeacherById(@PathVariable String id){

        Teacher teacher = teacherService.getById(id);
        return R.ok().data("item",teacher);
    }

    @ApiOperation(value = "根据ID修改讲师")
    @PostMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "讲师ID",required = true,paramType = "path"),
            @ApiImplicitParam(name = "teacher",value = "讲师对象",required = true)
    })
    public R updateTeacherById(@PathVariable String id,
                               @RequestBody Teacher teacher){
        teacher.setId(id);
        teacherService.updateById(teacher);
        return R.ok();

    }

}
