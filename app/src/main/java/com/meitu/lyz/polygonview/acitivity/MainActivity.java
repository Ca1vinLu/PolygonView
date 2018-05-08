package com.meitu.lyz.polygonview.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.meitu.lyz.polygonview.R;
import com.meitu.lyz.polygonview.widget.PolygonView;

import java.util.LinkedHashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private PolygonView mPolygonView;
    private Button mButton;

    private String[] keys = {"眼睛",
            "脸型",
            "嘴巴",
            "鼻子",
            "眉毛"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPolygonView = findViewById(R.id.polygon_view);
        mButton = findViewById(R.id.btn_change_value);

        generateData();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateData();
            }
        });
    }

    private void generateData() {
        Random random = new Random();
        LinkedHashMap<String, Float> data = new LinkedHashMap<>();

        for (String key : keys) {
            data.put(key, random.nextInt(100) / 10f);
        }


        mPolygonView.bindData(data);
    }
}
