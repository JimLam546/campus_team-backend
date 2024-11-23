package com.jim.Campus_Team.entity.request;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -516724294209138758L;

    /**
     * 当前页号
     */
    private Integer pageNum;

    /**
     * 一页的数量
     */
    private Integer pageSize;

    /**
     * 记录总量
     */
    private Integer total;
}
