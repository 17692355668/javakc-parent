package com.javakc.pms.dispord.controller;

import com.alibaba.excel.EasyExcel;
import com.javakc.commonutils.api.APICODE;
import com.javakc.pms.dispord.entity.DispOrd;
import com.javakc.pms.dispord.listnenr.ExcelListener;
import com.javakc.pms.dispord.service.DispOrdService;
import com.javakc.pms.dispord.vo.DispOrdData;
import com.javakc.pms.dispord.vo.DispOrdQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "调度指令库控制器")
@RestController
@RequestMapping("/pms/dispord")

public class DisOrdController {
    @Autowired
    private DispOrdService dispOrdService;


    @ApiOperation(value = "查询所有指令库1")
    @GetMapping
    public APICODE findAll() {
        List<DispOrd> list = dispOrdService.findAll();
        return APICODE.OK().data("items", list);
    }


    @ApiOperation(value = "根据条件进行分页查询")
    @PostMapping("{pageNum}/{pageSize}")
    public APICODE findPageDispOrd(@RequestBody(required = false) DispOrdQuery dispOrdQuery, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        Page<DispOrd> page = dispOrdService.findPageDispOrd(dispOrdQuery, pageNum, pageSize);
        long totalElements = page.getTotalElements();
        List<DispOrd> dispOrdList = page.getContent();
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
    public APICODE getDispOrdById(@PathVariable("dispOrdId") String dispOrdId) {
        DispOrd dispOrd = dispOrdService.getById(dispOrdId);
        return APICODE.OK().data("dispOrd", dispOrd);
    }


    @ApiOperation(value = "修改调度指令库")
    @PutMapping("updateDispOrd")
    public APICODE updateDispOrd(@RequestBody DispOrd dispOrd) {
        dispOrdService.saveOrUpdate(dispOrd);
        return APICODE.OK();
    }

    @ApiOperation(value = "根据ID删除调度指令库")
    @DeleteMapping("{dispOrdId}")
    public APICODE deleteDispOrdById(@PathVariable("dispOrdId") String dispOrdId) {
        dispOrdService.removeById(dispOrdId);
        return APICODE.OK();
    }


    @ApiOperation(value = "列表导出", notes = "使用阿里EasyExcel导出Excel格式的列表数据")
    @GetMapping("exportEasyExcel")
    public APICODE exportEasyExcel(HttpServletResponse response) {
        // ## 查询调度指令库
        List<DispOrd> dispOrdList = dispOrdService.findAll();
        // ## 定义导出列表集合
        List<DispOrdData> dispOrdDataList = new ArrayList<>();

        for (DispOrd dispOrd : dispOrdList) {
            DispOrdData dispOrdData = new DispOrdData();
            BeanUtils.copyProperties(dispOrd, dispOrdData);
            dispOrdDataList.add(dispOrdData);
        }

        //文件名
        String fileName = "dispOrdList";

        try {
            // ## 设置响应信息
            response.reset();
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8") + ".xlsx");
            EasyExcel.write(response.getOutputStream(), DispOrdData.class).sheet("指令列表").doWrite(dispOrdDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @ApiOperation(value = "列表导入", notes = "使用阿里EasyExcel导入Excel格式的列表数据")
    @PostMapping("importEasyExcel")
    public void importEasyExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DispOrdData.class, new ExcelListener(dispOrdService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "列表导出", notes = "使用POI导出Excel格式的列表数据")
    @GetMapping("exportExcel")
    public void exportExcel(HttpServletResponse response) {
        dispOrdService.exportExcel(response);
    }


    @ApiOperation(value = "列表导入", notes = "使用POI导入Excel格式的列表数据")
    @PostMapping("importExcel")
    public void importExcel(MultipartFile file) {
        dispOrdService.importExcel(file);
    }


}
