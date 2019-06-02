package com.example.foodspy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text);
        String code = getIntent().getStringExtra("code");
        textView.setText(String.format("The true code is: %s", code));
    }
}
