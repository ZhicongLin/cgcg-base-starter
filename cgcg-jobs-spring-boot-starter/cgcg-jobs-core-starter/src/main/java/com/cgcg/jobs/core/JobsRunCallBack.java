package com.cgcg.jobs.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务执行结果
 *
 * @author zhicong.lin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsRunCallBack implements Serializable {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long runTime;
    private Boolean result;
    private String remark;

}
