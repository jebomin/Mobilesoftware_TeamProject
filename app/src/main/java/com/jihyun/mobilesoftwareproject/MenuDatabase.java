package com.jihyun.mobilesoftwareproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MenuDatabase extends SQLiteOpenHelper {

    private static MenuDatabase instance;
    public static synchronized MenuDatabase getInstance(Context context){
        if (instance == null) {
            instance = new MenuDatabase(context.getApplicationContext(), "Menu", null, 1);
        }
        return instance;
    }

    public static final int VERSION = 1;
    public static final String DB_NAME = "Menu.db";
    public static final String TABLE_NAME = "menu";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NUM = "num";
    public static final String COLUMN_KCAL = "kcal";
    public static final String COLUMN_IMG = "image";
    public static final String COLUMN_MAP = "map";
    public static final String COLUMN_REVIEW = "review";

    public static final String COLUMN_PRICE = "price";

    public static final String COLUMN_DAY = "day";

    public static final String SQL_CREATE_MENU = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " +
            COLUMN_DATE + " TEXT, " + COLUMN_DAY + " TEXT, " +  // day 컬럼 추가
            COLUMN_TYPE + " TEXT, " + COLUMN_TIME + " TEXT, "
            + COLUMN_NUM + " TEXT, " + COLUMN_KCAL + " TEXT, " + COLUMN_IMG + " TEXT, "
            + COLUMN_PRICE + " TEXT, " + COLUMN_MAP + " TEXT, "+ COLUMN_REVIEW + " TEXT" + ");";

    private MenuDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MENU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    public List<MealCostData> calculateLastMonthCostByType() {
        List<MealCostData> costDataList = new ArrayList<>();

        // 현재 날짜 기준으로 한 달 전 날짜 계산
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        String lastMonthDateStr = lastMonthDate.format(DateTimeFormatter.ISO_DATE);

        // 현재 날짜
        LocalDate currentDate = LocalDate.now();
        String currentDateStr = currentDate.format(DateTimeFormatter.ISO_DATE);

// 데이터베이스에서 해당 기간의 데이터를 가져오기
        String sql = "SELECT " + COLUMN_TYPE + ", SUM(" + COLUMN_PRICE + ") as total_kcal " +
                "FROM " + TABLE_NAME +
                " WHERE " + COLUMN_DAY + " BETWEEN '" + lastMonthDateStr + "' AND '" + currentDateStr + "'" +
                " GROUP BY " + COLUMN_TYPE;

        Cursor cursor = getWritableDatabase().rawQuery(sql, null);

        int typeColumnIndex = cursor.getColumnIndex("type");
        int totalKcalColumnIndex = cursor.getColumnIndex("total_kcal"); // SUM(kcal)의 레이블 사용

        if (typeColumnIndex == -1) {
            Log.e("MenuDatabase2", "COLUMN_TYPE not found in the cursor");
            // 필요한 처리를 여기에 추가
        } else if (totalKcalColumnIndex == -1) {
            Log.e("CustomTag", "total_kcal not found in the cursor");
            // 필요한 처리를 여기에 추가
        } else {
            while (cursor.moveToNext()) {
                String type = cursor.getString(typeColumnIndex);
                int totalCost = cursor.getInt(totalKcalColumnIndex);

                // 개별 데이터 확인을 위한 로그 추가
                Log.d("CustomTag", "Type: " + type + ", Total Cost: " + totalCost);

                // MealCostData 객체 생성 및 리스트에 추가
                MealCostData costData = new MealCostData(type, totalCost);
                costDataList.add(costData);
            }
        }

        // 커서 닫기
        cursor.close();

        // 추가된 로그 출력 코드
        for (MealCostData costData : costDataList) {
            Log.d("MenuDatabase", "Type: " + costData.getType() + ", Total Cost: " + costData.getTotalCost());
        }

        return costDataList;
    }
}
