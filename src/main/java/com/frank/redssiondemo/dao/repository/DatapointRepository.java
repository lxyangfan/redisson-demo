package com.frank.redssiondemo.dao.repository;

import com.frank.redssiondemo.model.po.Datapoint;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Optional;

/**
* @author frank
* @description 针对表【datapoint(数据点)】的数据库操作Service
* @createDate 2024-02-08 14:49:17
*/
public interface DatapointRepository extends IService<Datapoint> {
  Optional<Datapoint> findByGroupIdAndIndicatorCode(Long groupId, String indicatorCode);

  List<Datapoint> listByGroupId(Long groupId);
}
