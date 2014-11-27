package com.sistem.sistem.modelviewer2;

import android.opengl.Matrix;
import android.view.MotionEvent;

public class CMbT_Spherical_Coordinate extends CameraMovebyTouch {

	
	public CMbT_Spherical_Coordinate() {
	}

    public volatile float touchscalefactortheta=0.2f*(float)(Math.PI/180f);
    public volatile float touchscalefactorphi=0.2f*(float)(Math.PI/180f);
    public volatile float maxphi=(float)(Math.PI/2);
    public volatile float minphi=0f;
    public volatile float cameradistance=5; 
    public volatile float theta=0f;
    public volatile float phi=minphi;
    public float[] center =new float[4];
    
	@Override
	public boolean Touch(MotionEvent e, float[] ViewMatrix, int mIndex,float previousX,float previousY) {
		// TODO Auto-generated method stub
		int action=e.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                float dx = e.getX() - previousX;
                float dy = e.getY() - previousY;

                double l=Math.sqrt(dx*dx+dy*dy);
                if(l!=0){
                	synchronized(lockObj){
                	theta=theta-dx*touchscalefactortheta;
                	phi=phi+dy*touchscalefactorphi;
                	Update(ViewMatrix,mIndex);
                	}
                }
                 return true;
        }
        return false;
		
	}
	@Override
	public void Update(float[] ViewMatrix, int mIndex) {
		// TODO Auto-generated method stub
		if(phi>maxphi)phi=maxphi;
		if(phi<minphi)phi=minphi;
		
		synchronized(ViewMatrix){
			//formula from Wikipedia
			float x=(float)(cameradistance*Math.sin(theta)*Math.cos(phi));
			float y=(float)(cameradistance*Math.sin(phi));
			float z=(float)(cameradistance*Math.cos(theta)*Math.cos(phi));
			Matrix.setLookAtM(ViewMatrix, mIndex,
					x,y,z,center[0], center[1], center[2],0f, 1f, 0f);
        }
	}
}
