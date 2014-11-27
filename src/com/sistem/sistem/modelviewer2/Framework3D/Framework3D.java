package com.sistem.sistem.modelviewer2.Framework3D;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;

public class Framework3D {

	public final static int BYTES_PER_FLOAT=4;
	public final static int VERTICES_PER_FACE=3;

	public volatile Object lockobj=new Object();
	
	Activity activity;
	GLSurfaceView surfaceview;
	FrameworkRenderer renderer;
	public float density=1.0f;

	/** Thread executor for generating cube data in the background. */
	final ExecutorService mSingleThreadedExecutor = Executors.newSingleThreadExecutor();
	

	public Framework3D(final Activity activity,final GLSurfaceView surfaceview,
			/*Layer[] Layers,*/ /*View view,*/ float[] backgroundcolor,/* Light light,*/
			boolean culling) throws InstantiationException{
		this.activity=activity;
		this.surfaceview=surfaceview;
		//this.layers=Layers;
		this.light=light;
		this.backgroundcolor=backgroundcolor;
		this.culling=culling;


	}
	public void Prepare(Runnable runnable){
		// Set the renderer to our demo renderer, defined below.
		renderer = new FrameworkRenderer(this);
		renderer.pendinginvoke=runnable;
		surfaceview.setRenderer(renderer);
        surfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
	}
	public void set(Layer[] layers,View view, Light light){
			this.layers=layers;this.view=view;this.light=light;
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		 density=(displayMetrics.density);
	 }
	public volatile Layer[] layers;
	public volatile View view;
	public volatile Light light;
	
	//Used in Initialize()
	public final float[] backgroundcolor;
	private final boolean culling;
	
	//Called from surfaceview.onSurfaceCreated()
	//<-- from surfaceview.set()
	//<-- from this.(ctor)
	//
	//Bad class design!!!
	void Initialize(){

		// Set the background clear color to black.
		GLES20.glClearColor(backgroundcolor[0],backgroundcolor[1],backgroundcolor[2],backgroundcolor[3]);
		// Use culling to remove back faces.
		if(culling)GLES20.glEnable(GLES20.GL_CULL_FACE);
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		GLES20.glDepthRangef(0, 1f);
		
		ShaderProgram.reset();
		for(int i=0;i<layers.length;i++){
			layers[i].onsurfacecreated();
		}
		
	}
	
	public FrameworkRenderer getRenderer(){return this.renderer;}
	
	//Called from surfaceview.onSurfaceChange()
	public void ChangeView(int width, int height){	view.Change(width,height);	}
	
	public void requestRender(){
		surfaceview.requestRender();
	}
	//Called from surfaceview.onDrawFrame();
	public void Render(){
		if(layers!=null){
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		for(int i=0;i<layers.length;i++){
			layers[i].render(view, light);
		}
		}
	}
	
	//Never called internally.
	public void Dispose(){
		if(layers!=null){
		for(int i=0;i<layers.length;i++){
			layers[i].Dispose();
		}
		}
	}
}
