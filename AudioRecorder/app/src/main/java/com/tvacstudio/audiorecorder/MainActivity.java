package com.tvacstudio.audiorecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button uploadBtn;
    ImageButton record;
    EditText people, date, time;
    Chronometer Chrono;
    int alarmHour=0, alarmMinute=0;
    boolean i = true;

    // Button 클릭 시 화면 전환
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.button);
        record = (ImageButton) findViewById(R.id.record_btn);
        people = (EditText) findViewById(R.id.edit_send_people);
        date = (EditText) findViewById(R.id.edit_send_date);
        time = (EditText) findViewById(R.id.edit_send_time);
        Chrono = (Chronometer) findViewById(R.id.record_timer);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        /**
         * setImageResource: record 버튼 누르면 색 변하는 코드
         * Chrono.(): 크로노미터 작동 코드
         */
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == true){
                    record.setImageResource(R.drawable.main_record);
                    Chrono.start();
                    i = false;
                }else {
                    record.setImageResource(R.drawable.main_recording);
                    i = true;
                    Chrono.stop();
                }
            }
        });


        // 날짜 선택, 시간 선택 코드
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (view == date) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(date.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            date.setText(year + " / " + (monthOfYear + 1) + " / " + dayOfMonth);
                        }
                    },mYear,mMonth,mDay);
                    datePickerDialog.show();
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog
                        = new TimePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    }
                },alarmHour, alarmMinute, false);
                timePickerDialog.show();
            }
        });

        // 네비게이션 툴바 선택 코드
        
    }
}
