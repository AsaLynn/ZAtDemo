package com.zxn.at;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.administrator.test.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends Activity {

    public static final String TAG = "PersonActivity";
    public static final String KEY_CID = "cid";
    public static final String KEY_NAME = "name";

    private ListView mListView;
    private PersonAdapter mAdapter;
    private List<Person> mPersons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at);
        mListView = findViewById(R.id.lv);
        mAdapter = new PersonAdapter(mPersons, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Person person = (Person) parent.getItemAtPosition(position);
                EventBus.getDefault().post(person);
                finish();
            }
        });

        initData();
    }

    private void initData() {
        mPersons.clear();
        for (int i = 0; i < 30; i++) {
            Person person = new Person();
            person.setId(String.valueOf(i));
            person.setName("王五" + i);
            mPersons.add(person);
        }
    }

}
