package com.itheima.health.test;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import java.io.FileOutputStream;
import java.io.IOException;

public class importExcelTest {

    @Test
    public void importExcel() throws IOException {
        //内存中创建一个Excel文件
        XSSFWorkbook workbook=new XSSFWorkbook();
        //创建工作表指定表名称
        XSSFSheet sheet=workbook.createSheet("传智播客");
        //创建行，0表示一个单元格
        XSSFRow row=sheet.createRow(0);
        //创建单元格，0表示第一个单元格
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("年龄");
        //创建第二行
        XSSFRow row1=sheet.createRow(1);
        row1.createCell(0).setCellValue("1");
        row1.createCell(1).setCellValue("小明");
        row1.createCell(2).setCellValue("20");
        //创建第三行
        XSSFRow row2=sheet.createRow(2);
        row2.createCell(0).setCellValue("2");
        row2.createCell(1).setCellValue("张三");
        row2.createCell(2).setCellValue("18");
//      通过输出流将workbook对象下载到磁盘
        FileOutputStream out=new FileOutputStream("d:\\itcast1.xlsx");
        workbook.write(out);
        out.flush();//刷新
        out.close();//关闭
        workbook.close();

    }
}
