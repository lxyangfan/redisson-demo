package com.frank.redissondemo.model.dto;

import java.math.BigDecimal;

public record CalcDatapointDTO(
    String indicatorCode, Long groupId, Long addLong, String appendText, BigDecimal addNumeric) {}
