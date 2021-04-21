package com.atguigu.eduservice.api;

import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.entity.vo.CourseWebVo;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.servicebase.vo.CourseWebVoForPay;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(description = "前台课程展示")
//@CrossOrigin
@RestController
@RequestMapping("/eduservice/courseapi")
public class CourseApiController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OrderClient orderClient;

    @ApiOperation(value = "前台带条件课程的分页查询")
    @PostMapping("getCourseApiPageVo/{page}/{limit}")
    public R getCourseApiPageVo(@PathVariable("page") long page, @PathVariable("limit") long limit,
                                @RequestBody CourseQueryVo courseQueryVo) {
        Page<EduCourse> pageParam = new Page<>(page, limit);
        Map<String, Object> map = courseService.getCourseApiPageVo(pageParam, courseQueryVo);
        return R.ok().data(map);
    }

    @ApiOperation(value = "前台课程页面详情")
    @GetMapping("getCourseApiInfo/{id}")
    public R getCourseApiInfo(@PathVariable String id, HttpServletRequest request) {
        //课程信息+课程描述信息+讲师信息
        CourseWebVo courseWebVo = courseService.getCourseWebVo(id);
        //课程大纲信息：章节+小节（树形）
        List<ChapterVo> chapterVideoList = chapterService.getChapterListById(id);
        //添加判断该课程此用户是否已购买
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        boolean isBuy = orderClient.isBuyCourse(memberId, id);


        return R.ok().data("courseWebVo", courseWebVo).data("chapterVideoList", chapterVideoList).data("isBuy", isBuy);
    }

    @ApiOperation("根据课程id查询课程信息远程调用")
    @GetMapping("getCourseInfoForPay/{courseId}")
    public CourseWebVoForPay getCourseInfoForPay(@PathVariable("courseId") String courseId) {
        CourseWebVo courseWebVo = courseService.getCourseWebVo(courseId);
        CourseWebVoForPay courseWebVoForPay = new CourseWebVoForPay();
        BeanUtils.copyProperties(courseWebVo, courseWebVoForPay);
        return courseWebVoForPay;
    }
}
