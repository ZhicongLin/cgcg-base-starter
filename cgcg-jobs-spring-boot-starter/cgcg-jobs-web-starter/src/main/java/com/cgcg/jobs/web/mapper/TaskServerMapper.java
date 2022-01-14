package com.cgcg.jobs.web.mapper;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import com.cgcg.jobs.model.TaskServer;
import org.apache.catalina.Server;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskServerMapper {

    @Select("select * from task_server where id=#{id}")
    TaskServer findById(Long id);

    @Select("select * from task_server where task_id = #{tid} and status = #{status}")
    List<TaskServer> findByTaskIdAndStatus(@Param("tid") Long taskId, @Param("status") Integer status);

    @Select("select * from task_server where task_id = #{tid}")
    List<TaskServer> findByTaskId(@Param("tid") Long taskId);

    @Select("select * from task_server where status = #{status}")
    List<TaskServer> findByStatus(@Param("status") Integer status);

    @Update("update task_server set status = #{s} where id = #{id}")
    void modifyStatus(@Param("id") Long serverId, @Param("s") Integer status);
}
