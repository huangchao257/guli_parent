package com.atguigu.eduservice.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "课时信息")
@Data
public class VideoVo implements Serializable {

    private String id;
    private String title;
    //是否免费
    private Boolean isFree;
    //云端视频资源
    private String videoSourceId;
}