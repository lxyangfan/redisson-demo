package com.frank.redissondemo.controller;

import static com.google.common.truth.Truth.assertThat;

import com.frank.redissondemo.ControllerTest;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.service.DatapointService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class DatapointControllerTest extends ControllerTest {

  @LocalServerPort private int port;

  @Autowired private DatapointService datapointService;

  @Test
  void get_datapoint_by_group_id_and_indicator_code() {

    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);

    // act
    Datapoint result =
        restTemplate.getForObject(
            buildUrl("/get-datapoint?group-id=1&indicator-code=code1"), Datapoint.class);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code1");
    assertThat(result.getTextValue()).isEqualTo("text1");
  }

  @Test
  void get_datapoint_by_group_id() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);

    // act
    ParameterizedTypeReference<List<Datapoint>> responseType =
        new ParameterizedTypeReference<List<Datapoint>>() {};
    ResponseEntity<List<Datapoint>> responseEntity =
        restTemplate.exchange(
            buildUrl("/get-datapoint-list-by-group-id?group-id=1"),
            HttpMethod.GET,
            null,
            responseType);

    List<Datapoint> result =
        responseEntity.getBody().stream()
            .sorted(Comparator.comparing(Datapoint::getIndicatorCode))
            .collect(Collectors.toList());
    // assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);

    assertThat(result.get(0).getGroupId()).isEqualTo(1L);
    assertThat(result.get(0).getIndicatorCode()).isEqualTo("code1");
    assertThat(result.get(0).getTextValue()).isEqualTo("text1");

    assertThat(result.get(1).getGroupId()).isEqualTo(1L);
    assertThat(result.get(1).getIndicatorCode()).isEqualTo("code2");
    assertThat(result.get(1).getLongValue()).isEqualTo(2L);

    assertThat(result.get(2).getGroupId()).isEqualTo(1L);
    assertThat(result.get(2).getIndicatorCode()).isEqualTo("code3");
    assertThat(result.get(2).getNumericValue()).isEqualToIgnoringScale(new BigDecimal("3.1415926"));
  }

  private String buildUrl(String path) {
    return "http://localhost:" + port + "/api/datapoint" + path;
  }

  private List<Datapoint> createDatapointList() {
    return Lists.newArrayList(
        Datapoint.builder().groupId(1L).indicatorCode("code1").textValue("text1").build(),
        Datapoint.builder().groupId(1L).indicatorCode("code2").longValue(2L).build(),
        Datapoint.builder()
            .groupId(1L)
            .indicatorCode("code3")
            .numericValue(new BigDecimal("3.1415926"))
            .build());
  }
}
