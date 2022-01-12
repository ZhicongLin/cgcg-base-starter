package com.cgcg.jobs.web;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TaskInfoMapper {
    @Select("select * from task_info where status = 1")
    List<TaskInfo> findAllTaskInfo();

    @Insert("insert into task_run_recode (task_id, run_time, start_time, end_time, result, remark) " +
            "values (#{taskId},#{runTime},#{startTime},#{endTime},#{result},#{remark})")
    void saveRunRecode(TaskRunRecode taskRunRecode);

    @Select("select * from task_info where id=#{id}")
    TaskInfo findById(Long id);

    @Update("update task_info set task_key=#{taskKey},group_key=#{groupKey},host=#{host},port=#{port}," +
            "args=#{args},name=#{name},`desc`=#{desc},cron=#{cron},status=#{status} where id=#{id}")
    int update(TaskInfo info);

    @Insert("insert into task_info (host, port, args, name, `desc`, task_key, group_key, cron, status) " +
            "values (#{host},#{port},#{args},#{name},#{desc},#{taskKey},#{groupKey},#{cron},#{status})")
    int insert(TaskInfo info);
}
