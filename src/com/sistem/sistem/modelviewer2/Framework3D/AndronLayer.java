package com.sistem.sistem.modelviewer2.Framework3D;

import java.nio.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.sistem.sistem.MyApplication;
import com.sistem.sistem.Util;
import com.sistem.sistem.anami.Record_place_table;

public class AndronLayer extends Layer {

	static final int FLOATS_PER_VERTEX=6;
	static final int BYTES_PER_VERTEX=FLOATS_PER_VERTEX*Framework3D.BYTES_PER_FLOAT;
	static final int BYTES_PER_INDEX=2;


	//FloatBuffer vertexdata;
	int GPUvertexbuffer;
	int GPUindexbuffer;
	final Framework3D m_framework;
	
	public AndronLayer(Framework3D framework, float[] ModelMatrices, boolean isvisible) throws Exception {
		super(framework, ModelMatrices,isvisible);
		this.m_framework=framework;
		/*
		framework.mSingleThreadedExecutor.submit(new Runnable(){
			@Override
			public void run() {			
					// Run on the GL thread -- the same thread the other members of the renderer run in.
					// register to the thread of the surfaceview
					m_framework.surfaceview.queueEvent(new Runnable() {
						@Override
						public void run() {												
							
							// Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
							System.gc();
*/
							load();
							/*													
						}				
					});
			}
			
			
		});	
		*/
	}
	@Override
	void onsurfacecreated(){
		AndronShaderProgram.initialize();
	}

    int vertexcount=0;
    int indexcount=0;
	private void load() {
			
				
               	for (Record_place_table place : MyApplication.indicium.place_table) {
               		vertexcount+=place.vertices.length/2;
               		indexcount+=(place.vertices.length-2)*3;
               	}
               	
               	
                FloatBuffer vertexbuffer=ByteBuffer.allocateDirect(vertexcount*BYTES_PER_VERTEX).order(ByteOrder.nativeOrder()).asFloatBuffer();
                ShortBuffer indexbuffer=ByteBuffer.allocateDirect(indexcount*BYTES_PER_INDEX).order(ByteOrder.nativeOrder()).asShortBuffer();
                final float rate=0.9f;
                int vi=0;
               	for (Record_place_table place : MyApplication.indicium.place_table) {
               		int vistart=vi;

               		float sumx=0,sumy=0;
               		for (int i = 0; i < place.vertices.length; i+=2) {
						sumx+=place.vertices[i];
						sumy+=place.vertices[i+1];
					}
               		float mx=sumx/place.vertices.length;
               		float my=sumy/place.vertices.length;

               		for (int i = 0; i < place.vertices.length; i+=2,vi++) {
               			float x,y;
               			if(place.vertices.length<=10) {
							x = (place.vertices[i]-mx)*rate+mx;
							y = (place.vertices[i+1]-my)*rate+my;
						}
               			else {
							x = place.vertices[i];
							y = place.vertices[i+1];
               			}
						float z=(place.floor-1)*3000;

						float r=0.5f*(place.floor+2)/5;
						float g=0.5f*(place.floor+2)/5;
						float b=1.0f*(place.floor+2)/5;
						
						//x=-x;
						
						vertexbuffer.put(x);
						vertexbuffer.put(y);
						vertexbuffer.put(z);
						vertexbuffer.put(r);
						vertexbuffer.put(g);
						vertexbuffer.put(b);
						
					}
               		
               		PolygonUtil.triangulatenaive(place.vertices, indexbuffer,vistart);
               		
               		
               		//indexbuffer.put((short)vistart);
               		//indexbuffer.put((short)vistart);
               		//int i = vistart+1;
//               		int j=vi-1; 
//               		for (;i < j; i++,j--) {
//               			indexbuffer.put((short)i);
//               			indexbuffer.put((short)j);
//					}
//               		if(i==j)indexbuffer.put((short)j);
//               		indexbuffer.put((short)j);
               		
               	}

                vertexbuffer.position(0);
                indexbuffer.position(0);
               
                final int GPUbuffers[] = new int[2];
        		GLES20.glGenBuffers(2, GPUbuffers, 0);						
        		GPUvertexbuffer=GPUbuffers[0];				
        		GPUindexbuffer=GPUbuffers[1];
        		
        		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,GPUvertexbuffer);
        		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexbuffer.capacity() * Framework3D.BYTES_PER_FLOAT, vertexbuffer, GLES20.GL_STATIC_DRAW);	
        		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,GPUindexbuffer);
        		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexbuffer.capacity() * BYTES_PER_INDEX, indexbuffer, GLES20.GL_STATIC_DRAW);			
        		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
                
        		//filedata=null;
                vertexbuffer.limit(0);
                vertexbuffer=null;
                indexbuffer.limit(0);
                indexbuffer=null;

	}

	// Create an empty, mutable bitmap
	Bitmap bitmap;
	Canvas canvas;
	int[] texttex=new int[1];
	Paint textPaint;
	void prepareText() {
		bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
		// get a canvas to paint over the bitmap
		canvas = new Canvas(bitmap);
		//Generate one texture pointer...
		GLES20.glGenTextures(1, texttex, 0);
		//...and bind it to our array
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texttex[0]);

		//Create Nearest Filtered Texture
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texttex[0]);

		// Draw the text
		textPaint = new Paint();
		textPaint.setTextSize(32);
		textPaint.setAntiAlias(true);
		textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
		textPaint.setTextAlign(Align.CENTER);

	}
	void drawText() {
		bitmap.eraseColor(0);
		// draw the text centered
		canvas.drawText("Hello World", 16,112, textPaint);

		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texttex[0]);
		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		//Clean up
		bitmap.recycle();
	}

	@Override
	protected void dispose(){

		//Releasing GPU buffers;
		final int[] buffersToDelete = new int[] { GPUvertexbuffer,GPUindexbuffer };
		GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
		//GLES20.glDeleteTextures(1, texttex,0);
	}
	public volatile float opacity;
	@Override
	public void Render(View view, Light light) {
		// TODO Auto-generated method stub
		
		AndronShaderProgram.use();

		synchronized(view.framework.lockobj){
			AndronShaderProgram.setMatrices(view,Modelmatrix);
			AndronShaderProgram.setLight(light,view);
		}
		AndronShaderProgram.setOpacity(opacity);
		AndronShaderProgram.setVertices(GPUvertexbuffer);
	
		//int dummy=0;
			
				//Draw
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.GPUvertexbuffer);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.GPUindexbuffer);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexcount,GLES20.GL_UNSIGNED_SHORT,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
 		//			);
			//}
		//System.out.println(dummy);
		
		
	}

}