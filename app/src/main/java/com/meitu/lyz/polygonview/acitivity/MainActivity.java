package com.meitu.lyz.polygonview.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.meitu.lyz.polygonview.R;
import com.meitu.lyz.polygonview.widget.PolygonView;

import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private PolygonView mMultidimensionalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMultidimensionalView = findViewById(R.id.multi_dimensional_view);
        LinkedHashMap<String, Float> data = new LinkedHashMap<>();

        data.put("眼睛", 5.5f);
        data.put("脸型", 9.6f);
        data.put("嘴巴", 8.7f);
        data.put("鼻子", 2.3f);
        data.put("眉毛", 4.4f);

        mMultidimensionalView.bindData(data);
    }
}
