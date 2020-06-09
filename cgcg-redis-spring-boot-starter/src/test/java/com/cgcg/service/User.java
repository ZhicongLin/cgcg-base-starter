package com.cgcg.service;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class User implements Serializable {
    private String name;
    private String code;
}
