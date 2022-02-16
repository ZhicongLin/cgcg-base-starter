package com.cgcg.mybatis.plus;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 基础的实体类
 *
 * @author zhicong.lin
 * @date 2022-02-15 14:10
 **/
@Setter
@Getter
@ApiModel
public class BaseEntity implements Serializable {

    protected static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = PATTERN)
    @DateTimeFormat(pattern = PATTERN)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(hidden = true)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = PATTERN)
    @DateTimeFormat(pattern = PATTERN)
    private LocalDateTime updateTime;
}
