package com.example.plantsrecognizer;

import android.content.Context;
import android.util.Log;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XlsParser {

    private List<String> xls_names = new ArrayList<>();
    private List<String> xls_plants = new ArrayList<>();
    private int index_max;
    private Context context;

    public XlsParser(Context current) {
        this.context = current;
        Sheet init_sheet = file_init();
        setXls_names(init_sheet);
        setXls_plants(init_sheet);
    }

    private Sheet file_init() {
        //открываем фай
        InputStream ins = context.getResources().openRawResource(R.raw.data);
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(ins);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        //перем первую страницу
        return wb.getSheetAt(0);
    }

    public String[] getXls_names() {
        return xls_names.toArray(new String[xls_names.size()]);
    }

    private void setXls_names(Sheet sheet) {
        try {
            //читаем первое поле
            Row row = sheet.getRow(0);
            index_max = row.getPhysicalNumberOfCells();
            for (int i = 0; i < index_max; i++) {
                xls_names.add(row.getCell(i).getStringCellValue());
            }
            //Log.d("XLS: ", xls_names.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String[] getXls_plants() {
        return xls_plants.toArray(new String[xls_plants.size()]);
    }

    private void setXls_plants(Sheet sheet) {
        try {
            index_max = 30; //количество растений
            for (int i = 1; i < index_max; i++) {
                Row row = sheet.getRow(i);
                xls_plants.add(row.getCell(0).getStringCellValue());
            }
            //Log.d("XLS: ", xls_plants.toString());
        } catch (Exception ex) {
            Log.d("XLS_ERROR: ", ex.toString());
        }
    }

}
