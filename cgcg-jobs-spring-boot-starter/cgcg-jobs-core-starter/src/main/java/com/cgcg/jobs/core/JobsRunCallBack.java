package com.cgcg.jobs.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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
