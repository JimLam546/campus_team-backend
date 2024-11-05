package com.jim.Campus_Team.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jim_Lam
 * @date 2024-11-05 21:47
 * @description MessageRequest
 */

@Data
@ApiModel(value = "消息请求")
public class MessageRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 4431753381174415046L;

    /**
     * 为id
     */
    @ApiModelProperty(value = "接收id")
    private Long toId;
    /**
     * 团队id
     */
    @ApiModelProperty(value = "队伍id")
    private Long teamId;
    /**
     * 文本
     */
    @ApiModelProperty(value = "文本")
    private String text;
    /**
     * 聊天类型
     */
    @ApiModelProperty(value = "聊天类型")
    private Integer chatType;
    /**
     * 是管理
     */
    @ApiModelProperty(value = "是否为管理员")
    private boolean isAdmin;
}