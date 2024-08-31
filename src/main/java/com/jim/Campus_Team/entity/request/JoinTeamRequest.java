package com.jim.Partner_Match.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class JoinTeamRequest implements Serializable {
    private static final long serialVersionUID = -1574079225593155036L;


    private Long teamId;

    /**
     * 加密队伍的密码
     */
    private String teamPassword;
}
