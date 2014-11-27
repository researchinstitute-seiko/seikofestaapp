package com.sistem.sistem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class ZoomFrameLayout extends FrameLayout {

	public ZoomFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ZoomFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public ZoomFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(this.getChildCount()>1)throw new IllegalStateException("Cannot have more than one child in a ZoomFrameLayout");
		if(this.getChildCount()==0) super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//MeasureSpec.makeMeasureSpec(size, mode)
		View child=this.getChildAt(0);
		child.measure(MeasureSpec.makeMeasureSpec(3000, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(3000, MeasureSpec.AT_MOST));
		int h=child.getMeasuredHeight();
		int w=child.getMeasuredWidth();
		float aspectratio=(float)w/h;
		float scale=0;
		if(MeasureSpec.getSize(widthMeasureSpec)>MeasureSpec.getSize(heightMeasureSpec)*aspectratio){
			//stick to width
			scale=(float)MeasureSpec.getSize(widthMeasureSpec)/w;
			
		}else{
			//stick to height
			scale=(float)MeasureSpec.getSize(heightMeasureSpec)/h;
		}
		child.measure(MeasureSpec.makeMeasureSpec((int) (w*scale),  MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec((int) (h*scale), MeasureSpec.EXACTLY));
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
		
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub

		View child=this.getChildAt(0);
		int h=child.getMeasuredHeight();
		int w=child.getMeasuredWidth();
		int ww=right-left;
		int wh=bottom-top;
		int x=(ww-w)/2;
		int y=(wh-h)/2;
		
		super.onLayout(changed, x, y, x+w, y+h);
	}
	
	
}
