package com.sistem.sistem.modelviewer2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.sistem.sistem.modelviewer2.Framework3D.*;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.AnimationUserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.MoveUserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.RotateAroundUserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.SelfRotateUserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.UserInput;

public class FrameworkSurfaceView extends GLSurfaceView 
{	
	private Framework3D framework;
	private UserInput userinput;
	//private UserInput[] UserInputs;
	//private Renderer renderer;
	//private Framework3D framework;
	
	// Offsets for touch events	 

        	
	public FrameworkSurfaceView(Context context) 
	{
		super(context);		
		// Request an OpenGL ES 2.0 compatible context.
		setEGLContextClientVersion(2);
	}
	public FrameworkSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);	
		// Request an OpenGL ES 2.0 compatible context.
		setEGLContextClientVersion(2);

		
	}
	int mode=0;
	
	boolean keyDown(int keyCode,KeyEvent event){
		if(userinput==null)return false;
		return userinput.keyDown(keyCode, event);
	}
	void changemode(UserInput newui){
		if(userinput!=null)userinput.detached();
		userinput=newui;
		if(newui!=null)newui.attached();
		requestRender();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(userinput==null)return false;
		return userinput.touchEvent(event);
	}
	void set(Framework3D framework) 
	{
		//this.renderer = renderer;
		this.framework=framework;
		/*
		UserInputs=new UserInput[]{//new MoveUserInput(framework, 0.001f, 100000f),
				new RotateAroundUserInput(framework, Math.toRadians(0.25),
						Math.toRadians(0),Math.toRadians(70),150000f,150000f),
/*
				new SelfRotateUserInput(framework,Math.toRadians(0.25),
						 Math.toRadians(0),Math.toRadians(70),10000f),
				
				new AnimationUserInput(framework)
	*/		/*
		};

		UserInputs[mode].attached();
		*/
		requestRender();
		//super.setRenderer(renderer);
	}
}
