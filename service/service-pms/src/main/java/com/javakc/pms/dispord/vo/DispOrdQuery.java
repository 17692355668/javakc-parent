package com.javakc.pms.dispord.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 封装查询条件 - 调度指令库
 */

@Data
@Getter
@Setter
public class DispOrdQuery {


    private String orderName;

    private String beginDate;

    private String endDate;


}
