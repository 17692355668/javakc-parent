package com.javakc.pms.dispord.controller;

import com.javakc.commonutils.api.APICODE;
import com.javakc.pms.dispord.entity.DispOrd;
import com.javakc.pms.dispord.service.DispOrdService;
import com.javakc.pms.dispord.vo.DispOrdQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "调度指令库控制器")
@RestController
@RequestMapping("/pms/dispord")
@CrossOrigin
public class DisOrdController {
    @Autowired
    private DispOrdService dispOrdService;


    @ApiOperation(value = "查询所有指令库1")
    @GetMapping
    public APICODE findAll() {
        List<DispOrd> list = dispOrdService.findAll();
        int i = 1/0;
        return APICODE.OK().data("items", list);
    }


    @ApiOperation(value = "根据条件进行分页查询")
    @PostMapping("{pageNum}/{pageSize}")
    public APICODE findPageDispOrd(@RequestBody(required = false) DispOrdQuery dispOrdQuery,@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize) {
        Page<DispOrd> page = dispOrdService.findPageDispOrd(dispOrdQuery, pageNum, pageSize);
        List<DispOrd> dispOrdList = page.getContent();
        long totalElements = page.getTotalElements();
        return APICODE.OK().data("total", totalElements).data("items", dispOrdList);
    }


    @ApiOperation(value = "新增调度指令库")
    @PostMapping("saveDispOrd")
    public APICODE saveDispOrd(@RequestBody DispOrd dispOrd) {
        dispOrdService.saveOrUpdate(dispOrd);
        return APICODE.OK();
    }

    @ApiOperation(value = "根据ID获取调度指令库")
    @GetMapping("{dispOrdId}")
    public APICODE getDispOrdById(@PathVariable(name = "dispOrdId") String dispOrdId) {
        DispOrd dispOrd = dispOrdService.getById(dispOrdId);
        return APICODE.OK().data("dispOrd", dispOrd);
    }


    @ApiOperation(value = "修改调度指令库")
    @PutMapping
    public APICODE updateDispOrd(@RequestBody DispOrd dispOrd) {
        dispOrdService.saveOrUpdate(dispOrd);
        return APICODE.OK();
    }

    @ApiOperation(value = "根据ID删除调度指令库")
    @DeleteMapping("{dispOrdId}")
    public APICODE deleteDispOrdById(@PathVariable(name = "dispOrdId") String dispOrdId) {
        dispOrdService.removeById(dispOrdId);
        return APICODE.OK();
    }



}
