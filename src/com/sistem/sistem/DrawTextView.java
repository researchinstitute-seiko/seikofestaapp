package com.sistem.sistem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
 
public class DrawTextView  extends TextView implements InputFilter{
 
 
	public DrawTextView(Context context) {
		super(context);
		setFocusable(false);
		this.setFilters(new InputFilter[] {this});
	}
	public DrawTextView(Context context, AttributeSet attrs) {
		super(context,attrs);
		setFocusable(false);
	}
	 public DrawTextView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        setFocusable(false);
 
	    }
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		this.setText(filter(this.getText(),0,this.getText().length(),null,0,0));
	}
	@Override
    public CharSequence filter(CharSequence source, int start, int end,
            Spanned dest, int dstart, int dend) {
        // 関連付けられたTextViewのTextPaintと幅を取得
        TextPaint paint = this.getPaint();
        int width = this.getWidth() - this.getCompoundPaddingLeft()
                - this.getCompoundPaddingRight();

        // TextView#setText()から呼ばれるだけの前提なので dest 以降の引数は使わない
        SpannableStringBuilder result = new SpannableStringBuilder();
        for (int index = start; index < end; index++) {
            // 幅チェック
            if (Layout.getDesiredWidth(source, start, index + 1, paint) > width) {
                // 行を越えた ⇒ ここまでを出力して改行を挿入
                result.append(source.subSequence(start, index));
                result.append("\n");
                start = index;

            } else if (source.charAt(index) == '\n') {
                // 改行文字 ⇒ ここまでを出力
                result.append(source.subSequence(start, index));
                start = index;
            }
        }

        if (start < end) {
            // 残りを格納(最後の行)
            result.append(source.subSequence(start, end));
        }
        return result;
    }
}