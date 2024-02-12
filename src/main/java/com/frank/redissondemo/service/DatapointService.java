package com.frank.redissondemo.service;

import com.frank.redissondemo.model.dto.CalcDatapointDTO;
import com.frank.redissondemo.model.dto.SaveDatapointDTO;
import com.frank.redissondemo.model.po.Datapoint;
import java.util.List;
import java.util.Optional;

public interface DatapointService {

  /**
   * 实时计算：根据CalcDatapointDTO的值进行计算, 并返回正确的结果
   * @param calcDatapoint 用户输入的计算数据点的参数
   * @return 计算后的数据点
   */
  Datapoint calculateDatapoint(CalcDatapointDTO calcDatapoint);

  /**
   * 保存数据点, 可能是新增或者更新数据点, 如果是更新, 需要先查询数据点是否存在, 需要加锁然后进行计算更新，最后还需要删除缓存。
   * 如果是新增, 则直接保存数据点, 无需加锁.
   * TODO: version1: 更新数据点时, 需要增加乐观锁，并更新版本号;
   * TODO: version2: 使用数据库自带的悲观锁 SELECT * FOR UPDATE
   * TODO: version3: 使用分布式锁
   * @param datapoint 用户输入的数据点
   * @return 保存后的数据点
   */
  Datapoint saveDatapoint(SaveDatapointDTO datapoint);

  /**
   * 批量创建数据点
   * @param dataPointList 数据点列表
   * @return 是否创建成功
   */
  boolean batchCreateDataPoint(List<Datapoint> dataPointList);

  boolean batchUpdateDataPoint(List<Datapoint> dataPointList);

  /**
   * 根据groupId查询数据点;
   * TODO: 需要增加缓存
   * @param groupId groupId
   * @return 数据点列表
   */
  List<Datapoint> getDatapointByGroupId(Long groupId);

  /**
   * 根据groupId + indicatorCode查询数据点;
   * TODO: 需要增加缓存. 当cache miss时, 需要从数据库查询, 并写回DB
   * @param groupId 数据点组ID
   * @param indicatorCode 指标代码
   * @return 数据点
   */
  Optional<Datapoint> getDatapointByGroupIdAndIndicatorCode(Long groupId, String indicatorCode);
}
