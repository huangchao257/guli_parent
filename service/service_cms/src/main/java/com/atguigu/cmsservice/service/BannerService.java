package com.atguigu.cmsservice.service;

import com.atguigu.cmsservice.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-04-12
 */
public interface BannerService extends IService<Banner> {

    List<Banner> getAllBanner();

    void updateBanner(Banner banner);
}
