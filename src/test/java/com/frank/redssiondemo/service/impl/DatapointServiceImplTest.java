package com.frank.redssiondemo.service.impl;

import static com.google.common.truth.Truth.assertThat;

import com.frank.redssiondemo.DatabaseTest;
import com.frank.redssiondemo.model.dto.CalcDatapointDTO;
import com.frank.redssiondemo.model.dto.SaveDatapointDTO;
import com.frank.redssiondemo.model.po.Datapoint;
import com.frank.redssiondemo.service.DatapointService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatapointServiceImplTest extends DatabaseTest {

  @Autowired private DatapointService datapointService;

  @Test
  void calculateDatapoint_append_text() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);

    // act
    CalcDatapointDTO calcDatapoint = new CalcDatapointDTO("code1", 1L, null, "new", null);
    Datapoint result = datapointService.calculateDatapoint(calcDatapoint);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code1");
    assertThat(result.getTextValue()).isEqualTo("text1new");
  }

  @Test
  void calculateDatapoint_add_long() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);

    // act
    CalcDatapointDTO calcDatapoint = new CalcDatapointDTO("code2", 1L, 1L, null, null);
    Datapoint result = datapointService.calculateDatapoint(calcDatapoint);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code2");
    assertThat(result.getLongValue()).isEqualTo(3L);
  }

  @Test
  void calculateDatapoint_add_numeric() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);

    // act
    CalcDatapointDTO calcDatapoint =
        new CalcDatapointDTO("code3", 1L, null, null, new BigDecimal("0.0000001"));
    Datapoint result = datapointService.calculateDatapoint(calcDatapoint);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code3");
    assertThat(result.getNumericValue()).isEqualToIgnoringScale(new BigDecimal("3.1415927"));
  }

  @Test
  void saveDatapoint_create_new_datapoint_if_not_exists() {
    // arrange
    SaveDatapointDTO saveDatapointDTO =
        new SaveDatapointDTO(1L, "code4", "text4", new BigDecimal("4.0000001"), 4L, null);
    // act
    Datapoint result = datapointService.saveDatapoint(saveDatapointDTO);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code4");
    assertThat(result.getTextValue()).isEqualTo("text4");
    assertThat(result.getNumericValue()).isEqualToIgnoringScale(new BigDecimal("4.0000001"));
    assertThat(result.getLongValue()).isEqualTo(4L);
  }

  @Test
  void saveDatapoint_update_datapoint_if_exists() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);
    SaveDatapointDTO saveDatapointDTO =
        new SaveDatapointDTO(
            1L,
            "code1",
            null,
            null,
            null,
            new CalcDatapointDTO("code1", 1L, 4L, "text4", new BigDecimal("0.0000001")));
    // act
    Datapoint result = datapointService.saveDatapoint(saveDatapointDTO);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code1");
    assertThat(result.getTextValue()).isEqualTo("text1text4");
    assertThat(result.getNumericValue()).isEqualToIgnoringScale(new BigDecimal("0.0000001"));
    assertThat(result.getLongValue()).isEqualTo(4L);
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
