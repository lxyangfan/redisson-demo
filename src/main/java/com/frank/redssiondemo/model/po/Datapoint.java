package com.frank.redssiondemo.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 数据点 @TableName datapoint */
@TableName(value = "datapoint")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Datapoint implements Serializable {
  /** ID */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 分组ID */
  private Long groupId;

  /** 指标编码 */
  private String indicatorCode;

  /** 长整型值 */
  private Long longValue;

  /** 数值型值 */
  private BigDecimal numericValue;

  /** 文本值 */
  private String textValue;

  /** 创建时间 */
  private OffsetDateTime createTime;

  /** 更新时间 */
  private OffsetDateTime updateTime;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
