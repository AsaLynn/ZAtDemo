package com.zxn.at;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import com.example.administrator.test.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zxn on 2018-9-19 14:35:30.
 */
public class AtEditActivity extends AppCompatActivity implements AtEditText.OnInputAtListener, TextWatcher {

    private static final String TAG = "AtEditActivity";
    @BindView(R.id.et_input)
    AtEditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_edit);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        etInput.setOnInputAtListener(this);
        etInput.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_ok)
    public void onClick() {
        String s = etInput.getText().toString();
        Log.i(TAG, "onClick: " + s);
        String[] users = etInput.getAtUsers();
        String userIds = users[0];
        String names = users[1];
        Log.i(TAG, "userIds: " + userIds);
        Log.i(TAG, "names: " + names);
    }

    @Override
    public void onInputAt() {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(Person event) {
        String tmpCidStr = event.getId();
        //String tmpNameStr = " " + "@" + event.getName();
        //etInput.insertAtName(tmpNameStr);
        etInput.insertAtName(tmpCidStr, event.getName());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        etInput.clearAtNamesHistory();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
