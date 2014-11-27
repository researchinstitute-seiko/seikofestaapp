package com.sistem.sistem.modelviewer2;

import android.opengl.Matrix;
import android.view.MotionEvent;

public class CMbT_All_Directional extends CameraMovebyTouch {

	public float[] Matrices=new float[16+4*3];
	
	public float[] Viewmatrix=new float[16];
	public float[] Center;
	public float[] InitialCameraPos;
	public float[] CameraPos;
	public float[] CameraRotation;
	
	
	              
	              
	public static final int MO_CAMERAROT=0;
	public static final int VO_CAMERAPOSZERO=16;
	public static final int VO_CENTER=20;
	private static final int VO_CAMERAPOS=24;
	
	public CMbT_All_Directional() {
		// TODO Auto-generated constructor stub
		  Matrices[VO_CAMERAPOSZERO]=0f;
	      Matrices[VO_CAMERAPOSZERO+1]=0f;
	      Matrices[VO_CAMERAPOSZERO+2]=2.0f;
	      Matrices[VO_CAMERAPOSZERO+3]=1f;
	      Matrices[VO_CENTER]=0f;
	      Matrices[VO_CENTER+1]=0f;
	      Matrices[VO_CENTER+2]=0f;
	      Matrices[VO_CENTER+3]=0f;
	      Matrix.setIdentityM(Matrices, MO_CAMERAROT);
	      
	}

    public volatile float touchscalefactor=0.5f;
    float[] axisbuffer=new float[4];
	@Override
	public boolean Touch(MotionEvent e, float[] ViewMatrix, int mIndex,float previousX,float previousY) {
		// TODO Auto-generated method stub


        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = e.getX() - previousX;
                float dy = e.getY() - previousY;

                double l=Math.sqrt(dx*dx+dy*dy);
                if(l!=0){
                axisbuffer[0]=(float)(dy/l);
                axisbuffer[1]=-(float)(dx/l);
                axisbuffer[2]=0f;
                axisbuffer[3]=0f;
                
                synchronized(lockObj){
                
                Matrix.multiplyMV(axisbuffer, 0, Matrices, MO_CAMERAROT, axisbuffer, 0);
                
                Matrix.rotateM(Matrices, MO_CAMERAROT,(float) (l*touchscalefactor),
                		axisbuffer[0],axisbuffer[1],axisbuffer[2]);}

              	 Update(ViewMatrix,mIndex);
                }
                 return true;
                 
        }
        return false;
		
	}
	@Override
	public void Update(float[] ViewMatrix, int mIndex) {
		// TODO Auto-generated method stub
		Matrix.multiplyMV(Matrices, VO_CAMERAPOS, Matrices, MO_CAMERAROT, Matrices, VO_CAMERAPOSZERO);
     	 //Matrix.translateV(Matrices, VO_CAMERAPOS, Matrices[VO_OBJTRANSLATION], Matrices[VO_OBJTRANSLATION+1], Matrices[VO_OBJTRANSLATION+2]);
     	 Matrices[VO_CAMERAPOS]=Matrices[VO_CAMERAPOS]+Matrices[VO_CENTER];
     	 Matrices[VO_CAMERAPOS+1]=Matrices[VO_CAMERAPOS+1]+Matrices[VO_CENTER+1];
     	 Matrices[VO_CAMERAPOS+2]=Matrices[VO_CAMERAPOS+2]+Matrices[VO_CENTER+2];

        synchronized(ViewMatrix){
     	 Matrix.setLookAtM(ViewMatrix, mIndex,
     			 Matrices[VO_CAMERAPOS], Matrices[VO_CAMERAPOS+1], Matrices[VO_CAMERAPOS+2],
     			 Matrices[VO_CENTER],Matrices[VO_CENTER+1],Matrices[VO_CENTER+2],
     			 0f, 1.0f, 0.0f);

       }
	}

}
