package com.frank.redissondemo.service.impl;

import static com.google.common.truth.Truth.assertThat;

import com.frank.redissondemo.DatabaseTest;
import com.frank.redissondemo.model.dto.CalcDatapointDTO;
import com.frank.redissondemo.model.dto.SaveDatapointDTO;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.service.DatapointService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class DatapointServiceImplTest extends DatabaseTest {

  @Autowired private DatapointService datapointService;

  @Test
  void calculate_datapoint_append_text() {
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
  void calculate_datapoint_add_long() {
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
  void calculate_datapoint_add_numeric() {
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
  void calculate_will_fail_if_datapoint_not_exists() {
    // arrange
    CalcDatapointDTO calcDatapoint = new CalcDatapointDTO("code4", 1L, null, null, null);

    // act
    try {
      datapointService.calculateDatapoint(calcDatapoint);
    } catch (Exception e) {
      // assert
      assertThat(e).isInstanceOf(RuntimeException.class);
      assertThat(e).hasMessageThat().isEqualTo("找不到数据点");
    }
  }

  @Test
  void save_datapoint_will_create_new_datapoint_if_it_does_not_exists() {
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
  void save_datapoint_currently() throws InterruptedException {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);
    CountDownLatch countDownLatch = new CountDownLatch(2);
    CountDownLatch resultLatch = new CountDownLatch(2);
    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(
        () -> {
          log.info("save_datapoint_currently: Thread 1 enter");
          countDownLatch.countDown();
          try {
            countDownLatch.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          log.info("save_datapoint_currently: Thread 1 begin to save");

          SaveDatapointDTO saveDatapointDTO =
              new SaveDatapointDTO(1L, "code3", null, new BigDecimal("4.0000001"), null, null);

          Datapoint result = datapointService.saveDatapoint(saveDatapointDTO);
          log.info("save_datapoint_currently: Thread 1 result: {}", result);
          resultLatch.countDown();
        });

    executor.submit(
        () -> {
          log.info("save_datapoint_currently: Thread 2 enter");
          countDownLatch.countDown();
          try {
            countDownLatch.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          log.info("save_datapoint_currently: Thread 2 begin to save");

          SaveDatapointDTO saveDatapointDTO =
              new SaveDatapointDTO(1L, "code3", null, new BigDecimal("4.0000002"), null, null);

          Datapoint result = datapointService.saveDatapoint(saveDatapointDTO);
          log.info("save_datapoint_currently: Thread 2 result: {}", result);
          resultLatch.countDown();
        });
    resultLatch.await();
  }

  @Test
  void save_datapoint_will_update_datapoint_if_it_existed() {
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

  @Test
  void batch_create_datapoint_then_get_them_by_group_id() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();

    // act
    boolean result = datapointService.batchCreateDataPoint(datapointList);
    List<Datapoint> resultDatapointList =
        datapointService.getDatapointByGroupId(1L).stream()
            .sorted(Comparator.comparing(Datapoint::getIndicatorCode))
            .collect(Collectors.toList());

    // assert
    assertThat(result).isTrue();
    assertThat(resultDatapointList).isNotNull();
    assertThat(resultDatapointList).hasSize(3);

    assertThat(resultDatapointList.get(0).getIndicatorCode()).isEqualTo("code1");
    assertThat(resultDatapointList.get(0).getGroupId()).isEqualTo(1L);
    assertThat(resultDatapointList.get(0).getTextValue()).isEqualTo("text1");

    assertThat(resultDatapointList.get(1).getIndicatorCode()).isEqualTo("code2");
    assertThat(resultDatapointList.get(1).getGroupId()).isEqualTo(1L);
    assertThat(resultDatapointList.get(1).getLongValue()).isEqualTo(2L);

    assertThat(resultDatapointList.get(2).getIndicatorCode()).isEqualTo("code3");
    assertThat(resultDatapointList.get(2).getGroupId()).isEqualTo(1L);
    assertThat(resultDatapointList.get(2).getNumericValue())
        .isEqualToIgnoringScale(new BigDecimal("3.1415926"));
  }

  @Test
  void get_by_indicator_and_group_id() {
    // arrange
    List<Datapoint> datapointList = createDatapointList();
    datapointService.batchCreateDataPoint(datapointList);

    // act
    Datapoint result = datapointService.getDatapointByGroupIdAndIndicatorCode(1L, "code1").get();

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code1");
    assertThat(result.getTextValue()).isEqualTo("text1");
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
