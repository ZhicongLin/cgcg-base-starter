package com.cgcg.jobs.model;

import lombok.Data;

/**
 * @author zhicong.lin 2022/1/26
 */
@Data
public class TaskServer {
    private Long id;
    private Long taskId;
    private String host;
    private Integer port;
    private Integer status;
    private Integer coreCount;
}
