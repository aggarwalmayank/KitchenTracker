package com.appsaga.kitchentracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ViewChart extends AppCompatActivity {

    ArrayList<BarEntry> barEntries;
    ArrayList<BarEntry> finalEntries;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chart);

        barEntries = getIntent().getParcelableArrayListExtra("barEntries");
        barChart = findViewById(R.id.BarChart);
        finalEntries = new ArrayList<>();

        for(int i=0;i<barEntries.size();i++)
        {
            Entry entry = barEntries.get(i);
            finalEntries.add(new BarEntry(entry.getX(),entry.getY()));
        }
        Log.d("entry",barEntries.get(0)+"");
        barDataSet = new BarDataSet(finalEntries, "");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(18f);
    }
}
