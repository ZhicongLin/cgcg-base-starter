package com.cgcg.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 分页基本参数
 *
 * @author zhicong.lin
 * @date 2022-02-15 10:01
 **/
@Setter
@Getter
@ApiModel("基础参数")
public class SimplePageInfo {

    @ApiModelProperty("当前页码")
    private Integer pageNum;
    @ApiModelProperty("每页条数")
    private Integer pageSize;
}
