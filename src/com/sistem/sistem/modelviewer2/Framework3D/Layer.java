package com.sistem.sistem.modelviewer2.Framework3D;

import android.opengl.GLES20;


public abstract class Layer {
	Framework3D framework;
	

	public volatile boolean isvisible=true;
	public volatile float[] Modelmatrix;
	public Layer(Framework3D framework, float[] ModelMatrices,boolean isvisible){
		this.framework=framework;
		this.Modelmatrix=ModelMatrices;
		this.isvisible=isvisible;
	}

	boolean disposed;
	public void Dispose(){
		if(!disposed){
		disposed=true;
		dispose();
		}
	}
	protected void dispose(){}
	
	protected abstract void Render(View view, Light light) ;
	public void render(View view, Light light) {if(isvisible)Render(view,light);}
	abstract void onsurfacecreated(); 
	
	//protected abstract int getshaderhandle();
	
}