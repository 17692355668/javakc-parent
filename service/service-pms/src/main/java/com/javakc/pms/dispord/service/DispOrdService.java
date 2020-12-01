package com.javakc.pms.dispord.service;

import com.javakc.commonutils.jpa.base.service.BaseService;
import com.javakc.commonutils.jpa.dynamic.SimpleSpecificationBuilder;
import com.javakc.pms.dispord.dao.DispOrdDao;
import com.javakc.pms.dispord.entity.DispOrd;
import com.javakc.pms.dispord.vo.DispOrdQuery;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DispOrdService extends BaseService<DispOrdDao,DispOrd> {

    @Autowired
    private DispOrdDao dispOrdDao;

    /**
     * 查询所有调度指令库
     * @return
     */
    public List<DispOrd> findAll() {
        List<DispOrd> list = dispOrdDao.findAll();
        return list;
    }

    /**
     * 根据条件进行分页查询
     * @param dispOrdQuery
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<DispOrd> findPageDispOrd(DispOrdQuery dispOrdQuery, int pageNum, int pageSize) {
        SimpleSpecificationBuilder<DispOrd> simpleSpecificationBuilder = new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(dispOrdQuery.getOrderName())) {
            /**
             * 可用操作符
             * = 等值、!= 不等值 (字符串、数字)
             * >=、<=、>、< (数字)
             * ge，le，gt，lt(字符串)
             * :表示like %v%
             * l:表示 v%
             * :l表示 %v
             * null表示 is null
             * !null表示 is not null
             */
            simpleSpecificationBuilder.and("orderName", ":", dispOrdQuery.getOrderName());
        }
        if (!StringUtils.isEmpty(dispOrdQuery.getBeginDate()))
        {
            simpleSpecificationBuilder.and("gmtCreate","ge",dispOrdQuery.getBeginDate());
        }
        if (!StringUtils.isEmpty(dispOrdQuery.getEndDate()))
        {
            simpleSpecificationBuilder.and("gmtCreate","lt",dispOrdQuery.getEndDate());
        }
        return dao.findAll(simpleSpecificationBuilder.getSpecification(), PageRequest.of(pageNum - 1, pageSize));
    }

    /**
     * 导出Excel
     * @param response
     */
    public void exportExcel(HttpServletResponse response) {
        // ## 设置表头值
        String[] titles = {"指令名称", "优先级", "指令类型", "指令描述"};
        // ## 1.创建workbook,对应一个Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        // ## 2.在workbook中创建一个sheet
        HSSFSheet hssfSheet = workbook.createSheet();
        // ## 3.在sheet中添加表头,第0行
        HSSFRow hssfRow = hssfSheet.createRow(0);
        // ## 4.在第0行设置表头数据
        for (int i = 0; i < titles.length; i++) {
            hssfRow.createCell(i).setCellValue(titles[i]);
        }

        // ## 5.查询并写入数据
        List<DispOrd> dispOrdList = dao.findAll();
        if (null != dispOrdList) {
            for (int i = 0; i < dispOrdList.size(); i++) {
                DispOrd dispOrd = dispOrdList.get(i);
                // ## 创建行
                HSSFRow row = hssfSheet.createRow(i + 1);
                row.createCell(0).setCellValue(dispOrd.getOrderName());
                row.createCell(1).setCellValue(dispOrd.getPriority());
                row.createCell(2).setCellValue(dispOrd.getSpecType());
                row.createCell(3).setCellValue(dispOrd.getOrderDesc());
            }
        }
        String fileName = new String(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        try {
            // ## 6.将Excel文件输出到客户端浏览器
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入Excel
     * @param file
     */
    @Transactional
    public void importExcel(MultipartFile file) {
        try {
            // ## 获取文件流
            InputStream inputStream = file.getInputStream();
            // ## 声明一个Excel接口
            Workbook workbook = null;
            // ## 根据文件后缀赋予不同POIExcel对象
            if (file.getOriginalFilename().endsWith(".xlsx")) {
                // ## 处理2007版及以上Excel
                workbook = new XSSFWorkbook(inputStream);
            } else {
                // ## 处理2003版本Excel
                workbook = new HSSFWorkbook(inputStream);
            }
            // ## 得到当前excel中的sheet总数
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                // ## 每循环一次,通过当前下标依次获取sheet
                Sheet sheet = workbook.getSheetAt(i);
                // ## 通过sheet得到当前共多少行
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

                List<DispOrd> list = new ArrayList<>();
                // ## 循环所有的行,j从1开始,因为0为第一行,第一行为表头
                for (int j = 1; j < physicalNumberOfRows; j++) {
                    // ## 每循环一次,通过当前下标一行一行的获取
                    Row row = sheet.getRow(j);
                    DispOrd dispOrd = new DispOrd();
                    dispOrd.setOrderName(row.getCell(0).getStringCellValue());
                    dispOrd.setPriority((int) row.getCell(1).getNumericCellValue());
                    dispOrd.setSpecType((int) row.getCell(2).getNumericCellValue());
                    dispOrd.setOrderDesc(row.getCell(3).getStringCellValue());
                    list.add(dispOrd);
                }
                // ## 批量添加
                dao.saveAll(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}