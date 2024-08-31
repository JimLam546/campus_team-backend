package com.jim.Partner_Match.entity.request;

import lombok.Data;


@Data
public class TeamQueryRequest extends PageRequest{

    private static final long serialVersionUID = -2558257455890914505L;
    /**
     * 队伍id
     */
    private Long id;

    /**
     * 搜索关键字（队伍名称和描述查询）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最多人数
     */
    private Integer maxNum;

    /**
     * 队伍创建人
     */
    private Long userId;

    /**
     * 队伍状态(0-公开，1-加密，2-private，3-过期)
     */
    private Integer teamStatus;
}
