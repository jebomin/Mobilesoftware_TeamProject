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

public class MenuDatabase2 extends SQLiteOpenHelper {

    private static MenuDatabase2 instance;
    public static synchronized MenuDatabase2 getInstance(Context context){
        if (instance == null) {
            instance = new MenuDatabase2(context, "Menu2", null, 1);
        }
        return instance;
    }

    public static final int VERSION = 1;
    public static final String DB_NAME = "Menu2.db";
    public static final String TABLE_NAME = "menu2";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_KCAL = "kcal";


    //변경
    public static final String SQL_CREATE_MENU2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_KCAL + " TEXT, date TEXT" + ");";


    private MenuDatabase2(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MENU2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        }
    }




}
