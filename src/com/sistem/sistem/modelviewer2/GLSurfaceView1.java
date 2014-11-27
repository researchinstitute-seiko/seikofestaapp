package com.sistem.sistem.modelviewer2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sistem.sistem.modelviewer2.Framework3D.*;

public class GLSurfaceView1 extends GLSurfaceView 
{	
	private View View;
	
	// Offsets for touch events	 
    private float mPreviousX;
    private float mPreviousY;
    
    private float mDensity;
        	
	public GLSurfaceView1(Context context) 
	{
		super(context);		
	}
	
	public GLSurfaceView1(Context context, AttributeSet attrs) 
	{
		super(context, attrs);		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event != null)
		{			
			float x = event.getX();
			float y = event.getY();
			
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if (mRenderer != null)
				{
					float deltaX = (x - mPreviousX) / mDensity;
					float deltaY = (y - mPreviousY) / mDensity;
					
					
				}
			}	
			
			mPreviousX = x;
			mPreviousY = y;
			
			return true;
		}
		else
		{
			return super.onTouchEvent(event);
		}		
	}

	private Renderer1 mRenderer;
	// Hides superclass method.
	public void setRenderer(Renderer1 renderer, float density) 
	{
		mRenderer = renderer;
		mDensity = density;
		super.setRenderer(renderer);
	}
}
