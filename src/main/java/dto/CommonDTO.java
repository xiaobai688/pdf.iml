package dto;

import lombok.Data;

/**
 * 通用DTO
 * @author danny
 * @package com.shallnew.danny.authority.dto.common
 * @date 2020/9/15 13:56
 */
@Data
public class CommonDTO {

    /**
     * 属性名
     */
    private String name;

    /**
     * 数量
     */
    private Integer value;

    /**
     * 数量
     */
    private String text;

    public CommonDTO(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public CommonDTO() {
    }
}
