package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import java.util.Stack;

import android.opengl.Matrix;

public class vec {
	
	private static Stack<vec> recyclebin=new Stack<vec>();
	private static int maxrecycle=1024;
	public static vec getnew() {
		synchronized(recyclebin) {
			return recyclebin.size()==0?new vec():recyclebin.pop();
		}
	}
	public static vec getnewzero() {

		synchronized(recyclebin) {
		return recyclebin.size()==0?new vec():recyclebin.pop().setzero();}
	}
	public void recycle() {
		if(recyclebin.size()<maxrecycle)
			synchronized(recyclebin) {
				recyclebin.push(this);
			}
	}
	public static vec getnewset(float x,float y,float z) {

		synchronized(recyclebin) {
			return getnew().set(x,y,z);
		}
		
	}
	public static vec getnewset2d(float x,float y) {

		synchronized(recyclebin) {
			return getnew().set2d(x,y);
		}
		
	}
	
	
	public float x,y,z;
	
	

	public vec() {
	}

	public vec(float x,float y,float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public vec(float x,float y) {
		this.x=x;
		this.y=y;
		this.z=0;
	}
	public vec add(vec v) {
		x+=v.x;
		y+=v.y;
		z+=v.z;
		return this;
	}
	public vec sub(vec v) {
		x-=v.x;
		y-=v.y;
		z-=v.z;
		return this;
	}
	public vec add(float x,float y,float z) {
		this.x+=x;
		this.y+=y;
		this.z+=z;
		return this;
	}
	public vec sub(float x,float y,float z) {
		this.x-=x;
		this.y-=y;
		this.z-=z;
		return this;
	}
	public vec set(vec v) {
		this.x=v.x;
		this.y=v.y;
		this.z=v.z;
		return this;
	}
	public vec set(float x,float y,float z) {
		this.x=x;
		this.y=y;
		this.z=z;
		return this;
	}
	public vec set2d(float x,float y) {
		this.x=x;
		this.y=y;
		this.z=0;
		return this;
	}
	public vec setzero() {
		return this.set(0,0,0);
	}
	public vec scale(float k) {
		this.x*=k;
		this.y*=k;
		this.z*=k;
		return this;
	}
	public vec setcross(vec v1,vec v2){
		x=v1.y*v2.z-v1.z*v2.y;
		y=v1.z*v2.x-v1.x*v2.z;
		z=v1.x*v2.y-v1.y*v2.x;
		return this;
	}

	//Calculates (v1-v2).(v3-v4)
	public static float subdot(vec v1,vec v2,vec v3,vec v4) {
		return (v1.x-v2.x)*(v3.x-v4.x)+(v1.y-v2.y)*(v3.y-v4.y)+(v1.z-v2.z)*(v3.z-v4.z);
	}
	public static float subcross2d(vec v1,vec v2,vec v3,vec v4) {
		return (v1.x-v2.x)*(v3.y-v4.y)-(v1.y-v2.y)*(v3.x-v4.x);
	}
	public static float cross2d(vec v1,vec v2){
		return v1.x*v2.y-v1.y*v2.x;
	}
	public static float dot(vec v1,vec v2) {
		return v1.x*v2.x+v1.y*v2.y+v1.z*v2.z;
	}
	public float len2() {
		return vecutil.len2(x, y, z);
	}
	public float len() {
		return vecutil.len(x, y, z);
	}
	public vec normalize() {
		float len=len();
		x/=len;
		y/=len;
		z/=len;
		return this;
	}

	public vec setpoint(vec p1,vec p2,float t) {
		return this.set(p2).sub(p1).scale(t).add(p1);
	}
	public void copytoarray(float[] array,int offset) {
		array[offset]=x;
		array[offset+1]=y;
		array[offset+2]=z;
	}
	public void copytoarray(float[] array) {
		copytoarray(array);
	}
	public void copytoarray2d(float[] array,int offset) {
		array[offset]=x;
		array[offset+1]=y;
	}
	public void copytoarray2d(float[] array) {
		copytoarray2d(array);
	}
	public vec setarrayvalue(float[] array,int offset) {
		x=array[offset];
		y=array[offset+1];
		z=array[offset+2];
		return this;
	}
	public vec setarrayvalue2d(float[] array,int offset) {
		x=array[offset];
		y=array[offset+1];
		z=0;
		return this;
	}
	public vec setarrayvalue(float[] array) {
		return setarrayvalue(array,0);
	}
	public vec setarrayvalue2d(float[] array) {
		return setarrayvalue2d(array,0);
	}
	public vec applymatrix(float[] matrix) {
		float[] v=new float[3];
		copytoarray(v);
		Matrix.multiplyMV(v, 0, matrix, 0, v, 0);
		return setarrayvalue(v);
	}
}
