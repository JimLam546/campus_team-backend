package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteTeamRequest implements Serializable {
    private static final long serialVersionUID = 6927373519855409332L;

    private long teamId;
}
