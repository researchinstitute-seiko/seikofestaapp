package com.sistem.sistem.modelviewer2.Framework3D;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class CopyOfView {


	public final Framework3D framework;
	volatile float[] Viewmatrix=new float[16];
	volatile float[] Projectionmatrix=new float[16];
	public volatile float[] Center=new float[4];
	public volatile double angradx;
	public volatile double angrady;
	public volatile float cameradistance;
	public volatile float mincameradistance;
	public volatile float maxcameradistance;
	public volatile float near;
	public volatile float far;
	public volatile double angradperpixel;
	public volatile double maxangrady;
	public volatile double minangrady;
	public volatile float moveperpixelpercameradist;
	 volatile float ratio;
	public volatile float scale;
	public volatile float maxscale;
	public volatile float minscale;
	public volatile float pixeldensity=1f;
	public volatile double fovrad;
	public CopyOfView(Framework3D framework,
			float centerx,float centery,float centerz, 
			double angradx, double angrady,
			float cameradistance, 
			float mincameradistance,float maxcameradistance,
			double fovrad,
			float near, float far,
			float moveperpixelpercameradist,
			double angradperpixel, 
			double minangrady, double maxangrady,
			float scale,
			float minscale, float maxscale) {
		this.framework=framework;
		this.Center[0]=centerx;
		this.Center[1]=centery;
		this.Center[2]=centerz;
		this.angradx=angradx;
		this.angrady=angrady;
		this.cameradistance=cameradistance;
		
		this.maxcameradistance=maxcameradistance;
		this.mincameradistance=mincameradistance;
		this.moveperpixelpercameradist=moveperpixelpercameradist;
		this.fovrad=fovrad;
		this.near=near;
		this.far=far;
		this.angradperpixel=angradperpixel;
		this.minangrady=minangrady;
		this.maxangrady=maxangrady;
		this.scale=scale;
		this.minscale=minscale;
		this.maxscale=maxscale;
	}
	//Called from Framework3D.(ctor)
	void setDensity(float density){
		this.pixeldensity=density;
	}

	public void Change(int rawwidth, int rawheight) {
		// Set the OpenGL viewport to the same size as the surface.
				GLES20.glViewport(0, 0, rawwidth, rawheight);
				ratio = (float) rawwidth / rawheight;
	}
	
	

	public float[] Viewmatrix() {
		//formula from Wikipedia
		float x=(float)(cameradistance*Math.sin(-angradx)*Math.cos(angrady))+Center[0];
		float y=(float)(cameradistance*Math.sin(angrady))+Center[1];
		float z=(float)(cameradistance*Math.cos(-angradx)*Math.cos(angrady))+Center[2];
		Matrix.setLookAtM(Viewmatrix, 0,
				x,y,z,Center[0], Center[1], Center[2],0f, 1f, 0f);
		//Matrix.setIdentityM(Viewmatrix, 0);
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
