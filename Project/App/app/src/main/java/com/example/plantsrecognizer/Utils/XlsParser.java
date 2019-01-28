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
import java.util.List;
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
        setXlsQuestions(fileInit(0));
        setXlsPlants(fileInit(0));
        setXlsAnswers(fileInit(0));
    }

    public String[] getXlsQuestions() {
        return xlsQuestions.toArray(new String[xlsPlants.size()]);
    }

    public String[] getXlsPlants() {
        return xlsPlants.toArray(new String[xlsPlants.size()]);
    }

    public ArrayList<String> getXlsAnswers(String key) {
        return xlsAnswers.get(key);
    }

    private void setXlsQuestions(Sheet sheet) {
        try {
            //читаем первое поле
            Row row = sheet.getRow(0);
            int tmp = row.getPhysicalNumberOfCells();
            for (int i = 1; i < tmp; i++) {
                xlsQuestions.add(row.getCell(i).getStringCellValue());
            }
            this.number_of_questions = xlsQuestions.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setXlsPlants(Sheet sheet) {
        try {
            for (int i = 1; i < index_max; i++) {
                Row row = sheet.getRow(i);
                xlsPlants.add(row.getCell(0).getStringCellValue());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Sheet fileInit(int pageNumber) {
        //открываем файл
        InputStream ins = context.getResources().openRawResource(R.raw.data);
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(ins);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        //берем первую страницу
        return Objects.requireNonNull(wb).getSheetAt(pageNumber);
    }

    private String getStringFromXls(Row row, int index) {
        String current_string;
        try {
            current_string = row.getCell(index).getStringCellValue();
            if (!current_string.equals("-")) {
                current_string = setFirstSymUpper(current_string);
            } else {
                current_string = "Не знаю/Не могу определить";
            }

        } catch (IllegalStateException e) {
            //If value equivalent to int
            current_string = Integer.toString((int) row.getCell(index).getNumericCellValue());
        }
        return current_string;
    }

    private void parseStringFromXls(String raw_string) {
        if (raw_string.contains(",")) {
            String[] raw_array = raw_string.split(",");
            for (String element : raw_array) {
                set.add(setFirstSymUpper(element));
            }
        } else {
            set.add(setFirstSymUpper(raw_string));
        }
    }

    private ArrayList<String> getSetConvert() {
        List<String> convert;
        convert = Arrays.asList(set.toArray(new String[set.size()]));
        ArrayList<String> converted = new ArrayList<>(convert);
        return converted;
    }

    private void getDataFromXls(Row row, int index) {

        String raw_string = getStringFromXls(row, index);
        parseStringFromXls(raw_string);

        xlsAnswers.put(xlsQuestions.get(index - 1), getSetConvert());
    }

    private void setXlsAnswers(Sheet sheet) {
        try {
            for (int i = 1; i <= number_of_questions; i++) {
                for (int j = 1; j < index_max; j++) {
                    Row row = sheet.getRow(j);
                    getDataFromXls(row, i);
                }
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
