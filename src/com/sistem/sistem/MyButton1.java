package com.sistem.sistem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton1 extends Button {

	
	public MyButton1(Context context) {
		super(context);
		initialize(context);
	}

	public MyButton1(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public MyButton1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}
	
	@SuppressWarnings("deprecation")
	void initialize(Context context){
		this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.my_button_1));
		//this.settext(TEXT_ALIGNMENT_CENTER);
		this.setTextSize(18);
	}
	
	public static float imageheightratio=0.8f;
	public static float imagemargin=15;
	public void setIcon(Drawable icon){
		
		float buttonheight=this.getLayoutParams().height;
		float imageheight=buttonheight*imageheightratio;
		float imagewidth=imageheight;
		icon.setBounds((int)imagemargin,0, (int)(imagewidth+imagemargin), (int)imageheight);
		this.setCompoundDrawables(/*new ScaleDrawable(icon,Gravity.CENTER_VERTICAL,this.getLayoutParams().height*0.9f,this.getLayoutParams().height*0.9f)*/ icon, null,null,null);
	}

}
