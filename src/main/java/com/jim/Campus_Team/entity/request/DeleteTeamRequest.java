package com.jim.Partner_Match.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteTeamRequest implements Serializable {
    private static final long serialVersionUID = 6927373519855409332L;

    private long teamId;
}
