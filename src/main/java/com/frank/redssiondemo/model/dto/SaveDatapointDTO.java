package com.frank.redssiondemo.model.dto;

import java.math.BigDecimal;

public record SaveDatapointDTO(
    Long groupId,
    String indicatorCode,
    String text,
    BigDecimal numeric,
    Long longValue,
    CalcDatapointDTO diff) {}
