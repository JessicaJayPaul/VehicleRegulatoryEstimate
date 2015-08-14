package com.cjt_pc.vehicleregulatoryestimate.sortlistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.cjt_pc.vehicleregulatoryestimate.R;

public class ClearEditText extends EditText implements OnFocusChangeListener,
        TextWatcher {

    private Drawable delet;

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    /**
     * ��ʼ��
     */
    private void init() {
        delet = getCompoundDrawables()[2];
        if (delet == null)
            delet = getResources().getDrawable(
                    R.drawable.emotionstore_progresscancelbtn);
        delet.setBounds(0, 0, delet.getIntrinsicWidth(), delet.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = event.getX() > (getWidth()
                        - getPaddingRight() - delet.getIntrinsicWidth())
                        && (event.getX() < (getWidth() - getPaddingRight()));
                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void setClearIconVisible(boolean b) {
        Drawable right = b ? delet : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore,
                              int lengthAfter) {
        setClearIconVisible(getText().length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

//    public void setShakeAnimtion() {
//        this.setAnimation(shakeAnimation(5));
//    }

//    public static Animation shakeAnimation(int counts) {
//        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
//        translateAnimation.setInterpolator(new CycleInterpolator(counts));
//        translateAnimation.setDuration(1000);
//        return translateAnimation;
//    }

}
