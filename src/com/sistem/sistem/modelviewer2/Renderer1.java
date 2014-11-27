package com.sistem.sistem.modelviewer2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.sistem.sistem.*;
import com.sistem.sistem.modelviewer2.common.RawResourceReader;
import com.sistem.sistem.modelviewer2.common.ShaderHelper;
import com.sistem.sistem.modelviewer2.common.TextureHelper;

/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class Renderer1 implements GLSurfaceView.Renderer {

	private final MapActivity mLessonSevenActivity;
	private final GLSurfaceView mGlSurfaceView;
	
	/**
	 * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	 * of being located at the center of the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] mViewMatrix = new float[16];

	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] mProjectionMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	
	
	
	
	/** Store the accumulated rotation. */
	private final float[] mAccumulatedRotation = new float[16];
	
	/** Store the current rotation. */
	private final float[] mCurrentRotation = new float[16];
	
	/** A temporary matrix. */
	private float[] mTemporaryMatrix = new float[16];
	
	
	
	/** 
	 * Stores a copy of the model matrix specifically for the light position.
	 */
	private float[] mLightModelMatrix = new float[16];		
	
	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;
	
	/** This will be used to pass in the modelview matrix. */
	private int mMVMatrixHandle;
	
	/** This will be used to pass in the light position. */
	private int mLightPosHandle;
	
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	
	/** This will be used to pass in model position information. */
	private int mPositionHandle;
	/** This will be used to pass in model position information. */
	private int mColorOrTexCoordHandle;
	
	/** This will be used to pass in model normal information. */
	private int mNormalHandle;
	
	/** This will be used to pass in model texture coordinate information. */
	//private int mTextureCoordinateHandle;
	
	
	/** Size of the position data in elements. */
	static final int POSITION_DATA_SIZE = 3;	
	/** Size of the position data in elements. */
	static final int COLOR_OR_TEXCOORD_DATA_SIZE = 4;	
	
	/** Size of the normal data in elements. */
	static final int NORMAL_DATA_SIZE = 3;
	
	/** Size of the texture coordinate data in elements. */
	//static final int TEXTURE_COORDINATE_DATA_SIZE = 2;
	static final int STRUCT_SIZE=POSITION_DATA_SIZE+COLOR_OR_TEXCOORD_DATA_SIZE + NORMAL_DATA_SIZE;
	/** How many bytes per float. */
	static final int BYTES_PER_FLOAT = 4;	
	static final int STRIDE = STRUCT_SIZE * BYTES_PER_FLOAT;

	
	
	/** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
	 *  we multiply this by our transformation matrices. */
	private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	
	/** Used to hold the current position of the light in world space (after transformation via model matrix). */
	private final float[] mLightPosInWorldSpace = new float[4];
	
	/** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
	private final float[] mLightPosInEyeSpace = new float[4];
	
	/** This is a handle to our cube shading program. */
	private int mProgramHandle;
	
	/** These are handles to our texture data. */
	private int mAndroidDataHandle;		
	
	// These still work without volatile, but refreshes are not guaranteed to happen.					
	public volatile float mDeltaX;					
	public volatile float mDeltaY;	
	
	/** Thread executor for generating cube data in the background. */
	private final ExecutorService mSingleThreadedExecutor = Executors.newSingleThreadExecutor();
	


	/**
	 * Initialize the model data.
	 */
	public Renderer1(final MapActivity lessonSevenActivity, final GLSurfaceView glSurfaceView) {
		mLessonSevenActivity = lessonSevenActivity;	
		mGlSurfaceView = glSurfaceView;
		//mGlEs20 = new AndroidGL20();
	}

	public volatile Meshdata data;
	private void generateCubes() {
		mSingleThreadedExecutor.submit(new GenDataRunnable());		
	}
	class GenDataRunnable implements Runnable {
		@Override
		public void run() {			
				// Run on the GL thread -- the same thread the other members of the renderer run in.
				// register to the thread of the surfaceview
				mGlSurfaceView.queueEvent(new Runnable() {
					@Override
					public void run() {												
						if (!disposed) {
							release();
						}
						// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
						System.gc();
						try {
							data=generatedata();
							SendDataToGPU(data);
							//SendDataToGPU();
						} catch (OutOfMemoryError err) {
							if (!disposed) {
								release();
							}
							// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
							System.gc();		
						}																	
					}				
				});
		}
	}
	
	static final int SIZE_PER_SIDE=4;
	static final float MIN_POSITION=-5f;
	static final float POSITION_RANGE=10f;
	static final float INTERVAL=POSITION_RANGE/(SIZE_PER_SIDE-1);
	private static Meshdata generatedata(){
		
		float[] vertexdata=new float[STRUCT_SIZE*SIZE_PER_SIDE*SIZE_PER_SIDE];
		int i=0;
		for(int iy=0;iy<SIZE_PER_SIDE;iy++){
			float y=iy*INTERVAL+MIN_POSITION;
			for(int ix=0;ix<SIZE_PER_SIDE;ix++){
				float x=ix*INTERVAL+MIN_POSITION;
				
				float z=func(x,y);
				float dz_dx=dz_dx(x,y);
				float dz_dy=dz_dy(x,y);
				//Pos, Col, Norm, Texcoord
				vertexdata[i++]=x;
				vertexdata[i++]=z;
				vertexdata[i++]=y;
				i+=color(x,y,vertexdata,i);
				//normalVector = {-dz_dx,-dz_dy,1}.normalize();
				float len=(float)Math.sqrt(dz_dx*dz_dx+dz_dy*dz_dy+1);
				vertexdata[i++]=-dz_dx/len;
				vertexdata[i++]=1/len;
				vertexdata[i++]=-dz_dy/len;
			}
		}
		i=0;
		short[] indexdata=new short[(SIZE_PER_SIDE*2+2)*(SIZE_PER_SIDE-1)-2];
		for(int iy=0;iy<SIZE_PER_SIDE-1;iy++){
			for(int ix=0;ix<SIZE_PER_SIDE;ix++){
				indexdata[i++]=(short)(iy*SIZE_PER_SIDE+ix);
				indexdata[i++]=(short)((iy+1)*SIZE_PER_SIDE+ix);
			}
			if(iy!=SIZE_PER_SIDE-2){
				//degenerate triangles
				indexdata[i++]=(short)((iy+2)*SIZE_PER_SIDE-1);
				indexdata[i++]=(short)((iy+1)*SIZE_PER_SIDE);
			}
		}
		return new Meshdata(vertexdata,indexdata);
	}
	private static float func(float x,float y){
		return (x*x+y*y)*0.1f;
	}
	private static float dz_dx(float x, float y){
		return 0.2f*x;
	}
	private static float dz_dy(float x, float y){
		return 0.2f*y;
	}
	private static int color(float x,float y, float[] buf,int offset){
		float rx=(x-MIN_POSITION)/POSITION_RANGE;
		float ry=(y-MIN_POSITION)/POSITION_RANGE;
		
		//mix (0f,0f,1f,1f), (0f,1f,0f,1f),
		//	  (1f,0f,0f,1f), (0f,0.5f,1f,1f)
		/*buf[offset]=mix4(0f,0f,1f,0f,rx,ry);	
		buf[offset+1]=mix4(0f,1f,0f,0.5f,rx,ry);	
		buf[offset+2]=mix4(1f,0f,0f,1f,rx,ry);	
		buf[offset+3]=1f;*/
		//mix (0,0,0,-1),(1,0,0,-1),
		//    (0,1,0,-1),(1,1,0,-1)
		buf[offset]=mix4(0f,1f,0f,1f,rx,ry);	
		buf[offset+1]=mix4(0f,0f,1f,1f,rx,ry);	
		buf[offset+2]=0f;	
		buf[offset+3]=-1f;
		return 4;
		
	}
	private static float mix4(float x0y0,float x1y0,float x0y1,float x1y1,float rx,float ry){
		return x0y0*(1-rx)*(1-ry)+x1y0*rx*(1-ry)+x0y1*(1-rx)*ry+x1y1*rx*ry;	
	}
	
	boolean disposed=true;
	boolean indexbufferused=true;
	
	private void SendDataToGPU(Meshdata data){
		//TODO: implement indexdata.
		
		disposed=false;
		FloatBuffer vertexBuffer=ByteBuffer.allocateDirect(data.vertexdata.length*BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(data.vertexdata);
		vertexBuffer.position(0);
		ShortBuffer indexBuffer=ByteBuffer.allocateDirect(data.indexdata.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
		indexBuffer.put(data.indexdata);
		indexBuffer.position(0);
		
		// Second, copy these buffers into OpenGL's memory. After, we don't need to keep the client-side buffers around.					
		final int buffers[] = new int[2];
		GLES20.glGenBuffers(2, buffers, 0);						
	
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GLES20.GL_STATIC_DRAW);			
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 2, indexBuffer, GLES20.GL_STATIC_DRAW);			
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	
		mVertexBufferIdx = buffers[0];	
		mIndexBufferIdx = buffers[1];			
		
		//Release the buffer
		vertexBuffer.limit(0);
		vertexBuffer = null;
		indexBuffer.limit(0);
		indexBuffer=null;
	}
	
	int mVertexBufferIdx;
	int mIndexBufferIdx;
	public void render() {	    
		
		// Pass in the position information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferIdx);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, 0);

		// Pass in the color/texture information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferIdx);
		GLES20.glEnableVertexAttribArray(mColorOrTexCoordHandle);
		GLES20.glVertexAttribPointer(mColorOrTexCoordHandle, COLOR_OR_TEXCOORD_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, POSITION_DATA_SIZE * BYTES_PER_FLOAT);
		
		// Pass in the normal information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferIdx);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, (POSITION_DATA_SIZE+COLOR_OR_TEXCOORD_DATA_SIZE) * BYTES_PER_FLOAT);
		
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// Draw the cubes.
		if(indexbufferused)	{

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIdx);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, data.indexdata.length, GLES20.GL_UNSIGNED_SHORT, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			
		}else{
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,	data.vertexdata.length/STRUCT_SIZE);
		}
	}
	public void release() {
		// Delete buffers from OpenGL's memory
		final int[] buffersToDelete = new int[] { mVertexBufferIdx,mIndexBufferIdx };
		GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
		disposed=true;
	}
	
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{		
		
		generateCubes();			
		
		// Set the background clear color to black.
		GLES20.glClearColor(0f, 0f, 0f, 1f);
		
		// Use culling to remove back faces.
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);						
	
		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);		

		final String vertexShader = RawResourceReader.readTextFileFromRawResource(mLessonSevenActivity, R.raw.shader_sample_vs);   		
 		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mLessonSevenActivity, R.raw.shader_sample_fs);
 				
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);		
		
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position","a_Color_Or_TexCoordinate",  "a_Normal"});		            
        
		// Load the texture
		/*mAndroidDataHandle = TextureHelper.loadTexture(mLessonSevenActivity, R.drawable.stone_wall_public_domain);		
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);		
        */
        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);        
	}	
		

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1f;
		final float far = 1000.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}	

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);			                                    
        
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);   
        
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix"); 
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");     
        mColorOrTexCoordHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color_Or_TexCoordinate");    
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal"); 
        //mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        
        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);                     
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 3.0f, 1.0f);
               
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);                      
        
        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -13f);     
        
        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);        
    	Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
    	Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
    	mDeltaX = 0.0f;
    	mDeltaY = 0.0f;
    	    	
    	// Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
    	Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
    	System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);
    	    	
        // Rotate the cube taking the overall rotation into account.     	
    	Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
    	System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);   
    	
    	// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// Pass in the light position in eye space.
		GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
		
		// Pass in the texture information
		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);

		// Tell the texture uniform sampler to use this texture in the
		// shader by binding to texture unit 0.
		GLES20.glUniform1i(mTextureUniformHandle, 0);
		
        if(!disposed)
        {
			render();
        }
	}		

	
}
class Meshdata{
	public float[] vertexdata;
	public short[] indexdata;
	public Meshdata(float[] vertexdata, short[] indexdata){this.vertexdata=vertexdata;this.indexdata=indexdata;}
}
