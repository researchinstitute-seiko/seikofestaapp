package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;
import com.sistem.sistem.modelviewer2.Framework3D.View;

import android.view.KeyEvent;
import android.view.MotionEvent;

public abstract class UserInput {

	Framework3D framework;
	public UserInput(Framework3D framework) {
		this.framework=framework;
			
	}

	float[] pointers=new float[4];
	float[] prevpointers=new float[4];
	int pointercount;
	int prevpointercount=0;
	boolean firsttime=false;
	public boolean touchEvent(MotionEvent event){
		
			this.prevpointercount=pointercount;
			this.pointercount=event.getPointerCount();
	        switch (event.getAction()&MotionEvent.ACTION_MASK) {
		        case MotionEvent.ACTION_DOWN:
		        case MotionEvent.ACTION_POINTER_DOWN:
		        case MotionEvent.ACTION_UP:
		        case MotionEvent.ACTION_POINTER_UP:
		        	firsttime=true;
		        case MotionEvent.ACTION_MOVE:
		        	for(int i=0;i<Math.min(2, pointercount);i++){
		        		prevpointers[i*2]=pointers[i*2];
		        		prevpointers[i*2+1]=pointers[i*2+1];
		        		pointers[i*2]=event.getX(i);
		        		pointers[i*2+1]=event.getY(i);		        		
		        	}
		        	if(!firsttime&&pointercount==prevpointercount&&framework!=null){
		        		if(pointercount==1){
			        		slide(pointers[0], pointers[1], pointers[0]-prevpointers[0], pointers[1]-prevpointers[1]);
			        	}
			        	else if(pointercount==2){
			        		zoom((float) (Math.hypot(pointers[0]-pointers[2], pointers[1]-pointers[3])/
			        				Math.hypot(prevpointers[0]-prevpointers[2], prevpointers[1]-prevpointers[3])));
			        		
			        	}
			        	else{return false;}
		        	}
		        	break; 
	            default:
	            	return false;
	
	        } 

			firsttime=false;
			return true;
	}
	/*
	 if(mode== MODE_MOVE){
				        	if(pointercount==1){
				        		slidetoMove(pointers[0], pointers[1], pointers[0]-prevpointers[0], pointers[1]-prevpointers[1]);
				        	}
				        	else if(pointercount==2){
				        		View.approach((float) (Math.hypot(pointers[0]-pointers[2], pointers[1]-pointers[3])/
				        				Math.hypot(prevpointers[0]-prevpointers[2], prevpointers[1]-prevpointers[3])));
				        		
				        	}
				        	else{return false;}
			        	}
		        		else if(mode== MODE_ROTATE){
				        	if(pointercount==1){
				        		View.slidetoRotate(pointers[0], pointers[1], pointers[0]-prevpointers[0], pointers[1]-prevpointers[1]);
				        	}
				        	else if(pointercount==2){
				        		View.approach((float) (Math.hypot(pointers[0]-pointers[2], pointers[1]-pointers[3])/
				        				Math.hypot(prevpointers[0]-prevpointers[2], prevpointers[1]-prevpointers[3])));
				        		
				        	}
				        	else{return false;}
			        	}
	 */
	public boolean slide(float x,float y, float dx,float dy){return false;}
	public boolean zoom(float dscale){return false;}
	//public boolean drag(float x,float y,float dx,float dy){return false;}
	public boolean volumeUp(){return false;}
	public boolean volumeDown(){return false;}
	public boolean menu(){return false;}
	public boolean home(){return false;}
	public abstract void attached();
	public abstract void detached();
	
	
	public boolean keyDown(int keyCode,KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			return volumeDown();
		case KeyEvent.KEYCODE_VOLUME_UP:
			return volumeUp();
		case KeyEvent.KEYCODE_MENU:
			return menu();
		case KeyEvent.KEYCODE_HOME:
			return home();
		default:
			return false;			
	}
		
	}
	/*
	 * 	switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				View.Center[1]-=300;
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				View.Center[1]+=300;
				break;
			case KeyEvent.KEYCODE_MENU:
				mode=(mode+1)%2;
				break;
			case KeyEvent.KEYCODE_HOME:
				return false;
				break;
			default:
				return false;			
		}
		View.framework.requestRender();
		return true;
	 */
	
	protected static double clamp(double x,double min,double max){
		return Math.max(min, Math.min(max, x));
	}

	protected static float clamp(float x,float min,float max){
		return Math.max(min, Math.min(max, x));
	}
	protected static float len2(float x, float y, float z){
		return x*x+y*y+z*z;
	}
	protected static float len(float x,float y,float z){
		return (float) Math.sqrt(len2(x,y,z));
	}
	
	
}
