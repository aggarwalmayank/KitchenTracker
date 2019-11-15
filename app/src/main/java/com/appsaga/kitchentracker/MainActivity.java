package com.appsaga.kitchentracker;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {
    TextView value;
    Button b1;
    DatabaseReference databaseReference;

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        value = findViewById(R.id.value);
        databaseReference= FirebaseDatabase.getInstance().getReference("Container1");

        barChart = findViewById(R.id.BarChart);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,String> map = (HashMap<String, String>)dataSnapshot.child("Date").getValue();

                TreeMap<String,String> values = new TreeMap<String, String>();
                values.putAll(map);
                TreeMap.Entry<String,String> lastEntry = values.lastEntry();
                value.setText(lastEntry.getValue());

                int i=1;
                barEntries=new ArrayList<>();
                for(TreeMap.Entry<String,String> entry:values.entrySet())
                {
                    barEntries.add(new BarEntry(i++, Integer.valueOf(entry.getValue())));
                }

                /*barEntries.add(new BarEntry(2f, 0));
                barEntries.add(new BarEntry(4f, 1));
                barEntries.add(new BarEntry(6f, 1));
                barEntries.add(new BarEntry(8f, 3));
                barEntries.add(new BarEntry(7f, 4));
                barEntries.add(new BarEntry(3f, 3));*/
                Log.d("Test",barEntries.size()+"");
                barDataSet = new BarDataSet(barEntries, "");
                barData = new BarData(barDataSet);
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(18f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("Test","test");
            }

        });


    }
}