package com.frank.redissondemo.service.impl;

import com.frank.redissondemo.dao.repository.DatapointRepository;
import com.frank.redissondemo.model.dto.CalcDatapointDTO;
import com.frank.redissondemo.model.dto.SaveDatapointDTO;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.service.CacheService;
import com.frank.redissondemo.service.DatapointService;
import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatapointServiceImpl implements DatapointService {

  private final DatapointRepository repository;

  private final CacheService cacheService;

  @Value("${datapoint.cache.expire-in-millis:300_000L}")
  private Long cacheExpireInMillis;

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

    Datapoint dp;
    if (Objects.isNull(saveReq.diff())) {
      // diff 不存在，则直接保存
      log.debug("diff 不存在，直接保存");
      dp = new Datapoint();
      dp.setGroupId(saveReq.groupId());
      dp.setIndicatorCode(saveReq.indicatorCode());
      dp.setTextValue(saveReq.text());
      dp.setNumericValue(saveReq.numeric());
      dp.setLongValue(saveReq.longValue());

    } else {
      // diff 存在，则进行计算更新
      // 可能是新增或者更新数据点， 如果是更新，需要先查询数据点是否存在，然后进行计算更新
      log.debug("diff 存在，进行计算更新");
      Optional<Datapoint> datapointOpt =
          getDatapointByGroupIdAndIndicatorCode(saveReq.groupId(), saveReq.indicatorCode());

      dp = datapointOpt.get();
      calculateDatapoint(dp, saveReq.diff());
    }

    repository.insertOnDuplicateKeyUpdate(dp);
    // 删除缓存
    String cacheKey = cacheKeyForMap(dp.getGroupId());
    String itemCacheKey = cacheKeyForMapItem(dp);
    cacheService.deleteMapItem(cacheKey, itemCacheKey);
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

    Long groupId = assertDataPointSameGroupId(dataPointList);

    repository.saveBatch(dataPointList);

    cacheService.deleteMap(cacheKeyForMap(groupId));
    return true;
  }

  @Override
  public boolean batchUpdateDataPoint(List<Datapoint> dataPointList) {

    // 验证数据点都是同一个groupId
    Long groupId = assertDataPointSameGroupId(dataPointList);

    repository.updateBatchById(dataPointList);

    // 删除缓存
    String cacheKey = cacheKeyForMap(groupId);
    Set<String> itemCacheKeys =
        dataPointList.stream().map(this::cacheKeyForMapItem).collect(Collectors.toSet());
    cacheService.batchDeleteMapItems(cacheKey, itemCacheKeys);
    return true;
  }

  private Long assertDataPointSameGroupId(List<Datapoint> dataPointList) {
    Long groupId = dataPointList.get(0).getGroupId();
    Preconditions.checkArgument(
        dataPointList.stream().allMatch(dp -> dp.getGroupId().equals(groupId)), "数据点groupId不一致");
    return groupId;
  }

  @Override
  public List<Datapoint> getDatapointByGroupId(Long groupId) {

    List<Datapoint> result = repository.listByGroupId(groupId);
    if (!result.isEmpty()) {
      //  添加redis缓存
      String cacheKey = cacheKeyForMap(groupId);
      Map<String, Datapoint> resultMap =
          result.stream().collect(Collectors.toMap(this::cacheKeyForMapItem, Function.identity()));
      cacheService.batchSetMap(cacheKey, resultMap, cacheExpireInMillis);
    }
    return result;
  }

  @Override
  public Optional<Datapoint> getDatapointByGroupIdAndIndicatorCode(
      Long groupId, String indicatorCode) {
    //  添加redis缓存
    String itemCacheKey = cacheKeyForMapItem(groupId, indicatorCode);
    String cacheKey = cacheKeyForMap(groupId);
    Datapoint datapoint = cacheService.getMapItem(Datapoint.class, cacheKey, itemCacheKey);

    if (Objects.nonNull(datapoint)) {
      log.debug("cache hit: {} vals:{}", itemCacheKey, datapoint);
      return Optional.of(datapoint);
    } else {
      Optional<Datapoint> datapointOps =
          repository.findByGroupIdAndIndicatorCode(groupId, indicatorCode);
      if (datapointOps.isPresent()) {
        cacheService.setMapItem(cacheKey, itemCacheKey, datapointOps.get(), cacheExpireInMillis);
      }
      return datapointOps;
    }
  }

  private String cacheKeyForMap(Long groupId) {
    return "datapoint:group:" + groupId;
  }

  private String cacheKeyForMapItem(Datapoint datapoint) {
    return "datapoint:group:"
        + datapoint.getGroupId()
        + ":indicator:"
        + datapoint.getIndicatorCode();
  }

  private String cacheKeyForMapItem(Long groupId, String indicatorCode) {
    return "datapoint:group:" + groupId + ":indicator:" + indicatorCode;
  }
}
