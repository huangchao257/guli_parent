package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.vo.ExcelSubjectData;
import com.atguigu.eduservice.entity.vo.OneSubjectVo;
import com.atguigu.eduservice.entity.vo.TwoSubjectVo;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Action;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-04-02
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {


    @Override
    public void addSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelSubjectData.class, new SubjectExcelListener(subjectService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GuliException(20001, "导入课程信息失败");
        }
    }

    @Override
    public List<OneSubjectVo> getAllSubject() {
        //1查询所有一级分类信息
        QueryWrapper<EduSubject> oneWrapper = new QueryWrapper<>();
        oneWrapper.eq("parent_id", "0");

        List<EduSubject> oneSubjects = baseMapper.selectList(oneWrapper);
        //2查询所有二级分类信息
        QueryWrapper<EduSubject> twoWrapper = new QueryWrapper<>();
        twoWrapper.ne("parent_id", "0");
        List<EduSubject> twoSubjects = baseMapper.selectList(twoWrapper);
        //3遍历一级集合进行封装
        List<OneSubjectVo> allSubject = new ArrayList<>();
        for (int i = 0; i < oneSubjects.size(); i++) {
            OneSubjectVo oneSubjectVo = new OneSubjectVo();
            EduSubject oneSubject = oneSubjects.get(i);
            //将EduSubject转换成OneSubjectVo
            BeanUtils.copyProperties(oneSubject, oneSubjectVo);
            //4遍历二级，与一级信息有关二级进行封装
            List<TwoSubjectVo> twoSubjectVos = new ArrayList<>();
            for (int m = 0; m < twoSubjects.size(); m++) {
                EduSubject twoSubject = twoSubjects.get(m);
                //判断一级和二级是否有关系
                if (twoSubject.getParentId().equals(oneSubject.getId())) {
                    TwoSubjectVo twoSubjectVo = new TwoSubjectVo();
                    BeanUtils.copyProperties(twoSubject, twoSubjectVo);
                    //封装数据
                    twoSubjectVos.add(twoSubjectVo);
                }
            }
            oneSubjectVo.setChildren(twoSubjectVos);
            allSubject.add(oneSubjectVo);
        }

        return allSubject;
    }
}
