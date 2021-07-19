package com.example.park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.util.Date;

import static com.example.park.util.Constants.EXTRA_DATE_PICKER;

public class DatePickerActivity extends AppCompatActivity {

   private AppCompatButton btnSaveDate;
   private Date myDate;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_date_picker);

      btnSaveDate = findViewById(R.id.btn_set_date_picker);

      final SingleDateAndTimePicker singleDateAndTimePicker = findViewById(R.id.single_day_picker);
      SingleDateAndTimePicker.OnDateChangedListener changeListener = new SingleDateAndTimePicker.OnDateChangedListener() {
         @Override
         public void onDateChanged(String displayed, Date date) {
            DatePickerActivity.this.display(displayed);
            myDate = date;
         }
      };
      singleDateAndTimePicker.addOnDateChangedListener(changeListener);
      setBtnSaveDate();
   }

   private void setBtnSaveDate(){
      btnSaveDate.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(EXTRA_DATE_PICKER, myDate.getTime());
            setResult(DatePickerActivity.RESULT_OK, returnIntent);
            finish();
         }
      });
   }

   private void display(String toDisplay) {
      Toast.makeText(this, toDisplay, Toast.LENGTH_SHORT).show();
   }
}