package com.sistem.sistem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;

public class EventSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	public EventSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EventSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public EventSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Runnable onSurfaceChanged;
	public SurfaceHolder holder;
	public int format;
	public int width;
	public int height;
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		this.format=format;
		this.width=width;
		this.height=height;
		this.holder=holder;
		if(onSurfaceChanged!=null)onSurfaceChanged.run();
	}

	public Runnable onSurfaceCreated;
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder=holder;
		if(onSurfaceCreated!=null)onSurfaceCreated.run();
		
	}

	public Runnable onSurfaceDestroyed;
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.holder=holder;
		if(onSurfaceDestroyed!=null)onSurfaceDestroyed.run();
	}
    
	public void setOnSurfaceChanged(Runnable onSurfaceChanged) {
		this.onSurfaceChanged = onSurfaceChanged;
	}
	public void setOnSurfaceCreated(Runnable onSurfaceCreated) {
		this.onSurfaceCreated = onSurfaceCreated;
	}
	public void setOnSurfaceDestroyed(Runnable onSurfaceDestroyed) {
		this.onSurfaceDestroyed = onSurfaceDestroyed;
	}

}
