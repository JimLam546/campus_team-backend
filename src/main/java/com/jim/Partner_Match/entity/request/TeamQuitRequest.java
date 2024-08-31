package com.jim.Partner_Match.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -8460420987559219784L;

    private Long teamId;
}
