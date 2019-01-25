package com.zxn.zatlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zxn on 2018/9/19.
 */
public class AtEditText extends AppCompatEditText implements InputFilter {

    private OnInputAtListener mOnInputAtListener;
    private String mAtName = "";
    private String mLastAtName;
    private String TAG = "AtEditText";
    private List<String> mAtNames = new ArrayList<>();
    private HashMap<String, String> mAtUsersMap = new HashMap<>();

    public AtEditText(Context context) {
        this(context, (AttributeSet) null);
    }

    public AtEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public AtEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFilters(new InputFilter[]{this});
        //addTextChangedListener();
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.toString().equalsIgnoreCase("@")
                || source.toString().equalsIgnoreCase("＠")) {
            if (null != mOnInputAtListener) {
                mOnInputAtListener.onInputAt();
            }
        }
        return source;
    }

    public interface OnInputAtListener {
        void onInputAt();
    }

    public void setOnInputAtListener(OnInputAtListener l) {
        this.mOnInputAtListener = l;
    }

    public void insertAtName(String userId, String name) {
        String atName = " " + "@" + name;
        mAtUsersMap.put(userId, atName);
        mAtName += atName;
        //mAtNames.add(atName);
        mLastAtName = atName;
        // 把要@的人插入光标所在位置
        int curIndex = getSelectionStart();
        getText().insert(curIndex, mLastAtName);
        //要删除之前输入的@
        if (curIndex >= 1) {
            getText().replace(curIndex - 1, curIndex, "");
        }
        setAtImageSpan(mAtName);
    }

    public void insertAtName(String atName) {
        mAtName += atName;
        //mAtNames.add(atName);
        mLastAtName = atName;
        // 把要@的人插入光标所在位置
        int curIndex = getSelectionStart();
        getText().insert(curIndex, mLastAtName);
        //要删除之前输入的@
        if (curIndex >= 1) {
            getText().replace(curIndex - 1, curIndex, "");
        }
        setAtImageSpan(mAtName);
    }

    private void setAtImageSpan(String nameStr) {
        String content = String.valueOf(getText());
        //String content = nameStr;
        Log.i(TAG, "content: " + content);
        if (content.endsWith("@") || content.endsWith("＠")) {
            content = content.substring(0, content.length() - 1);
        }
        String tmp = content;
        Log.i(TAG, "tmp: " + tmp);
        SpannableString ss = new SpannableString(tmp);
        if (TextUtils.isEmpty(nameStr)) return;
        Log.i(TAG, "tmp: " + tmp);
        String[] names = nameStr.split(" ");
        Log.i(TAG, "nameStr: " + nameStr);
        if (names == null || names.length <= 0) return;

        int oldPosition = 0;
        for (final String name : names) {
            if (name != null && name.trim().length() > 0) {
                // 这里会出现删除过的用户，需要做判断，过滤掉
                int start = tmp.indexOf(name, oldPosition);
                if (start >= 0 && (start + name.length()) <= tmp.length()) {
                    // 把取到的要@的人名，用DynamicDrawableSpan代替
                    //Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 从起始下标到终了下标，但都不包括起始下标和终了下标
                    //setSpan(Object what, int start, int end, int flags)
                    oldPosition = start;
                    ss.setSpan(new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BASELINE) {
                        @Override
                        public Drawable getDrawable() {
                            return getNameDrawable(name);
                        }
                    }, oldPosition, oldPosition + name.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        setTextKeepState(ss);
    }

    private Drawable getNameDrawable(String name) {
        Bitmap bmp = getNameBitmap(name);
        BitmapDrawable drawable
                = new BitmapDrawable(getResources(), bmp);
        drawable.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
        return drawable;
    }

    /**
     * 把返回的人名，转换成bitmap
     *
     * @param name
     * @return
     */
    private Bitmap getNameBitmap(String name) {
        /* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */
        //name = "" + name;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //设置字体画笔的颜色
//        paint.setColor(getResources().getColor(R.color.color_blue));
        float textSize = getTextSize();
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

    public void clearAtNamesHistory() {
        if (TextUtils.isEmpty(getText().toString())) {
            mAtName = "";
            mAtUsersMap.clear();
        }
        //mAtUsersMap.remove()
        for (Map.Entry<String, String> entry : mAtUsersMap.entrySet()) {
            Log.i(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
            //sb0.append(entry.getKey()).append(",");
            //sb1.append(entry.getValue()).append(",");
            if (!mAtName.contains(entry.getValue())) {
                mAtUsersMap.remove(entry.getKey());
            }
        }
    }

//    public abstract class AtTextWatcher implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            clearAtNamesHistory();
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }

//    public void addAtTextChangedListener(AtTextWatcher watcher) {
//        super.addTextChangedListener(watcher);
//    }

    public String[] getAtUsers() {
        String[] texts = new String[2];
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (Map.Entry<String, String> entry : mAtUsersMap.entrySet()) {
            Log.i(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
            sb0.append(entry.getKey()).append(",");
            sb1.append(entry.getValue()).append(",");
        }
        texts[0] = sb0.toString();
        texts[1] = sb1.toString();
        return texts;
    }

}
