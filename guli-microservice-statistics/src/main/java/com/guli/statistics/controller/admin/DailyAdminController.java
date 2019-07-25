package com.guli.statistics.controller.admin;


import com.guli.common.vo.R;
import com.guli.statistics.service.DailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author lyy
 * @since 2019-07-20
 */
@CrossOrigin
@RestController
@RequestMapping("/admin/statistics/daily")

public class DailyAdminController {

    @Autowired
    private DailyService dailyService;

    @GetMapping("{day}")
    public R createStatisticsByDate(@PathVariable String day) {
        dailyService.createStatisticsByDay(day);
        return R.ok();
    }

    @GetMapping("/show-chart/{begin}/{end}/{type}")
    public R showChart(@PathVariable String begin,
                       @PathVariable String end,
                       @PathVariable String type){
        //返回map集合，里面存入创建日期和对应的创建人数
        Map<String,Object> map = dailyService.getChartData(begin, end, type);
        return R.ok().data(map);

    }

}

