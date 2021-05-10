package com.example.park;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

public class DatePickerActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_date_picker);

      final SingleDateAndTimePicker singleDateAndTimePicker = findViewById(R.id.single_day_picker);
      // Example for setting default selected date to yesterday
//        Calendar instance = Calendar.getInstance();
//        instance.add(Calendar.DATE, -1 );
//        singleDateAndTimePicker.setDefaultDate(instance.getTime());
      SingleDateAndTimePicker.OnDateChangedListener changeListener = (displayed, date) -> display(displayed);
      singleDateAndTimePicker.addOnDateChangedListener(changeListener);

      //singleDateAndTimePicker.setTypeface(Typeface.DEFAULT);
      //singleDateAndTimePicker2.setTypeface(ResourcesCompat.getFont(this, R.font.dinot_regular));
   }

   private void display(String toDisplay) {
      Toast.makeText(this, toDisplay, Toast.LENGTH_SHORT).show();
   }
}