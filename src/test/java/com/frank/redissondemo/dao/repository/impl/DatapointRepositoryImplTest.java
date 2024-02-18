package com.frank.redissondemo.dao.repository.impl;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.frank.redissondemo.DatabaseTest;
import com.frank.redissondemo.dao.repository.DatapointRepository;
import com.frank.redissondemo.model.po.Datapoint;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatapointRepositoryImplTest extends DatabaseTest {

  @Autowired
  private DatapointRepository datapointRepository;
  @Test
  void insertOnDuplicateKeyUpdate() {
    // arrange
    List<Datapoint> datapointList = prepareData();
    datapointRepository.saveBatch(datapointList);

    // act
    Datapoint datapoint = Datapoint.builder().groupId(1L).indicatorCode("code1").textValue("text11").build();
    boolean result = datapointRepository.insertOnDuplicateKeyUpdate(datapoint);

    // assert
    assertThat(result).isTrue();
    Datapoint actual = datapointRepository.findByGroupIdAndIndicatorCode(1L, "code1").get();
    assertThat(actual.getTextValue()).isEqualTo("text11");

  }

  private List<Datapoint> prepareData() {
    return Lists.newArrayList(
        Datapoint.builder().groupId(1L).indicatorCode("code1").textValue("text1").build(),
        Datapoint.builder().groupId(1L).indicatorCode("code2").longValue(2L).build(),
        Datapoint.builder().groupId(2L).indicatorCode("code1").textValue("text2").build(),
        Datapoint.builder().groupId(2L).indicatorCode("code2").longValue(3L).build()
    );
  }
}
