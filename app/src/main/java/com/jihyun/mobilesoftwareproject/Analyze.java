package com.jihyun.mobilesoftwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


public class Analyze extends AppCompatActivity {

    PieChart pieChart;
    public static final String TABLE_NAME = "menu";
    SQLiteDatabase database;
    private TextView monthYear;
    private TextView totalCalorieTextView;

    private TextView morningTextView;
    private TextView lunchTextView;
    private TextView eveningTextView;
    private TextView beverageTextView;

    private LocalDate currentDate;
    private TextView monthYearTextView;

    private int totalMorningCost = 0;
    private int totalLunchCost = 0;
    private int totalEveningCost = 0;
    private int totalBeverageCost = 0;

    private BarChart barChart;

    ImageButton checkbutton;

    private void calculateLastMonthCalories() {
        // 현재 날짜 기준으로 한 달 전 날짜 계산
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        String lastMonthDateStr = lastMonthDate.format(DateTimeFormatter.ISO_DATE);
        String currentDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        // 한 달 동안의 총 칼로리를 계산할 변수
        int totalCalories = 0;

        // 데이터베이스에서 해당 기간의 데이터를 가져오기
        String sql = "SELECT kcal FROM " + TABLE_NAME + " WHERE day BETWEEN '" + lastMonthDateStr + "' AND '" + currentDateStr + "'";
        Cursor cursor = database.rawQuery(sql, null);

        // 디버깅을 위한 로그
        Log.d("CustomTag", "lastMonthDate: " + lastMonthDateStr);
        Log.d("CustomTag", "SQL: " + sql);

        // 결과에서 칼로리를 누적
        while (cursor.moveToNext()) {
            int kcal = cursor.getInt(0);
            totalCalories += kcal;

            // 개별 kcal 값 확인을 위한 로그 추가
            Log.d("CustomTag", "Cursor에서 가져온 Kcal: " + kcal);
        }

        // TextView에 총 칼로리 설정
        totalCalorieTextView.setText("총 칼로리: " + totalCalories + " kcal");

        // 한 달 전 날짜부터 현재 날짜까지의 기간을 포맷팅하여 monthYear에 설정
        String formattedPeriod = lastMonthDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + " ~ " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        monthYear.setText(formattedPeriod);

        // 가격 로그 추가
        Log.d("CustomTag", "Total Calories: " + totalCalories);

        // 커서 닫기
        cursor.close();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // 레이아웃을 인플레이트하고 초기화
        setContentView(R.layout.activity_analyze);

        // 뷰 초기화

        totalCalorieTextView = findViewById(R.id.total_kcal);
        monthYear = findViewById(R.id.monthYear2);
        morningTextView = findViewById(R.id.morning);
        lunchTextView = findViewById(R.id.lunch);
        eveningTextView = findViewById(R.id.evening);
        beverageTextView = findViewById(R.id.beverage);
        checkbutton = findViewById(R.id.check_button);
        pieChart = findViewById(R.id.piechart);

//        barChart = findViewById(R.id.chart);

        // currentDate 초기화
        currentDate = LocalDate.now();


        // 데이터베이스 초기화
        MenuDatabase menuDatabase = MenuDatabase.getInstance(this);
        database = menuDatabase.getWritableDatabase();


        // 최근 한 달 간의 총 칼로리 계산 및 설정
        calculateLastMonthCalories();

        // Intent로 전달된 데이터 받기
        int selectedId = intent.getIntExtra("selected_id", -1);

        //id = intent.getExtras().getInt("id");

        // 최근 한 달 간의 식사 비용 분석 및 표시
        analyzeLastMonthCostByType();

        // 차트 업데이트를 위해 메소드 호출
         updateChart();


        checkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Analyze.this, MainActivity.class);
                startActivity(intent1);
            }
        });


    }
    private void analyzeLastMonthCostByType() {
        // MenuDatabase 클래스를 사용하여 데이터베이스에서 종류별 비용 가져오기
        MenuDatabase menuDatabase = MenuDatabase.getInstance(this);

        // 여기서 MenuDatabase의 calculateLastMonthCostByType 메서드 호출
        List<MealCostData> costDataList = menuDatabase.calculateLastMonthCostByType();

        for (MealCostData costData : costDataList) {
            Log.d("MenuDatabase2", "Type: " + costData.getType() + ", Total Cost: " + costData.getTotalCost());
        }

        // 종류별로 TextView에 비용 표시
        for (MealCostData costData : costDataList) {
            String type = costData.getType();
            int totalCost = costData.getTotalCost();

            switch (type) {
                case "Breakfast":
                    totalMorningCost += totalCost;
                    // 가격 로그 추가
                    Log.d("MenuDatabase3", "Morning Cost: " + totalCost);
                    Log.d("MenuDatabase3", "Total Morning Cost: " + totalMorningCost);
                    break;
                case "Lunch":
                    totalLunchCost += totalCost;
                    // 가격 로그 추가
                    Log.d("MenuDatabase3", "Lunch Cost: " + totalCost);
                    Log.d("MenuDatabase3", "Total Lunch Cost: " + totalLunchCost);
                    break;
                case "Dinner":
                    totalEveningCost += totalCost;
                    // 가격 로그 추가
                    Log.d("MenuDatabase3", "Evening Cost: " + totalCost);
                    Log.d("MenuDatabase3", "Total Evening Cost: " + totalEveningCost);
                    break;
                case "Beverage":
                    totalBeverageCost += totalCost;
                    // 가격 로그 추가
                    Log.d("MenuDatabase3", "Beverage Cost: " + totalCost);
                    Log.d("MenuDatabase3", "Total Beverage Cost: " + totalBeverageCost);
                    break;
            }
        }
        // 최종 결과를 TextView에 설정
        morningTextView.setText("조식 비용: " + totalMorningCost + "원");
        lunchTextView.setText("중식 비용: " + totalLunchCost + "원");
        eveningTextView.setText("석식 비용: " + totalEveningCost + "원");
        beverageTextView.setText("음료 비용: " + totalBeverageCost + "원");
    }

    // 날짜를 특정 형식으로 출력하는 메서드
    private String printDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy\nMM");
        return date.format(formatter);
    }


