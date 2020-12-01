package com.javakc.pms.dispord.client;

import com.javakc.commonutils.api.APICODE;
import com.javakc.pms.dispord.entity.DispOrdRls;
import org.springframework.stereotype.Component;

/**
 * @program:javakc-parent
 * @description:
 * @create:2020-11-30
 */
@Component
public class MesFeignClient implements MesClient{

    @Override
    public APICODE savePmsDispOrdRls(DispOrdRls dispOrdRls) {
        return APICODE.ERROR().message("MES 服务调用失败 - 下达");
    }
}
