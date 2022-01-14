package com.cgcg.jobs.web.mapper;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskInfoMapper {
    @Select("select distinct t.* from task_info t inner join task_server ts on ts.task_id = t.id where ts.status = 1")
    List<TaskInfo> findAllTaskInfo();

    @Insert("insert into task_run_recode (task_id, server_id, run_time, start_time, end_time, result, remark) " +
            "values (#{taskId},#{serverId},#{runTime},#{startTime},#{endTime},#{result},#{remark})")
    void saveRunRecode(TaskRunRecode taskRunRecode);

    @Select("select * from task_info where id=#{id}")
    TaskInfo findById(Long id);

    @Update("update task_info set task_key=#{taskKey},group_key=#{groupKey},host=#{host},port=#{port}," +
            "args=#{args},name=#{name},`desc`=#{desc},cron=#{cron},status=#{status} where id=#{id}")
    int update(TaskInfo info);

    @Insert("insert into task_info (host, port, args, name, `desc`, task_key, group_key, cron, status) " +
            "values (#{host},#{port},#{args},#{name},#{desc},#{taskKey},#{groupKey},#{cron},#{status})")
    int insert(TaskInfo info);


    @Select("select * from task_run_recode where server_id =#{sId} order by start_time desc limit #{ps}")
    List<TaskRunRecode> findRecodeByServerId(@Param("sId")Long serverId, @Param("ps")int pageSize);
}
