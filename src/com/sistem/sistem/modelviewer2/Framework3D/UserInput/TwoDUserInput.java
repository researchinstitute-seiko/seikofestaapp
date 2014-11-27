package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import com.sistem.sistem.Util;
import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;

public class TwoDUserInput extends UserInput {
	
	public double angx_rad,angy_rad;
	public float minx,maxx,miny,maxy,scale_y,len,move_scale;

	public TwoDUserInput(Framework3D framework, float minx, float maxx, float miny,
			float maxy, double angx_rad, double angy_rad,float len, float scale_y,float move_scale) {
		super(framework);
		this.angx_rad=angx_rad;
		this.angy_rad=angy_rad;
		this.minx=minx;
		this.maxx=maxx;
		this.miny=miny;
		this.maxy=maxy;
		this.len=len;
		this.scale_y=scale_y;
		this.move_scale=move_scale;
		
	}

	@Override
	public void attached() {
		setview();
	}
	@Override
	public boolean slide(float x, float y, float dx, float dy) {
		framework.view.Camera[0]-=dx*move_scale;
		framework.view.Camera[1]+=dy*move_scale;
		framework.view.LookAt[0]-=dx*move_scale;
		framework.view.LookAt[1]+=dy*move_scale;
		framework.requestRender();
		return true;
	}

	public void setview() {
		framework.view.LookAt[0]=Util.clamp(framework.view.LookAt[0], minx, maxx);
		framework.view.LookAt[2]=Util.clamp(framework.view.LookAt[2], miny, maxy);
		float x=(float)(len*Math.cos(angx_rad)*Math.sin(angy_rad));
		float y=(float)(len*Math.sin(angx_rad));
		float z=(float)(len*Math.cos(angx_rad)*Math.cos(angy_rad));
		framework.view.Camera[0]=framework.view.LookAt[0]-x;
		framework.view.Camera[1]=framework.view.LookAt[1]+y;
		framework.view.Camera[2]=framework.view.LookAt[2]-z;
		framework.requestRender();		
	}
	@Override
	public void detached() {}

}
