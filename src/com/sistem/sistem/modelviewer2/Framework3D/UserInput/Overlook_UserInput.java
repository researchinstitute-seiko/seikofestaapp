package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;

public class Overlook_UserInput extends UserInput {

	public vec o1,o2;
	public float height,radius,hscale;
	public float minhanglerad,maxhanglerad;
	public float scale;
	
	public vec center=vec.getnew();
	public vec camera=vec.getnew();
	public Overlook_UserInput(Framework3D framework, vec o1,vec o2,float height,
			float radius, float hscale, float minhanglerad,float maxhanglerad, float scale) {
		super(framework);
		this.o1=o1;
		this.o2=o2;
		this.height=height;
		this.radius=radius;
		this.hscale=hscale;
		this.maxhanglerad=minhanglerad;
		this.maxhanglerad=maxhanglerad;
		this.scale=scale;
		
		
	}

	
	@Override
	public void attached() {
		vec cam=vec.getnew().setarrayvalue(framework.view.Camera);
		vec lookat=vec.getnew().setarrayvalue(framework.view.LookAt);
		vec intersection=vec.getnew();
		vec cam2d=vec.getnewset2d(cam.x, cam.z);
		vec lookat2d=vec.getnewset2d(lookat.x,lookat.z);
		int ret=vecutil.intersect2d(o1, o2, cam2d, lookat2d, intersection);
		if(ret!=0)
			center.set(o2).sub(o1).scale(0.5f).add(o1);
		else {
			float u=intersection.x, v=intersection.y;
			intersection.setpoint(cam, lookat, v);
			if(u<0) {
				center.set(o1.x, intersection.z, o1.y);
			}
			else if(u>1){
				center.set(o2.x, intersection.z, o2.y);
			}
			else {
				center.set(intersection);
			}
		}
		float[] M=new float[16];
		Matrix.setIdentityM(M, 0);
		Matrix.translateM(M, 0, o1.x, o1.y, o1.z);
		Matrix.rotateM(M, 0, 90-(float)Math.toDegrees(Math.atan2(o1.z-o2.z,o1.x-o2.x)), 0, 1, 0);
		Matrix.rotateM(M, 0, 90, 1, 0, 0);
		Matrix.scaleM(M, 0, 1f,hscale, 0f);
		
		float[] av_cam=new float[6];
		float[] av_lookat=new float[6];
		float[] av_o1=new float[6];
		float[] av_o2=new float[6];
		
		
		
		cam.copytoarray(av_cam,0);
		lookat.copytoarray(av_lookat,0);
		o1.copytoarray(av_o1,0);
		o2.copytoarray(av_o2,0);
		Matrix.multiplyMV(av_o1, 3, M	, 0, av_o1, 0);
		Matrix.multiplyMV(av_o2, 3, M	, 0, av_o2, 0);
		Matrix.multiplyMV(av_cam, 3, M	, 0, av_cam, 0);
		Matrix.multiplyMV(av_lookat, 3, M	, 0, av_lookat, 0);
		cam2d.setarrayvalue2d(av_cam);
		lookat2d.setarrayvalue2d(av_lookat);
		
		
	}

	@Override
	public void detached() {
		// TODO Auto-generated method stub
		
	}

}
