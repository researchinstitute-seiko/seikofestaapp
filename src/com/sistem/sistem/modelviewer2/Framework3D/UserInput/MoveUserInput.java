package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import android.os.Debug;
import android.util.DebugUtils;

import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;

public class MoveUserInput extends UserInput {

	private float cameradistance;
	public volatile float moveperpixelpercameradist;
	public volatile float moveforwardperlog10dscale;
	public MoveUserInput(Framework3D framework,float moveperpixelpercameradist, float moveforwardperdscale) {
		super(framework);
		this.moveperpixelpercameradist=moveperpixelpercameradist;
		this.moveforwardperlog10dscale=moveforwardperdscale;
	}
	@Override
	public boolean slide(float rawx,float rawy,float rawdx,float rawdy){
		float x=rawx*framework.density;
		float y=rawy*framework.density;
		float dx=rawdx*framework.density;
		float dy=rawdy*framework.density;
		
		float centerdu=-dx*moveperpixelpercameradist*cameradistance;
		float centerdv=dy*moveperpixelpercameradist*cameradistance;
		
		float[] N=new float[4];
		substract(N,framework.view.LookAt,framework.view.Camera);
		float[] M=new float[4];
		crossproduct(M,N,framework.view.Up);
		if(len2(M[0],M[1],M[2])==0){
			System.out.append('!');
		}
		normalize(M);
		float[] O=M.clone();
		scale(M,centerdu,M);
		crossproduct(O,O,N);
		if(len2(O[0],O[1],O[2])==0){
			System.out.append('!');
		}
		normalize(O);
		scale(O,centerdv,O);
		add(N,M,O);
		add(framework.view.LookAt,framework.view.LookAt,N);
		add(framework.view.Camera,framework.view.Camera,N);
		
		
		//float centerdx=(float) (Math.cos(-angradx)*centerdu-Math.sin(-angradx)*centerdv);
		//float centerdy=(float) (Math.sin(-angradx)*centerdu+Math.cos(-angradx)*centerdv);
		
		//framework.view.Camera[0]=framework.view.Camera[0]-centerdx;
		//framework.view.LookAt[0]=framework.view.LookAt[0]-centerdx;
		//framework.view.Camera[2]=framework.view.Camera[2]+centerdy;
		//framework.view.LookAt[2]=framework.view.LookAt[2]+centerdy;
		framework.requestRender();
		return true;
	}

	@Override
	public boolean zoom(float dscale){
		float[] N=new float[4];
		substract(N,framework.view.LookAt,framework.view.Camera);
		normalize(N);
		scale(N,(float)Math.log10(dscale)*moveforwardperlog10dscale,N);
		add(framework.view.LookAt,framework.view.LookAt,N);
		add(framework.view.Camera,framework.view.Camera,N);
		framework.requestRender();
		
		return true;
	}
	@Override
	public void attached() {
		float x=framework.view.Camera[0]-framework.view.LookAt[0];
		float y=framework.view.Camera[1]-framework.view.LookAt[1];
		float z=framework.view.Camera[2]-framework.view.LookAt[2];
		cameradistance=len(x,y,z);
		
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
	public
	void detached() {
		// TODO Auto-generated method stub
		
	}
	protected static void normalize(float[] vec){
		float len=len(vec[0],vec[1],vec[2]);
		vec[0]/=len;
		vec[1]/=len;
		vec[2]/=len;
	}
	protected static void crossproduct(float[] result,float[] p1,float[] p2){
		float x=p1[1]*p2[2]-p1[2]*p2[1];
		float y=p1[2]*p2[0]-p1[0]*p2[2];
		float z=p1[0]*p2[1]-p1[1]*p2[0];
		result[0]=x;
		result[1]=y;
		result[2]=z;
		
	}
	protected static void add(float[] result,float[] p1,float[] p2){
		result[0]=p1[0]+p2[0];
		result[1]=p1[1]+p2[1];
		result[2]=p1[2]+p2[2];
	}
	protected static void substract(float[] result,float[] p1,float[] p2){
		result[0]=p1[0]-p2[0];
		result[1]=p1[1]-p2[1];
		result[2]=p1[2]-p2[2];
	}
	protected static void scale(float[] result,float scale,float[] p){
		result[0]=scale*p[0];
		result[1]=scale*p[1];
		result[2]=scale*p[2];
	}

}
