package com.jihyun.mobilesoftwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private Geocoder geocoder;
    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;

    private MenuDatabase menuDatabase;
    public static final String TABLE_NAME = "menu";
    SQLiteDatabase database;

    TextView type_text;
    TextView time_text;
    TextView name_text;
    TextView num_text;
    TextView review_text;
    TextView date_text;
    ImageView menu_image;
    String image_uri;
    TextView map_text;
    ImageButton checkbutton;
    ImageButton deletebutton;
    ImageButton revisebutton;
    TextView price_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        Intent intent = getIntent();
        int id;
        String date;
        id = intent.getExtras().getInt("id");
        date = intent.getExtras().getString("date");
        menuDatabase = MenuDatabase.getInstance(this);
        database = menuDatabase.getWritableDatabase();
        type_text = findViewById(R.id.type_text);
        time_text = findViewById(R.id.time_text);
        name_text = findViewById(R.id.name_text);
        num_text = findViewById(R.id.num_text);
        review_text = findViewById(R.id.review_text);
        date_text = findViewById(R.id.date_text);
        checkbutton = findViewById(R.id.check_button);
        deletebutton = findViewById(R.id.delete_button);
        revisebutton = findViewById(R.id.revise_button);
        map_text = findViewById(R.id.place_text_revision);
        price_text = findViewById(R.id.priceText);
        menu_image = findViewById(R.id.menu_image);
        date_text.setText(date);
        getAlldata(TABLE_NAME, id);

        try {
            if (image_uri != null && !image_uri.isEmpty()) {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.image_empty) // 로딩 중에 표시될 이미지
                        .error(R.drawable.image_empty) // 이미지 로드 실패 시 표시될 이미지
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);

                Glide.with(this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(image_uri)
                        .into(menu_image);
            } else {
                menu_image.setImageResource(R.drawable.image_empty);
                Toast.makeText(getApplicationContext(), "이미지를 불러오지 못했습니다", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            menu_image.setImageResource(R.drawable.image_empty);
            Toast.makeText(getApplicationContext(), "이미지를 불러오지 못했습니다", Toast.LENGTH_LONG).show();
        }

        checkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DetailActivity2.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletemenu(TABLE_NAME, id);
                Intent intent2 = new Intent(DetailActivity2.this, MainActivity.class);
                startActivity(intent2);
            }
        });

        revisebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(DetailActivity2.this, EditActivity2.class);
                intent3.putExtra("id", id);
                intent3.putExtra("select_date", date);
                startActivity(intent3);
            }
        });

/*        geocoder = new Geocoder(this, Locale.KOREA);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.output_map);
        mapFragment.getMapAsync(this);*/

    }

    MarkerOptions markerOptions = new MarkerOptions();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 변환
        DetailActivity2.geocoderthread thread = new DetailActivity2.geocoderthread();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LatLng start = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.title("식사 위치");
        markerOptions.snippet(map_text.getText().toString());
        markerOptions.position(new LatLng(latitude, longitude));

        mMap.addMarker(markerOptions);

    }

    class geocoderthread extends Thread {
        public void run() {
            List<Address> address = null;

            try {
                address = geocoder.getFromLocationName(map_text.getText().toString(), 3);
                latitude = address.get(0).getLatitude();
                longitude = address.get(0).getLongitude();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("GeoCoding", "해당 주소로 찾는 위도 경도가 없습니다. 올바른 주소를 입력해주세요.");
            }

        }
    }

    private void getAlldata(String t_name, int id)
    {
        Log.d("CustomTag", "GetAllData Start");
        if (database != null)
        {
            String sql = "SELECT type, time, name, num, kcal, review, image, price, map FROM " + t_name + " WHERE id = " + id;
            Cursor cursor = database.rawQuery(sql, null);
            Log.d("CustomTag", "For senetece Start");
            for(int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToNext();
                String type = cursor.getString(0);
                String time = cursor.getString(1);
                String name = cursor.getString(2);
                String num = cursor.getString(3);
                String kcal = cursor.getString(4);
                String review = cursor.getString(5);
                String image = cursor.getString(6);
                String map = cursor.getString(8);
                String price = cursor.getString(7);
                type_text.setText(type);
                time_text.setText(time);
                name_text.setText(name);
                num_text.setText("음식 하나당 칼로리 : " + kcal + "kcal  /  먹은 양 : " + num + "개");
                review_text.setText(review);
                image_uri = image;
                map_text.setText(map);
                price_text.setText(price);
            }
            Log.d("CustomTag", "Data Select End");
        }
        else{
            Log.d("CustomTag", "No DATABASE");
        }
    }

    private void deletemenu(String t_name, int id) {
        if (database != null)
        {
            database.execSQL("DELETE FROM " + t_name + " WHERE id = " + id);
        }
    }
}