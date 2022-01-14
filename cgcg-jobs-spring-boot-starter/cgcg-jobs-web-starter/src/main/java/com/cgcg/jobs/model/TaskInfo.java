package com.cgcg.jobs.model;

import lombok.Data;

import java.util.List;

@Data
public class TaskInfo {
    private Long id;
    private String args;
    private String name;
    private String desc;
    private String taskKey;
    private String groupKey;
    private String cron;
    private List<TaskServer> servers;
    private TaskServer server;
}
