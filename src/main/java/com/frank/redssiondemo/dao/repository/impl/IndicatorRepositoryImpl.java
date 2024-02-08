package com.frank.redssiondemo.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.redssiondemo.model.po.Indicator;
import com.frank.redssiondemo.dao.repository.IndicatorRepository;
import com.frank.redssiondemo.dao.mapper.IndicatorMapper;
import org.springframework.stereotype.Repository;

/**
 * @author frank
 * @description 针对表【indicator】的数据库操作Service实现
 * @createDate 2024-02-08 14:49:51
 */
@Repository
public class IndicatorRepositoryImpl extends ServiceImpl<IndicatorMapper, Indicator>
    implements IndicatorRepository {}
