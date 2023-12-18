package com.dezhi.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Hello类
 * @author liaodezhi
 * @date 2023/2/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hello implements Serializable {

    /**
     * 消息
     */
    private String message;

    /**
     * 描述
     */
    private String description;
}
