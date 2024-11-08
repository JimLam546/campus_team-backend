package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @date 2024-11-06 16:09
 * @description ChatRequest
 */

@Data
public class ChatRequest implements Serializable {
    private static final long serialVersionUID = -2666766923028333519L;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 接收用户
     */
    private Long toId;
}