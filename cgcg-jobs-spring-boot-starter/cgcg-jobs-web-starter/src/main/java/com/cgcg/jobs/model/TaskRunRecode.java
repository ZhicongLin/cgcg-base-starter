package com.cgcg.jobs.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author zhicong.lin
 */
@Setter
@Getter
public class TaskRunRecode {
    private Long id;
    private Long taskId;
    private Long serverId;
    private Long runTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean result;
    private String remark;
}
