package com.cgcg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@AllArgsConstructor
@Setter
@Getter
public class User implements Serializable {
    private String name;
    private String code;
}
