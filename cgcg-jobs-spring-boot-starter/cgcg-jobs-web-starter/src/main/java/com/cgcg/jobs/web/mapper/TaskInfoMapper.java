package com.cgcg.jobs.web.mapper;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author zhicong.lin
 */
@Mapper
public interface TaskInfoMapper {

    /**
     * 获取全部任务信息
     *
     * @return java.util.List<com.cgcg.jobs.model.TaskInfo>
     * @author : zhicong.lin
     * @date : 2022/1/26 15:53
     */
    @Select("select distinct t.* from task_info t inner join task_server ts on ts.task_id = t.id where ts.status = 1")
    List<TaskInfo> findAllTaskInfo();

    /**
     * 保存任务运行记录
     *
     * @param taskRunRecode 运行记录
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 15:59
     */
    @Insert("insert into task_run_recode (task_id, server_id, run_time, start_time, end_time, result, remark) " +
            "values (#{taskId},#{serverId},#{runTime},#{startTime},#{endTime},#{result},#{remark})")
    void saveRunRecode(TaskRunRecode taskRunRecode);

    /**
     * 获取任务信息
     *
     * @param id 任务主键
     * @return com.cgcg.jobs.model.TaskInfo
     * @author : zhicong.lin
     * @date : 2022/1/26 15:59
     */
    @Select("select * from task_info where id=#{id}")
    TaskInfo findById(Long id);

    /**
     * 修改任务信息
     *
     * @param info 任务信息
     * @return int
     * @author : zhicong.lin
     * @date : 2022/1/26 16:00
     */
    @Update("update task_info set task_key=#{taskKey},group_key=#{groupKey},host=#{host},port=#{port}," +
            "args=#{args},name=#{name},`desc`=#{desc},cron=#{cron},status=#{status} where id=#{id}")
    int update(TaskInfo info);

    /**
     * 插入任务信息到数据库
     *
     * @param info 任务信息
     * @author : zhicong.lin
     * @date : 2022/1/26 16:01
     */
    @Insert("insert into task_info (host, port, args, name, `desc`, task_key, group_key, cron, status) " +
            "values (#{host},#{port},#{args},#{name},#{desc},#{taskKey},#{groupKey},#{cron},#{status})")
    void insert(TaskInfo info);


    /**
     * 获取服务器执行的任务信息
     *
     * @param serverId 服务器id
     * @param pageSize 每页条数
     * @return java.util.List<com.cgcg.jobs.model.TaskRunRecode>
     * @author : zhicong.lin
     * @date : 2022/1/26 16:01
     */
    @Select("select * from task_run_recode where server_id =#{sId} order by start_time desc limit #{ps}")
    List<TaskRunRecode> findRecodeByServerId(@Param("sId") Long serverId, @Param("ps") int pageSize);
}
