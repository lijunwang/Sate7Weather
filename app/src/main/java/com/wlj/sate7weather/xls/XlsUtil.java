package com.wlj.sate7weather.xls;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.wlj.sate7weather.R;
import com.wlj.sate7weather.db.CopyDB2SD;
import com.wlj.sate7weather.db.WeatherDB;
import com.wlj.sate7weather.db.WeatherDao;
import com.wlj.sate7weather.db.WeatherRegion;
import com.wlj.sate7weather.db.WeatherType;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.wlj.sate7weather.utils.XLogKt.log;
import static com.wlj.sate7weather.utils.XLogKt.loge;

public class XlsUtil {
    private static WeatherDao dao;

    public static void init(Context context) {
        dao = WeatherDB.Companion.getInstance(context).weatherDao();
        int xlsWeatherType = R.raw.wether_type;
        int xlsWeatherRegion = R.raw.weather_city_region;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                log("------start parse weather.db.db type -----");
                readXls(context, xlsWeatherType, WeatherType.class);
//                log("------start parse weather.db.db region -----");
                readXls(context, xlsWeatherRegion, WeatherRegion.class);
                CopyDB2SD.Companion.save2SD(context);

                List<WeatherRegion> regionList = dao.queryAllWeatherRegion();
                List<WeatherType> typeList = dao.queryAllWeatherType();
                log("------------list of region ------------");
                log(regionList.toString());
                log("------------list of types ------------");
                log(typeList.toString());
            }
        };
        new Thread(r).start();
    }

    private static void readXls(Context context, int rawId, Class toBean) {
        long start = System.currentTimeMillis();
//        InputStream stream = context.getResources().openRawResource(R.raw.wether_type);
//        InputStream stream = context.getResources().openRawResource(R.raw.weather_city_region);
        try (InputStream stream = context.getResources().openRawResource(rawId)) {
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                JsonObject jsonObject = new JsonObject();
                for (int c = 0; c < cellsCount; c++) {
                    String value = getCellAsString(row, c, formulaEvaluator,toBean.getName().equals(WeatherType.class.getName()) ? false : true);
                    sb.append(value).append("\t");
                    if (r > 0) {
                        parsToBean(c,value,toBean,jsonObject);
                    }
                }
                String cellInfo = "row[" + r + "] == " + sb.toString();
                log(cellInfo);
                sb.replace(0, sb.length(), "");
//                log("json == " + jsonObject);
                if(r>0){
                    insertToDB(jsonObject,toBean);
                }
            }
        } catch (Exception e) {
            /* proper exception handling to be here */
            log("readXls Exception ..." + e.getMessage());
        }
        log("time spend == " + (System.currentTimeMillis() - start));
    }

    private static void parsToBean(int columnIndex, String value, Class toBean,JsonObject jsonObject) {
        if (toBean.getName().equals(WeatherType.class.getName())) {
            switch (columnIndex) {
                case 0:
                    jsonObject.addProperty("weatherType", Integer.parseInt(value));
                    break;
                case 1:
                    jsonObject.addProperty("nameCN", value);
                    break;
                case 2:
                    jsonObject.addProperty("nameEn", value);
                    break;
                default:
                    loge("parse weather.db.db type error!!");
            }
        }else if(toBean.getName().equals(WeatherRegion.class.getName())){
            switch (columnIndex) {
                case 0:
                    jsonObject.addProperty("regionId", Integer.parseInt(value));
                    break;
                case 1:
                    jsonObject.addProperty("cityName", String.valueOf(value));
                    break;
                default:
                    loge("parse weather.db.db region error!!");
            }
        }
    }
    private static long insertToDB(JsonObject jsonObject,Class type){
        if (type.getName().equals(WeatherType.class.getName())){
            WeatherType weatherType = new Gson().fromJson(jsonObject.toString(),WeatherType.class);
            long id = dao.insertWeatherType(weatherType);
//            log("insert id == " + id + ",weatherType == " + weatherType);
        }else if(type.getName().equals(WeatherRegion.class.getName())){
            WeatherRegion weatherRegion = new Gson().fromJson(jsonObject.toString(),WeatherRegion.class);
            long id = dao.insertWeatherRegion(weatherRegion);
//            log("insert id == " + id + ",weatherRegion == " + weatherRegion);
        }
        return 0;
    }



    private static String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator,boolean big) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cellValue.getBooleanValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        if(big){
                            BigDecimal bg = new BigDecimal(Double.toString(numericValue));
                            value = "" + bg.toPlainString();
                        }else{
                            int nub = (int) numericValue;
                            value = String.valueOf(nub);
                        }

                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            log("NullPointerException ... " + e.getMessage());
        }
        return value;
    }
}
