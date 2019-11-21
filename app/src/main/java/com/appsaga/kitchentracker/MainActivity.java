package com.appsaga.kitchentracker;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    TextView value,expiry;
    Button viewChart,change_xpiry,change_threshold;
    DatabaseReference databaseReference;
    EditText expiryET,thresholdET;

    ArrayList<BarEntry> barEntries;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        value = findViewById(R.id.value);
        expiry=findViewById(R.id.expiry);
        change_xpiry=findViewById(R.id.change_expiry);
        expiryET=findViewById(R.id.expiry_edit_text);
        change_threshold=findViewById(R.id.change_threshold);
        thresholdET=findViewById(R.id.threshold_edit_text);

        viewChart=findViewById(R.id.view_chart);
        databaseReference= FirebaseDatabase.getInstance().getReference("Container1");
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading");

        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,String> map = (HashMap<String, String>)dataSnapshot.child("Date").getValue();
                String xpiry = dataSnapshot.child("Expiry").getValue(String.class);

                TreeMap<String,String> values = new TreeMap<String, String>();
                values.putAll(map);
                TreeMap.Entry<String,String> lastEntry = values.lastEntry();
                value.setText(lastEntry.getValue());
                expiry.setText(xpiry);

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

                viewChart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this,ViewChart.class);
                        intent.putParcelableArrayListExtra("barEntries",barEntries);
                        startActivity(intent);
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("Test","test");
            }

        });

        change_xpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String expiry_date = expiryET.getText().toString().trim();

                if(!expiry_date.equals("") && expiry_date.length()==10 && expiry_date.charAt(4)=='-' && expiry_date.charAt(7)=='-')
                {
                    databaseReference.child("Expiry").setValue(expiry_date);
                    expiry.setText(expiry_date);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                }
            }
        });

        change_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String threshold_value = thresholdET.getText().toString().trim();

                if(!threshold_value.equals(""))
                {
                    databaseReference.child("Threshold").setValue(threshold_value);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Invalid threshold value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}