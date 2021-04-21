package com.atguigu.cmsservice.controller;


import com.atguigu.cmsservice.entity.Banner;
import com.atguigu.cmsservice.service.BannerService;
import com.atguigu.commonutils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 首页banner表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-12
 */
@Api(description = "banner管理")
//@CrossOrigin
@RestController
@RequestMapping("/cmsservice/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation(value = "获取Banner分页列表")
    @GetMapping("getBannerPage/{page}/{limit}")
    public R getBannerPage(@PathVariable("page") Long page,@PathVariable("limit") Long limit){
        Page<Banner> pageParam = new Page<>(page, limit);
        bannerService.page(pageParam, null);
        List<Banner> records = pageParam.getRecords();
        long total = pageParam.getTotal();
        return R.ok().data("records", records).data("total", total);
    }

    @ApiOperation(value = "获取Banner")
    @GetMapping("getBannerById/{id}")
    public R get(@PathVariable String id) {
        Banner banner = bannerService.getById(id);
        return R.ok().data("banner", banner);
    }

    @ApiOperation(value = "新增Banner")
    @PostMapping("addBanner")
    public R save(@RequestBody Banner banner) {
        bannerService.save(banner);
        return R.ok();
    }

    @ApiOperation(value = "修改Banner")
    @PutMapping("updateBanner")
    public R updateById(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return R.ok();
    }

    @ApiOperation(value = "删除Banner")
    @DeleteMapping("delBannerById/{id}")
    public R remove(@PathVariable String id) {
        bannerService.removeById(id);
        return R.ok();
    }
}

