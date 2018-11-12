package com.example.plantsrecognizer.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.plantsrecognizer.R;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class XlsParser {

    private ArrayList<String> xlsQuestions = new ArrayList<>();
    private ArrayList<String> xlsPlants = new ArrayList<>();
    private Map<String, ArrayList<String>> xlsAnswers = new HashMap<>();
    private HashSet<String> set = new HashSet<>();
    private final int index_max = 30;         //количество растений;
    private int number_of_questions;
    private Context context;

    public XlsParser(Context current) {
        this.context = current;
        setXlsQuestions(file_init(0));
        setXlsPlants(file_init(0));
        setXlsAnswers(file_init(0));
    }

    public String[] getXlsQuestions() {
        return xlsQuestions.toArray(new String[xlsPlants.size()]);
    }

    private void setXlsQuestions(Sheet sheet) {
        try {
            //читаем первое поле
            Row row = sheet.getRow(0);
            int tmp = row.getPhysicalNumberOfCells();
            for (int i = 1; i < tmp; i++) {
                xlsQuestions.add(row.getCell(i).getStringCellValue());
            }
            //Log.d("XLS: ", xls_names.toString());
            this.number_of_questions = xlsQuestions.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String[] getXlsPlants() {
        return xlsPlants.toArray(new String[xlsPlants.size()]);
    }

    private void setXlsPlants(Sheet sheet) {
        try {
            for (int i = 1; i < index_max; i++) {
                Row row = sheet.getRow(i);
                xlsPlants.add(row.getCell(0).getStringCellValue());
            }
            //Log.d("XLS: ", xls_plants.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> getXlsAnswers(String key) {
        return xlsAnswers.get(key);
    }

    private Sheet file_init(int pageNumber) {
        //открываем фай
        InputStream ins = context.getResources().openRawResource(R.raw.data);
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(ins);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        //перем первую страницу
        return Objects.requireNonNull(wb).getSheetAt(pageNumber);
    }

    private void setXlsAnswers(Sheet sheet) {
        String raw_string;
        try {
            for (int i = 1; i <= number_of_questions; i++) {
                for (int j = 1; j < index_max; j++) {
                    Row row = sheet.getRow(j);
                    try {
                        raw_string = row.getCell(i).getStringCellValue();
                        if (!raw_string.equals("-")) {
                            raw_string = setFirstSymUpper(raw_string);
                        } else {
                            raw_string = "Не знаю/Не могу определить";
                        }
                    } catch (IllegalStateException e) {
                        raw_string = Integer.toString((int) row.getCell(i).getNumericCellValue());
                    }
                    if (raw_string.contains(",")) {
                        String[] raw_array = raw_string.split(",");
                        for (String element : raw_array) {
                            set.add(setFirstSymUpper(element));
                        }
                    } else {
                        set.add(raw_string);
                    }
                }

                if (xlsQuestions.get(i - 1).equals("Древовидный ли ствол")) {
                    set.add("Не древовидный");
                    set.add("Не знаю/Не могу определить");
                }

                xlsAnswers.put(xlsQuestions.get(i - 1), new ArrayList<>(Arrays.asList(set.toArray(new String[set.size()]))));
                set.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @NonNull
    private String setFirstSymUpper(String raw_string) {
        char[] tmp = raw_string.toCharArray();
        tmp[0] = Character.toUpperCase(tmp[0]);
        return new String(tmp);
    }
}
