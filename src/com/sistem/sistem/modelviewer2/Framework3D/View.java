package com.sistem.sistem.modelviewer2.Framework3D;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class View {


	public final Framework3D framework;
	volatile float[] Viewmatrix=new float[16];
	volatile float[] Projectionmatrix=new float[16];
	public volatile float[] LookAt=new float[4];
	public volatile float[] Camera=new float[4];
	public volatile float[] Up=new float[4];
	/*
	public volatile double angradx;
	public volatile double angrady;
	public volatile float cameradistance;
	public volatile float mincameradistance;
	public volatile float maxcameradistance;
	*/
	public volatile float near;
	public volatile float far;
	/*
	public volatile double angradperpixel;
	public volatile double maxangrady;
	public volatile double minangrady;
	public volatile float moveperpixelpercameradist;
	public volatile float maxscale;
	public volatile float minscale;
	*/
	public volatile float scale;
	 volatile float ratio;
	public volatile float pixeldensity=1f;
	public volatile double fovrad;
	public View(Framework3D framework,
			float camerax,float cameray,float cameraz,
			float lookatx,float lookaty,float lookatz, 
			float upx,float upy, float upz,
			double fovrad,
			float near, float far,
			float scale) {
		this.framework=framework;
		this.Camera[0]=camerax;
		this.Camera[1]=cameray;
		this.Camera[2]=cameraz;
		this.Camera[3]=1f;
		this.LookAt[0]=lookatx;
		this.LookAt[1]=lookaty;
		this.LookAt[2]=lookatz;
		this.LookAt[3]=1f;
		this.Up[0]=upx;
		this.Up[1]=upy;
		this.Up[2]=upz;
		this.Up[3]=0f;
		this.fovrad=fovrad;
		this.near=near;
		this.far=far;
		this.scale=scale;
	}
	//Called from Framework3D.(ctor)
	/*void setDensity(float density){
		this.pixeldensity=density;
	}
*/
	public void Change(int rawwidth, int rawheight) {
		// Set the OpenGL viewport to the same size as the surface.
				GLES20.glViewport(0, 0, rawwidth, rawheight);
				ratio = (float) rawwidth / rawheight;
	}
	
	

	public float[] Viewmatrix() {
		//formula from Wikipedia
		/*
		
		 
		 */
		Matrix.setLookAtM(Viewmatrix, 0, 
				Camera[0], Camera[1], Camera[2],
				LookAt[0], LookAt[1], LookAt[2],
				Up[0],Up[1],Up[2]);
		return Viewmatrix;
	}

	public float[] Projectionmatrix() {
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float recscale=1/scale;
		final float near_plane_halftop_length=(float)(near*Math.tan(fovrad*0.5));
		final float left = -ratio*recscale*near_plane_halftop_length;
		final float right = ratio*recscale*near_plane_halftop_length;
		final float bottom = -recscale*near_plane_halftop_length;
		final float top = recscale*near_plane_halftop_length;
		
		Matrix.frustumM(Projectionmatrix, 0, left, right, bottom, top, near, far);
		
		//Matrix.setIdentityM(Projectionmatrix, 0);
		return Projectionmatrix;
	}
}
