package com.cgcg.jobs.model;

import lombok.Data;

import java.util.List;

@Data
public class TaskServer {
    private Long id;
    private Long taskId;
    private String host;
    private Integer port;
    private Integer status;
    private Integer coreCount; //Runtime.getRuntime().availableProcessors()
}
