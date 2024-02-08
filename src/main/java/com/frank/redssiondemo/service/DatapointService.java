package com.frank.redssiondemo.service;

import com.frank.redssiondemo.model.dto.CalcDatapointDTO;
import com.frank.redssiondemo.model.dto.SaveDatapointDTO;
import com.frank.redssiondemo.model.po.Datapoint;
import java.util.List;
import java.util.Optional;

public interface DatapointService {

  Datapoint calculateDatapoint(CalcDatapointDTO calcDatapoint);

  Datapoint saveDatapoint(SaveDatapointDTO datapoint);

  boolean batchCreateDataPoint(List<Datapoint> dataPointList);

  boolean batchUpdateDataPoint(List<Datapoint> dataPointList);

  List<Datapoint> getDatapointByGroupId(Long groupId);

  Optional<Datapoint> getDatapointByGroupIdAndIndicatorCode(Long groupId, String indicatorCode);
}
