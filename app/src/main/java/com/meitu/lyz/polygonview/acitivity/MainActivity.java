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

        data.put("test1", 5.5f);
        data.put("test2", 9.9f);
        data.put("test3", 8.7f);
        data.put("test4", 2.3f);
        data.put("test5", 4.4f);
        mMultidimensionalView.bindData(data);
    }
}
