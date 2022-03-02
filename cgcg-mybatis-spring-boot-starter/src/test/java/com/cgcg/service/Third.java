package com.cgcg.service;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * dd
 * </p>
 * @author zhicong.lin
 * @since 2022/3/1 11:41
 */
@Setter
@Getter
@ToString
@TableName("app_menu")
public class Third implements Serializable {

    private Long id;

    private String name;

}
