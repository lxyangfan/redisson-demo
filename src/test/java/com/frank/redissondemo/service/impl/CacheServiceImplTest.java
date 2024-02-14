package com.frank.redissondemo.service.impl;

import static com.google.common.truth.Truth.assertThat;

import com.frank.redissondemo.DatabaseTest;
import com.frank.redissondemo.model.po.Datapoint;
import com.frank.redissondemo.service.CacheService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CacheServiceImplTest extends DatabaseTest {

  @Autowired private CacheService cacheService;

  @Test
  void set_a_value_with_tll_and_get_before_it_expires() {
    // arrange
    List<Datapoint> datapointList = prepareData();

    // act
    cacheService.set("key-0", datapointList.get(2), 300000L);

    // assert
    Datapoint result = cacheService.get("key-0");
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code3");
    assertThat(result.getNumericValue()).isEqualToIgnoringScale("3.1415926");
  }

  @Test
  void batch_set_a_map_with_ttl_and_get_before_it_expires() {
    // arrange
    List<Datapoint> datapointList = prepareData();
    Map<String, Datapoint> datapointMap =
        datapointList.stream()
            .collect(Collectors.toMap(Datapoint::getIndicatorCode, Function.identity()));
    // act
    String cacheKey = buildDatapointListCacheKey(datapointList.get(0).getGroupId());
    cacheService.batchSetMap(cacheKey, datapointMap, 30000L);

    // assert
    List<Datapoint> result =
        cacheService.batchGetMap(Datapoint.class, cacheKey, datapointMap.keySet()).stream()
            .sorted(Comparator.nullsLast(Comparator.comparing(Datapoint::getIndicatorCode)))
            .collect(Collectors.toList());

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
    assertThat(result.get(2).getNumericValue()).isEqualToIgnoringScale("3.1415926");

    cacheService.deleteMap(cacheKey);
  }

  @Test
  void batch_get_a_map_fails_if_it_expired() {
    // arrange

    List<Datapoint> datapointList = prepareData();
    Map<String, Datapoint> datapointMap =
        datapointList.stream()
            .collect(Collectors.toMap(Datapoint::getIndicatorCode, Function.identity()));
    String cacheKey = buildDatapointListCacheKey(datapointList.get(0).getGroupId());

    // act
    cacheService.batchSetMap(cacheKey, datapointMap, 200L);
    try {
      Thread.sleep(210L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // assert
    List<Datapoint> result =
        cacheService.batchGetMap(Datapoint.class, cacheKey, datapointMap.keySet());
    assertThat(result).isEmpty();

    cacheService.deleteMap(cacheKey);

  }

  @Test
  void batch_set_map_with_different_values() {
    // arrange
    List<Datapoint> datapointList = prepareData();
    Map<String, Datapoint> datapointMap =
        datapointList.subList(0, 1).stream()
            .collect(Collectors.toMap(Datapoint::getIndicatorCode, Function.identity()));

    // act
    String cacheKey = buildDatapointListCacheKey(datapointList.get(0).getGroupId());
    cacheService.batchSetMap(cacheKey, datapointMap, 3000L);

    datapointMap =
        datapointList.subList(1, 3).stream()
            .collect(Collectors.toMap(Datapoint::getIndicatorCode, Function.identity()));
    cacheService.batchSetMap(cacheKey, datapointMap, 3000L);

    // assert
    List<Datapoint> result =
        cacheService
            .batchGetMap(Datapoint.class, cacheKey, Set.of("code1", "code2", "code3"))
            .stream()
            .sorted(Comparator.nullsLast(Comparator.comparing(Datapoint::getIndicatorCode)))
            .collect(Collectors.toList());

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
    assertThat(result.get(2).getNumericValue()).isEqualToIgnoringScale("3.1415926");

    cacheService.deleteMap(cacheKey);

  }

  @Test
  void set_map_item_and_get_it_before_it_expires() {
    // arrange
    List<Datapoint> datapointList = prepareData();
    String cacheKey = buildDatapointListCacheKey(datapointList.get(0).getGroupId());

    // act
    cacheService.setMapItem(cacheKey, "code1", datapointList.get(0), 3000L);

    // assert
    Datapoint result = cacheService.getMapItem(Datapoint.class, cacheKey, "code1");
    assertThat(result).isNotNull();
    assertThat(result.getGroupId()).isEqualTo(1L);
    assertThat(result.getIndicatorCode()).isEqualTo("code1");
    assertThat(result.getTextValue()).isEqualTo("text1");

    // clean up
    cacheService.deleteMap(cacheKey);

  }

  @Test
  void get_empty_item_if_not_set() {
    // arrange
    String cacheKey = buildDatapointListCacheKey(1L);

    // assert
    Datapoint result = cacheService.getMapItem(Datapoint.class, cacheKey, "code1");
    assertThat(result).isNull();
  }

  private String buildDatapointListCacheKey(Long groupId) {
    return "datapoint-list-" + groupId;
  }

  private List<Datapoint> prepareData() {
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
