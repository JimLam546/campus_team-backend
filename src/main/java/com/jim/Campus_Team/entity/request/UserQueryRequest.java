package com.jim.Campus_Team.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jim_Lam
 * @description UserQueryRequest
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 8233505000037854688L;

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 性别：1男 0女
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否有效(是否被封号)
     */
    private Integer isValid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 角色
     */
    private String userRole;

}