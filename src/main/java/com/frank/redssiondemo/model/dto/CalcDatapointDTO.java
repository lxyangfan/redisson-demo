package com.frank.redssiondemo.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CalcDatapointDTO(
    String indicatorCode, Long groupId, Long addLong, String appendText, BigDecimal addNumeric) {}
