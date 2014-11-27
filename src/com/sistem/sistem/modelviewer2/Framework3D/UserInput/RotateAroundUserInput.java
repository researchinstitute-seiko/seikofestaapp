package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;

public class RotateAroundUserInput extends UserInput {

	public volatile double angradperpixel;
	public volatile double angradx;
	public volatile double angrady;
	public volatile double minangrady;
	public volatile double maxangrady;
	public volatile float cameradistance;
	public volatile float mincameradistance;
	public volatile float maxcameradistance;
	
	public RotateAroundUserInput(Framework3D framework, double angradperpixel, double minangrady,double maxangrady, float mincameradistance, float maxcameradistance) {
		super(framework);
		this.angradperpixel=angradperpixel;
		this.minangrady=minangrady;
		this.maxangrady=maxangrady;
		this.mincameradistance=mincameradistance;
		this.maxcameradistance=maxcameradistance;
	}

	@Override
	public void attached() {
		float x=framework.view.Camera[0]-framework.view.LookAt[0];
		float y=framework.view.Camera[1]-framework.view.LookAt[1];
		float z=framework.view.Camera[2]-framework.view.LookAt[2];
		cameradistance=len(x,y,z);
		angradx=-Math.atan2(x, z);
		angrady=Math.atan2(y, Math.hypot(x, z));	
		
	}
	public void set(double angradx, double angrady, float cameradistance){
		this.angradx=angradx;
		this.angrady=angrady;
		this.cameradistance=cameradistance;
		setcamerapos();
	}
	@Override
	public boolean slide(float rawx,float rawy,float rawdx,float rawdy){
		//float x=rawx*framework.density;
		//float y=rawy*framework.density;
		float dx=rawdx*framework.density;
		float dy=rawdy*framework.density;
		angradx=angradx+dx*angradperpixel;
		angrady=clamp(angrady+dy*angradperpixel,minangrady,maxangrady);
		
		//Matrix.setIdentityM(Viewmatrix, 0);
		
		setcamerapos();
		return true;
	}
	private void setcamerapos(){

		float x=(float)(cameradistance*Math.sin(-angradx)*Math.cos(angrady))+framework.view.LookAt[0];
		float y=(float)(cameradistance*Math.sin(angrady))+framework.view.LookAt[1];
		float z=(float)(cameradistance*Math.cos(-angradx)*Math.cos(angrady))+framework.view.LookAt[2];
		framework.view.Camera[0]=x;
		framework.view.Camera[1]=y;
		framework.view.Camera[2]=z;
		framework.requestRender();
	}
	@Override
	public boolean zoom(float dscale){
		/*float newcameradistance=clamp(cameradistance/dscale, mincameradistance, maxcameradistance);
		float realdscale=newcameradistance/cameradistance;
		cameradistance=cameradistance*realdscale;
		//float dnear=framework.view.near-framework.view.near*realdscale;
		framework.view.near=framework.view.near*realdscale;
		framework.view.far=framework.view.far*(float)Math.max(Math.sqrt(realdscale),realdscale);*/
		
		//setcamerapos();
		framework.view.scale*=dscale;
		framework.requestRender();
		return true;
	}
	@Override
	public boolean volumeUp(){
		framework.view.Camera[1]+=300;
		framework.view.LookAt[1]+=300;
		framework.requestRender();
		return true;
	}
	@Override
	public boolean volumeDown(){
		framework.view.Camera[1]-=300;
		framework.view.LookAt[1]-=300;
		framework.requestRender();
		return true;
	}
	/*
	
	*/
	/*
	public void scale(float dscale){
		scale=clamp(scale*dscale, minscale, maxscale);	
		framework.requestRender();
	}
	*/
	

	@Override
	public void detached() {
		
	}


}