//    private void updateChart() {
//        // 차트에 표시할 데이터 생성
//        List<BarEntry> entries = new ArrayList<>();
//        entries.add(new BarEntry(0f, totalMorningCost));
//        entries.add(new BarEntry(1f, totalLunchCost));
//        entries.add(new BarEntry(2f, totalEveningCost));
//        entries.add(new BarEntry(3f, totalBeverageCost));
//
//        // BarDataSet을 만들고 데이터 적용
//        BarDataSet dataSet = new BarDataSet(entries, "비용");
//        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);  // 막대의 색상 설정
//        dataSet.setValueTextColor(Color.BLACK);  // 값의 텍스트 색상 설정
//
//        // X 축 라벨 설정
//        List<String> labels = new ArrayList<>();
//        labels.add("조식");
//        labels.add("중식");
//        labels.add("석식");
//        labels.add("음료");
//
//        // X축에 라벨 적용
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // X축 라벨을 아래에 표시
//        xAxis.setGranularity(1f);  // 라벨 간격 설정
//        xAxis.setCenterAxisLabels(true);  // 라벨을 막대 중앙에 표시
//
//
//        // Y 축 설정
//        YAxis yAxis = barChart.getAxisLeft();
//        yAxis.setDrawGridLines(false);  // 그리드 라인 비활성화
//        yAxis.setAxisMinimum(0f);  // Y 축 최소값 설정
//
//
//
//        // X 축 라벨 적용
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
//
//        // 차트에 데이터 설정
//        BarData barData = new BarData(dataSet);
//        barData.setBarWidth(0.4f);  // 막대의 너비 조절
//
//        // 값의 포맷 조절
//        ValueFormatter valueFormatter = new DefaultValueFormatter(0);  // 0 소수점 자리수로 설정
//        barData.setValueFormatter(valueFormatter);
//        // 레전드 위치 조절
//        Legend legend = barChart.getLegend();
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(false);
//
//        // 차트 아래의 설명(레전드) 표시 여부 설정
//        barChart.getLegend().setEnabled(true);
//
//        // 차트 업데이트
//        barChart.invalidate();
//
//        // 차트 업데이트
//        barChart.setData(barData);
//
//
//    }


    private void updateChart() {
        // 차트에 표시할 데이터 생성
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalMorningCost, "조식"));
        entries.add(new PieEntry(totalLunchCost, "중식"));
        entries.add(new PieEntry(totalEveningCost, "석식"));
        entries.add(new PieEntry(totalBeverageCost, "음료"));

        // PieDataSet을 만들고 데이터 적용
        PieDataSet dataSet = new PieDataSet(entries, "(식사 비용)");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);  // 원형 그래프의 색상 설정

        // 데이터 값의 텍스트 크기 설정
        dataSet.setValueTextSize(14f);

        pieChart.setEntryLabelColor(Color.BLACK);

        // PieData 객체에 데이터 설정
        PieData pieData = new PieData(dataSet);

        // 차트에 데이터 설정
        pieChart.setData(pieData);

        // 차트 업데이트
        pieChart.invalidate();

        dataSet.setDrawIcons(true);

        dataSet.setSelectionShift(15f); //

        // 레전드 위치 및 기타 설정
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        // 차트 아래의 설명(레전드) 표시 여부 설정
        pieChart.getLegend().setEnabled(true);
    }


}
