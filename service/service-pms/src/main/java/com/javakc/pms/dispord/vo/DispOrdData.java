package com.javakc.pms.dispord.vo;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 设置表头和添加的数据字段
 * @author leesiyu
 * @version V1.0
 * @Copyright © 北京汇才同飞教育科技公司
 */
@Data
public class DispOrdData {

    @ExcelProperty(value = "指令名称", index = 0)
    private String orderName;

    @ExcelProperty(value = "指令类型", index = 1)
    private int specType;

    @ExcelProperty(value = "优先级", index = 2)
    private int priority;

    @ExcelProperty(value = "指令描述", index = 3)
    private String orderDesc;

}