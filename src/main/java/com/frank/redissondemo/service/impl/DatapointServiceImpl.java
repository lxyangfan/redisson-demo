package com.frank.redissondemo.service.impl;

import com.frank.redissondemo.dao.repository.DatapointRepository;
import com.frank.redissondemo.model.dto.CalcDatapointDTO;
import com.frank.redissondemo.model.dto.SaveDatapointDTO;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.service.DatapointService;
import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatapointServiceImpl implements DatapointService {

  private final DatapointRepository repository;

  @Override
  public Datapoint calculateDatapoint(CalcDatapointDTO calcDatapoint) {
    // 从数据库根据groupId + indicatorCode查询数据点
    Optional<Datapoint> datapointOpt =
        getDatapointByGroupIdAndIndicatorCode(
            calcDatapoint.groupId(), calcDatapoint.indicatorCode());
    // 如果没有查询到数据点，则报异常：找不到数据点
    // 如果查询到了数据点，则根据CalcDatapointDTO的值进行计算, 并返回正确的结果
    return datapointOpt
        .map(dp -> calculateDatapoint(dp, calcDatapoint))
        .orElseThrow(() -> new RuntimeException("找不到数据点"));
  }

  @Override
  public Datapoint saveDatapoint(SaveDatapointDTO saveReq) {

    // 可能是新增或者更新数据点， 如果是更新，需要先查询数据点是否存在，然后进行计算更新
    Optional<Datapoint> datapointOpt =
        getDatapointByGroupIdAndIndicatorCode(saveReq.groupId(), saveReq.indicatorCode());

    Datapoint dp;
    if (datapointOpt.isPresent()) {
      dp = datapointOpt.get();
      if (Objects.nonNull(saveReq.diff())) {
        calculateDatapoint(dp, saveReq.diff());
      }
      repository.updateById(dp);
    } else {
      dp = new Datapoint();
      dp.setGroupId(saveReq.groupId());
      dp.setIndicatorCode(saveReq.indicatorCode());
      dp.setTextValue(saveReq.text());
      dp.setNumericValue(saveReq.numeric());
      dp.setLongValue(saveReq.longValue());
      repository.save(dp);
    }
    return dp;
  }

  private Datapoint calculateDatapoint(Datapoint datapoint, CalcDatapointDTO calcDatapoint) {
    Preconditions.checkNotNull(datapoint, "数据点不能为空");
    Preconditions.checkNotNull(calcDatapoint, "计算数据点不能为空");
    // 根据CalcDatapointDTO的值进行计算, 并返回正确的结果

    if (Objects.nonNull(calcDatapoint.addLong())) {
      datapoint.setLongValue(
          Optional.ofNullable(datapoint.getLongValue()).orElse(0L) + calcDatapoint.addLong());
    }

    if (Objects.nonNull(calcDatapoint.addNumeric())) {
      BigDecimal result =
          Optional.ofNullable(datapoint.getNumericValue())
              .orElse(BigDecimal.ZERO)
              .add(calcDatapoint.addNumeric());
      datapoint.setNumericValue(result);
    }

    if (Objects.nonNull(calcDatapoint.appendText())) {
      datapoint.setTextValue(
          Optional.ofNullable(datapoint.getTextValue()).orElse("") + calcDatapoint.appendText());
    }
    return datapoint;
  }

  @Override
  public boolean batchCreateDataPoint(List<Datapoint> dataPointList) {
    return repository.saveBatch(dataPointList);
  }

  @Override
  public boolean batchUpdateDataPoint(List<Datapoint> dataPointList) {
    return repository.updateBatchById(dataPointList);
  }

  @Override
  public List<Datapoint> getDatapointByGroupId(Long groupId) {
    return repository.listByGroupId(groupId);
  }

  @Override
  public Optional<Datapoint> getDatapointByGroupIdAndIndicatorCode(
      Long groupId, String indicatorCode) {
    // TODO 添加redis缓存
    return repository.findByGroupIdAndIndicatorCode(groupId, indicatorCode);
  }
}
