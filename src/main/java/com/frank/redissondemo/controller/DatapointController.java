package com.frank.redissondemo.controller;

import com.frank.redissondemo.model.dto.CalcDatapointDTO;
import com.frank.redissondemo.model.dto.SaveDatapointDTO;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.service.DatapointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/datapoint")
@RequiredArgsConstructor
public class DatapointController {

  private final DatapointService datapointService;

  @PostMapping("/batch-create-datapoint")
  public boolean batchCreateDatapoint(@RequestBody List<Datapoint> datapointList) {
    return datapointService.batchCreateDataPoint(datapointList);
  }

  @PostMapping("/calculate")
  public Datapoint calculateDatapoint(@RequestBody CalcDatapointDTO calcDatapoint) {
    return datapointService.calculateDatapoint(calcDatapoint);
  }

  @PostMapping("/save-datapoint")
  public Datapoint saveDatapoint(@RequestBody SaveDatapointDTO datapoint) {
    return datapointService.saveDatapoint(datapoint);
  }

  @GetMapping("/get-datapoint")
  public Datapoint getDatapoint(
      @RequestParam("group-id") Long groupId,
      @RequestParam("indicator-code") String indicatorCode) {
    return datapointService
        .getDatapointByGroupIdAndIndicatorCode(groupId, indicatorCode)
        .orElse(null);
  }

  @GetMapping("/get-datapoint-list-by-group-id")
  public List<Datapoint> getDatapointByGroupId(@RequestParam("group-id") Long groupId) {
    return datapointService.getDatapointByGroupId(groupId);
  }
}
