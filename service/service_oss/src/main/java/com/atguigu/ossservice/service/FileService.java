package com.atguigu.ossservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    //上传文件
    String uploadFileOss(MultipartFile file);

    void delFile(String cover);
}
