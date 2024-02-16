package com.frank.redissondemo.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.dao.repository.DatapointRepository;
import com.frank.redissondemo.dao.mapper.DatapointMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * @author frank
 * @description 针对表【datapoint(数据点)】的数据库操作Service实现
 * @createDate 2024-02-08 14:49:17
 */
@Repository
public class DatapointRepositoryImpl extends ServiceImpl<DatapointMapper, Datapoint>
    implements DatapointRepository {

  @Override
  public Optional<Datapoint> findByGroupIdAndIndicatorCode(Long groupId, String indicatorCode) {
    return lambdaQuery()
        .eq(Datapoint::getGroupId, groupId)
        .eq(Datapoint::getIndicatorCode, indicatorCode)
        .oneOpt();
  }

  @Override
  public List<Datapoint> listByGroupId(Long groupId) {
    return lambdaQuery().eq(Datapoint::getGroupId, groupId).list();
  }

  @Override
  public boolean insertOnDuplicateKeyUpdate(Datapoint datapoint) {
    return getBaseMapper().insertOnDuplicateKeyUpdate(datapoint);
  }
}
