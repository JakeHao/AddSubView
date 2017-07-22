package com.jake.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.roundview.RoundLinearLayout;


/**
 * author：Jake
 * date：2017/7/21
 */
public class AddSubView extends RoundLinearLayout implements TextWatcher, View.OnClickListener {
    ViewGroup fl_sub;
    ViewGroup fl_add;
    EditText etNumber;
    int minNum, maxNum;


    public AddSubView(Context context) {
        super(context);
        init(context, null);
    }

    public AddSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int addWith, subWidth, textWidth;
        Drawable addDrawable, subDrawable;
        String digits;
        boolean isEdit;
        int lineWidth;
        int lineColor;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddSubView);
        try {
            addWith = typedArray.getDimensionPixelSize(R.styleable.AddSubView_add_width, dipToPx(context, 40));
            subWidth = typedArray.getDimensionPixelSize(R.styleable.AddSubView_sub_width, dipToPx(context, 40));
            textWidth = typedArray.getDimensionPixelSize(R.styleable.AddSubView_textview_width,
                    dipToPx(context, 40));
            addDrawable = typedArray.getDrawable(R.styleable.AddSubView_add_background);
            subDrawable = typedArray.getDrawable(R.styleable.AddSubView_sub_background);
            minNum = typedArray.getInt(R.styleable.AddSubView_min_num, 0);
            maxNum = typedArray.getInt(R.styleable.AddSubView_max_num, Integer.MAX_VALUE);
            digits = typedArray.getString(R.styleable.AddSubView_digits);
            isEdit = typedArray.getBoolean(R.styleable.AddSubView_is_edit, true);
            lineWidth = typedArray.getDimensionPixelSize(R.styleable.AddSubView_line_width, dipToPx(context, 0.5f));
            lineColor = typedArray.getColor(R.styleable.AddSubView_line_color, getResources().getColor(R.color.divider_color));
        } finally {
            typedArray.recycle();
        }
        if (TextUtils.isEmpty(digits)) digits = "0123456789";
        //
        int imageWidth = dipToPx(context, 12);
        //sub
        fl_sub = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subWidth, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        fl_sub.setLayoutParams(params);
        //imageview
        ImageView imageView = new ImageView(context);
        params = new FrameLayout.LayoutParams(imageWidth, imageWidth);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        if (subDrawable != null) {
            imageView.setImageDrawable(subDrawable);
        } else {
            imageView.setImageResource(R.drawable.ic_num_sub);
        }
        fl_sub.addView(imageView);
        //
        addView(fl_sub);
        //
        View line = new View(context);
        line.setLayoutParams(new LinearLayout.LayoutParams(lineWidth, LinearLayout.LayoutParams.MATCH_PARENT));
        line.setBackgroundColor(lineColor);
        addView(line);
        //EditText
        etNumber = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutParams.width = textWidth;
        etNumber.setLayoutParams(layoutParams);
        etNumber.setBackgroundDrawable(null);
        etNumber.setTextColor(getResources().getColor(R.color.black));
        etNumber.setSingleLine();
        etNumber.setGravity(Gravity.CENTER);
        etNumber.setKeyListener(DigitsKeyListener.getInstance(digits));
        etNumber.setText("0");
        if (isEdit) etNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        else {
            etNumber.setFocusable(false);
            etNumber.setEnabled(false);
        }
        addView(etNumber);
        //
        line = new View(context);
        line.setLayoutParams(new LinearLayout.LayoutParams(lineWidth, LinearLayout.LayoutParams.MATCH_PARENT));
        line.setBackgroundColor(lineColor);
        addView(line);
        //add
        fl_add = new FrameLayout(context);
        params = new FrameLayout.LayoutParams(addWith, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        fl_add.setLayoutParams(params);
        //
        imageView = new ImageView(context);
        params = new FrameLayout.LayoutParams(imageWidth, imageWidth);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        if (addDrawable != null) {
            imageView.setImageDrawable(addDrawable);
        } else {
            imageView.setImageResource(R.drawable.ic_num_add);
        }
        fl_add.addView(imageView);
        //imageview
        addView(fl_add);
        //
        fl_sub.setOnClickListener(this);
        fl_add.setOnClickListener(this);
        etNumber.addTextChangedListener(this);
        etNumber.setSelection(etNumber.length());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String txt = s.toString();
        if (TextUtils.isEmpty(txt)) {
            etNumber.setText("0");
            etNumber.setSelection(etNumber.length());
            return;
        }
        if (txt.length() > 1 && txt.startsWith("0")) {
            etNumber.setText(txt.replaceFirst("0", ""));
            etNumber.setSelection(etNumber.length());
            return;
        }
        int number = tryParse(txt, 0);
        if (number > maxNum) {
            etNumber.setText(String.valueOf(maxNum));
            etNumber.setSelection(etNumber.length());
        } else if (number < minNum) {
            etNumber.setText(String.valueOf(minNum));
            etNumber.setSelection(etNumber.length());
        }
    }

    @Override
    public void onClick(View v) {
        String txt = etNumber.getText().toString().trim();
        int number = tryParse(txt, 0);
        if (v == fl_add) {
            if (number >= maxNum) {
                return;
            }
            number++;
            etNumber.setText(String.valueOf(number));
            etNumber.setSelection(etNumber.length());
        } else if (v == fl_sub) {
            if (number <= minNum) {
                return;
            }
            number--;
            etNumber.setText(String.valueOf(number));
            etNumber.setSelection(etNumber.length());
        }
    }

    public int getCurrentNumber() {
        String txt = etNumber.getText().toString().trim();
        int number = tryParse(txt, 0);
        return number;
    }

    public static int dipToPx(Context mContext, float size) {
        Resources r;
        if (mContext == null) {
            r = Resources.getSystem();
        } else {
            r = mContext.getResources();
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());
    }

    public static int tryParse(String value, int defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        int result;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            result = defaultValue;
        }
        return result;
    }
}
