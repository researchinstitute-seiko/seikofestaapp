package com.sistem.sistem.modelviewer2;

import android.view.MotionEvent;

public abstract class CameraMovebyTouch {
	public abstract boolean Touch(MotionEvent e, float[] ViewMatrix, int mIndex,float previousX,float previousY);
	public abstract void Update(float[] ViewMatrix, int mIndex);
	public final Object lockObj=new Object();
}
