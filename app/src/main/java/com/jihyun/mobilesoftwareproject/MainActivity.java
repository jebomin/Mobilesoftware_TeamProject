package com.jihyun.mobilesoftwareproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Clickevent {
    private MenuDatabase menuDatabase;
    public static final String TABLE_NAME = "menu";
    SQLiteDatabase database;

    //메뉴 이름과 칼로리 정보를 담고 있는 데이터베이스
    private MenuDatabase2 menuDatabase2;
    public static final String TABLE_NAME2 = "menu2";
    SQLiteDatabase database2;

    TextView monthYear;
    TextView choose_date;
    LocalDate selectedDate;
    RecyclerView recyclerView;
    RecyclerView mRecyclerView;
    MenuRecyclerAdapter mRecyclerAdapter;
    ArrayList<Menudata> menudata;
    TextView sum_kcal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        mRecyclerAdapter = new MenuRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menudata = new ArrayList<>();
        sum_kcal = findViewById(R.id.sum_kcal);

        menuDatabase = MenuDatabase.getInstance(this);
        database = menuDatabase.getWritableDatabase();

        menuDatabase2 = MenuDatabase2.getInstance(this);
        database2 = menuDatabase2.getWritableDatabase();

        //메뉴 이름과 칼로리 정보 임의로 넣기
        database2.execSQL("DELETE FROM " + TABLE_NAME2);
        insertmenu2("쌀밥", "300");
        insertmenu2("흑미밥", "300");
        insertmenu2("짜장면", "700");
        insertmenu2("라면", "520");
        insertmenu2("햄버거", "400");
        insertmenu2("단팥크림빵", "388");
        insertmenu2("삼겹살김치철판", "388");
        insertmenu2("치즈불닭철판", "388");
        insertmenu2("데리야끼치킨솥밥", "388");
        insertmenu2("숯불삼겹솥밥", "388");
        insertmenu2("콘치즈솥밥", "388");
        insertmenu2("냉모밀", "388");
        insertmenu2("냉모밀세트", "388");
        insertmenu2("커피", "80" );
        insertmenu2("오렌지주스", "150" );
        insertmenu2("콜라", "100" );
        insertmenu2("생수", "0" );
        insertmenu2("탄산수", "5" );

        monthYear = findViewById(R.id.monthYear);
        ImageButton pre_but = findViewById(R.id.pre_but);
        ImageButton next_but = findViewById(R.id.next_but);
        recyclerView = findViewById(R.id.recyclerView);
        selectedDate = LocalDate.now();
        setMonthView();
        Click_date(String.valueOf(selectedDate.getDayOfMonth()));

        pre_but.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();
            }
        });

       next_but.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selectedDate = selectedDate.plusMonths(1);
                setMonthView();
            }
        });

        ImageButton input_button = findViewById(R.id.input_button);
        String Curr_date = choose_date.getText().toString();

        input_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String Curr_date = choose_date.getText().toString();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("select_date", Curr_date);
                startActivity(intent);
            }
        });

        //바꾼부분
        ImageButton input_button2 = findViewById(R.id.input_button2);
        input_button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                Intent intent = new Intent(MainActivity.this, Analyze.class);

                startActivity(intent);
            }
        });
    }


    private String printDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy\nMM");
        return date.format(formatter);
    }

    private String printDate2(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월");
        return date.format(formatter);
    }


    private void setMonthView(){
        monthYear.setText(printDate(selectedDate));
        String content = monthYear.getText().toString();
        SpannableString spannableString = new SpannableString(content);
        String word = String.valueOf(monthYear);
        spannableString.setSpan(new RelativeSizeSpan(2.5f), 5, 7, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        monthYear.setText(spannableString);

        ArrayList<String> dayarray = date_arr(selectedDate);
        DateDecision decision = new DateDecision(dayarray, MainActivity.this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(decision);
    }

    private ArrayList<String> date_arr(LocalDate date){
        ArrayList<String> dayarray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int Last_date = yearMonth.lengthOfMonth();
        LocalDate First_date = selectedDate.withDayOfMonth(1);
        int First_day = First_date.getDayOfWeek().getValue();
        for(int i = 1; i < 42; i++){
            if(i <= First_day || i > Last_date + First_day){
                dayarray.add(null);
            }
            else
            {
                dayarray.add(String.valueOf(i - First_day));
            }
        }
        return dayarray;
    }

    @Override
    public void Click_date(String day) {
        if(day != null)
        {
            sum_kcal.setText("총 칼로리 : 0kcal");
            menudata.clear();
            mRecyclerAdapter.setmenulist(menudata);
            int year = selectedDate.getYear();
            int monthValue = selectedDate.getMonthValue();
            LocalDate selectedDate2 = LocalDate.of(year, monthValue, Integer.parseInt(day));
            DayOfWeek dayOfWeek = selectedDate2.getDayOfWeek();
            String Date= printDate2(selectedDate) + " " + day + "일 " + dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN);
            choose_date = findViewById(R.id.choose_date);
            choose_date.setText(Date);

            // "yyyy-MM-dd" 형식으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = selectedDate2.format(formatter);

            addtext(TABLE_NAME, choose_date.getText().toString(),formattedDate);
        }
    }

    private void addtext(String t_name, String date, String formattedDate){
        if (database != null) {

            // day 컬럼에는 "yyyy-MM-dd" 형식의 문자열이 저장됩니다.
            ContentValues values = new ContentValues();
            values.put("day", formattedDate);
            database.update(t_name, values, "date = ?", new String[] { date });
            

            String sql = "SELECT id, type, time, name, num, kcal, day FROM " + t_name + " WHERE date = \"" + date + "\"";
            Cursor cursor = database.rawQuery(sql, null);
            int a = 0;
            for(int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String type = cursor.getString(1);
                String time = cursor.getString(2);
                String name = cursor.getString(3);
                int num = Integer.parseInt(cursor.getString(4));
                int kcal = Integer.parseInt(cursor.getString(5));
                String day = cursor.getString(6); // day 컬럼 추가


                // 로그로 출력
                Log.d("DB_DATA", "ID: " + id + ", Type: " + type + ", Time: " + time +
                        ", Name: " + name + ", Num: " + num + ", Kcal: " + kcal + ", Day: " + day + "date: "+ date);


                int total_kcal = num * kcal;
                a = a + total_kcal;
                menudata.add(new Menudata(type, time + " / " + name, day, total_kcal, id)); // 날짜를 day로 변경
            }
            sum_kcal.setText("총 칼로리 : " + a + "kcal");
            cursor.close();
        }
    }




    private void insertmenu2(String name, String kcal) {
        if (database2 != null) {
            String sql = "INSERT INTO menu2(name, kcal) VALUES(?, ?)";
            Object[] params = {name, kcal};
            database2.execSQL(sql, params);
        }
    }

}