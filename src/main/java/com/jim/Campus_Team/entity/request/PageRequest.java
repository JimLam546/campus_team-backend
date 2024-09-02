package com.jim.Campus_Team.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -516724294209138758L;

    /**
     * 当前页号
     */
    private int pageNum;

    /**
     * 一页的数量
     */
    private int pageSize;

    /**
     * 记录总量
     */
    private int total;
}
