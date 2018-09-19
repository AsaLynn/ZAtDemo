package com.zxn.at;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.test.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private EditText mEditText;
    /**
     * 存储@的cid、name对
     */
    private Map<String, String> cidNameMap = new HashMap<>();

    /**
     * 返回的所有的用户名,用于识别输入框中的所有要@的人
     * 如果用户删除过，会出现不匹配的情况，需要在for循环中做处理
     */
    private String nameStr;
    /**
     * 上一次返回的用户名，用于把要@的用户名拼接到输入框中
     */
    private String lastNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.et_input);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        mEditText.setFilters(new InputFilter[]{new MyInputFilter()});
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                String[] users = getUsers();
                String userIds = users[0];
                String names = users[1];
                Log.i(TAG, "userIds: " + userIds);
                Log.i(TAG, "names: " + names);
                break;
        }
    }

    private class MyInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            //如果输入了@符号,那么就跳到选择好友界面
            if (source.toString().equalsIgnoreCase("@")
                    || source.toString().equalsIgnoreCase("＠")) {
                goAt();
            }
            return source;
        }
    }

    private void goAt() {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivity(intent);
    }

    //上传需要的id值
    public String[] getUsers() {
        String[] texts = new String[2];
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (Map.Entry<String, String> entry : cidNameMap.entrySet()) {
            Log.i(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
            sb0.append(entry.getKey()).append(",");
            sb1.append(entry.getValue()).append(",");
        }
        texts[0] = sb0.toString();
        texts[1] = sb1.toString();
        Log.i(TAG, "mEditText: " + mEditText.getText().toString());
        return texts;
    }


    private void setAtImageSpan(String nameStr) {
        String content = String.valueOf(mEditText.getText());
        if (content.endsWith("@") || content.endsWith("＠")) {
            content = content.substring(0, content.length() - 1);
        }
        String tmp = content;
        SpannableString ss = new SpannableString(tmp);
        if (nameStr != null) {
            String[] names = nameStr.split(" ");
            if (names != null && names.length > 0) {
                for (String name : names) {
                    if (name != null && name.trim().length() > 0) {
                        //把获取到的名字转为bitmap对象
                        final Bitmap bmp = getNameBitmap(name);
                        // 这里会出现删除过的用户，需要做判断，过滤掉
                        if (tmp.indexOf(name) >= 0
                                && (tmp.indexOf(name) + name.length()) <= tmp
                                .length()) {
                            // 把取到的要@的人名，用DynamicDrawableSpan代替
                            ss.setSpan(
                                    new DynamicDrawableSpan(
                                            DynamicDrawableSpan.ALIGN_BASELINE) {
                                        @Override
                                        public Drawable getDrawable() {
                                            BitmapDrawable drawable = new BitmapDrawable(
                                                    getResources(), bmp);
                                            drawable.setBounds(0, 0,
                                                    bmp.getWidth(),
                                                    bmp.getHeight());
                                            return drawable;
                                        }
                                    }, tmp.indexOf(name),
                                    tmp.indexOf(name) + name.length(),
                                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        }
        mEditText.setTextKeepState(ss);
    }

    /**
     * 把返回的人名，转换成bitmap
     *
     * @param name
     * @return
     */
    private Bitmap getNameBitmap(String name) {
        /* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */
        name = "" + name;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //设置字体画笔的颜色
//        paint.setColor(getResources().getColor(R.color.color_blue));
        float textSize = mEditText.getTextSize();
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(name, 0, name.length(), rect);
        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));
        final Bitmap bmp = Bitmap.createBitmap(width, rect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        //canvas.drawColor(getResources().getColor(R.color.color_blue));
        canvas.drawText(name, rect.left, rect.height() - rect.bottom, paint);
        return bmp;
    }

    @Subscribe
    public void onEvent(Person event) {
        String tmpCidStr = event.getId();
        String tmpNameStr = " " + "@" + event.getName();
        cidNameMap.put(tmpCidStr, tmpNameStr);
        if (nameStr == null) {
            nameStr = tmpNameStr;
        } else {
            nameStr = nameStr + tmpNameStr;
        }
        lastNameStr = tmpNameStr;

        // 把要@的人插入光标所在位置
        int curIndex = mEditText.getSelectionStart();
        mEditText.getText().insert(curIndex, lastNameStr);
        // 通过输入@符号进入好友列表并返回@的人，要删除之前输入的@
        if (curIndex >= 1) {
            mEditText.getText().replace(curIndex - 1, curIndex, "");
        }
        setAtImageSpan(nameStr);
    }

}