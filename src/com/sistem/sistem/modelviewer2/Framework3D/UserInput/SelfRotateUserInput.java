package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;

public class SelfRotateUserInput extends UserInput {

	public SelfRotateUserInput(Framework3D framework, double angradperpixel,
			double minangrady,double maxangrady, float moveforwardperdscale) {
		super(framework);
		this.angradperpixel=angradperpixel;
		this.minangrady=minangrady;
		this.maxangrady=maxangrady;
		this.moveforwardperlog10dscale=moveforwardperdscale;
	}

	public volatile double angradperpixel;
	public volatile double angradx;
	public volatile double angrady;
	public volatile double minangrady;
	public volatile double maxangrady;
	public volatile float cameradistance;

	public volatile float moveforwardperlog10dscale;
	@Override
	public void attached() {
		float x=framework.view.Camera[0]-framework.view.LookAt[0];
		float y=framework.view.Camera[1]-framework.view.LookAt[1];
		float z=framework.view.Camera[2]-framework.view.LookAt[2];
		cameradistance=len(x,y,z);
		angradx=Math.PI+(-Math.atan2(x, z));
		angrady=-(Math.atan2(y, Math.hypot(x, z)));	
		
	}

	@Override
	public boolean slide(float rawx,float rawy,float rawdx,float rawdy){
		//float x=rawx*framework.density;
		//float y=rawy*framework.density;
		float dx=rawdx*framework.density;
		float dy=rawdy*framework.density;
		angradx=angradx-dx*angradperpixel;
		angrady=clamp(angrady+dy*angradperpixel,minangrady,maxangrady);
		
		//Matrix.setIdentityM(Viewmatrix, 0);
		
		setpos();
		return true;
	}
	@Override
	public boolean zoom(float dscale){
		float ux=(float)(Math.sin(-angradx)*Math.cos(angrady));
		float uy=(float)(Math.sin(angrady));
		float uz=(float)(Math.cos(-angradx)*Math.cos(angrady));
		
		float movelength=(float)Math.log10(dscale)*moveforwardperlog10dscale;

		framework.view.Camera[0]+=ux*movelength;
		framework.view.Camera[1]+=uy*movelength;
		framework.view.Camera[2]+=uz*movelength;
		setpos();
		
		return true;
	}
	private void setpos(){

		float x=framework.view.Camera[0]+(float)(cameradistance*Math.sin(-angradx)*Math.cos(angrady));
		float y=framework.view.Camera[1]+(float)(cameradistance*Math.sin(angrady));
		float z=framework.view.Camera[2]+(float)(cameradistance*Math.cos(-angradx)*Math.cos(angrady));
		framework.view.LookAt[0]=x;
		framework.view.LookAt[1]=y;
		framework.view.LookAt[2]=z;
		framework.requestRender();
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
	@Override
	public void detached() {
		// TODO Auto-generated method stub
		
	}
	

}
