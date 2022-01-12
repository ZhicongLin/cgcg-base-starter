package com.cgcg.jobs.model;

import lombok.Data;

@Data
public class TaskInfo {
    private Long id;
    private String host;
    private Integer port;
    private String args;
    private String name;
    private String desc;
    private String taskKey;
    private String groupKey;
    private String cron;
    private Integer status;
}
