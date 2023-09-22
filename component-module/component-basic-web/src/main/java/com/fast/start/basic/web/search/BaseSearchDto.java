package com.fast.start.basic.web.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 通用查询基类
 */
@Data
public class BaseSearchDto implements Serializable {

    /**
     * 关键字
     */
    @ApiModelProperty("关键字")
    private String keyword;

    /**
     * 每页记录数
     */
    @ApiModelProperty("每页记录数")
    private int pageSize = 10;
    /**
     * 第几页
     */
    @ApiModelProperty("第几页")
    private int pageNum = 1;


}
