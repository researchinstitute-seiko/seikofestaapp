package com.sistem.sistem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.text.method.SingleLineTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class SwipingText extends FrameLayout {


	public SwipingText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}
	public SwipingText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	public SwipingText(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(final Context context){
		tv=new TextView(context);
		lp.setMargins(20, 0, 0, 0);
		lp.gravity=Gravity.CENTER_VERTICAL|Gravity.LEFT;
		tv.setLayoutParams(lp);
		this.addView(tv);
		tv.setTransformationMethod(new SingleLineTransformationMethod());
		tv.setHorizontallyScrolling(true);
		tv.setHorizontalScrollBarEnabled(false);
		this.setBackgroundColor(0x50ffffff);
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//Uri uri = Uri.parse( "http://seikofesta.official.jp/2014/index.html" );
				//context.startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
				
			}
		});
	}
	int state;
	CharSequence textupdate;
	public void setText(CharSequence text){
		textupdate=text;
		if(!isswiping)startswipe();
	}
	boolean isswiping=false;
	public void startswipe(){
		if(textupdate!=null){
			tv.setText(textupdate);
			textupdate=null;
		}
		/*if(tv.getWidth()+20<=this.getWidth()-20){
			stop();
			lp.leftMargin=20;
			tv.setLayoutParams(lp);
			isswiping=false;
		}else{*/
			
			state=HomeActivity._default.windowwidth;

			isswiping=true;
			timer.Ensure(new Runnable() {
					@Override
					public void run() {
						if(state<=-tv.getWidth()){oncomplete();}
						state-=1;
						tv.post(new Runnable() {
							
							@Override
							public void run() {

								lp.leftMargin=state;
								tv.setLayoutParams(lp);	
							}
						});
						}
					});
			
		//}
		
	}
	
	private void oncomplete(){
		startswipe();
	}
	public void stop(){
		timer.Stop();		
	}

	
	public TextView tv;
	public MyTimer timer=new MyTimer(10);
	private FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

}
