package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 4290544131877510487L;
    private String userAccount;
    private String userPassword;
}
