package com.javakc.pms.dispord.listnenr;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.javakc.pms.dispord.entity.DispOrd;
import com.javakc.pms.dispord.service.DispOrdService;
import com.javakc.pms.dispord.vo.DispOrdData;
import org.springframework.beans.BeanUtils;

public class ExcelListener extends AnalysisEventListener<DispOrdData> {

    private DispOrdService dispOrdService;

    public ExcelListener() {

    }

    public ExcelListener(DispOrdService dispOrdService) {
        this.dispOrdService = dispOrdService;
    }

    /**
     * 一行行读取数据时执行的方法
     * @param data
     * @param context
     */
    @Override
    public void invoke(DispOrdData data, AnalysisContext context) {
        // ## 创建 DispOrd 实体
        DispOrd dispOrd = new DispOrd();
        BeanUtils.copyProperties(data, dispOrd);
        // ## 保存
        dispOrdService.saveOrUpdate(dispOrd);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}