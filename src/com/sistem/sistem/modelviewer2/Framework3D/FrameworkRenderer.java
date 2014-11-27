package com.sistem.sistem.modelviewer2.Framework3D;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class FrameworkRenderer implements GLSurfaceView.Renderer {

	private final Framework3D framework;
	

	Runnable pendinginvoke;
	boolean invoked;

	/**
	 * Initialize the model data.
	 */
	public FrameworkRenderer(final Framework3D framework) {
		this.framework=framework;
	}

	
	private void render() {	    
		framework.Render();
	}
	
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{		
		if(!invoked&&pendinginvoke!=null)
			pendinginvoke.run();
		framework.Initialize();
		
		

	}	
		

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		framework.ChangeView(width, height);
		render();
	}	

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{		
		render();		
	}		

	
}
