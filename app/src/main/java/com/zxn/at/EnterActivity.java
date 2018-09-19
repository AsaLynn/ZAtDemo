package com.zxn.at;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.test.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zxn on 2018-9-19 14:28:11.
 */
public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn0, R.id.btn1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn0:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btn1:
                startActivity(new Intent(this, AtEditActivity.class));
                break;
        }
    }
}
