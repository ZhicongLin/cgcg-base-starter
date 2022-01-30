package com.cgcg.jobs.web.mapper;

import com.cgcg.jobs.model.TaskServer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author zhicong.lin
 */
@Mapper
public interface TaskServerMapper {

    /**
     * 获取服务器信息
     *
     * @param id 主键
     * @return com.cgcg.jobs.model.TaskServer
     * @author : zhicong.lin
     * @date : 2022/1/26 15:35
     */
    @Select("select * from task_server where id=#{id}")
    TaskServer findById(Long id);

    /**
     * 获取服务器列表
     *
     * @param taskId 任务id
     * @param status 状态
     * @return java.util.List<com.cgcg.jobs.model.TaskServer>
     * @author : zhicong.lin
     * @date : 2022/1/26 15:35
     */
    @Select("select * from task_server where task_id = #{tid} and status = #{status}")
    List<TaskServer> findByTaskIdAndStatus(@Param("tid") Long taskId, @Param("status") Integer status);

    /**
     * 根据任务ID查询服务器列表
     *
     * @param taskId 任务id
     * @return java.util.List<com.cgcg.jobs.model.TaskServer>
     * @author : zhicong.lin
     * @date : 2022/1/26 15:34
     */
    @Select("select * from task_server where task_id = #{tid}")
    List<TaskServer> findByTaskId(@Param("tid") Long taskId);

    /**
     * 查询任务服务器列表
     *
     * @param status 状态
     * @return java.util.List<com.cgcg.jobs.model.TaskServer>
     * @author : zhicong.lin
     * @date : 2022/1/26 15:32
     */
    @Select("select * from task_server where status = #{status}")
    List<TaskServer> findByStatus(@Param("status") Integer status);

    /**
     * 修改任务服务器状态
     *
     * @param serverId 服务器id
     * @param status   状态
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 15:30
     */
    @Update("update task_server set status = #{s} where id = #{id}")
    void modifyStatus(@Param("id") Long serverId, @Param("s") Integer status);


}
