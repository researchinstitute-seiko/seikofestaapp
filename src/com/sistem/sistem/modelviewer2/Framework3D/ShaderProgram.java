package com.sistem.sistem.modelviewer2.Framework3D;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.sistem.sistem.R;
import com.sistem.sistem.modelviewer2.common.RawResourceReader;
import com.sistem.sistem.modelviewer2.common.ShaderHelper;

public class ShaderProgram {

	public static int shaderhandle;
	public static int h_u_MVPMatrix;
	public static int h_u_MVMatrix;
	public static int h_u_LightPos;
	public static int h_u_Texture;
	//public static int h_u_Color;
	public static int h_u_KaColor;
	public static int h_u_KdColor;
	public static int h_u_KsColor;
	
	public static int h_u_UseTexture;
	public static int h_u_Ns;
	public static int h_u_opacity;
	public static int h_a_Position;
	public static final int SIZE_a_Position_floats=3;
	public static final int OFFSET_a_Position_floats=0;
	public static int h_a_TexCoordinate;
	public static final int SIZE_a_TexCoordinate_floats=2;
	public static final int OFFSET_a_TexCoordinate_floats=SIZE_a_Position_floats;
	public static int h_a_Normal;
	public static final int SIZE_a_Normal_floats=3;
	public static final int OFFSET_a_Normal_floats=OFFSET_a_TexCoordinate_floats+SIZE_a_TexCoordinate_floats;
	public static final int STRIDE_floats=OFFSET_a_Normal_floats+SIZE_a_Normal_floats;
	public static final int STRIDE_bytes=STRIDE_floats*Framework3D.BYTES_PER_FLOAT;
	
	public static boolean initialized=false;
	public static void initialize(){
		initialized=true;
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(App.getContext(), R.raw.shader_vs);   		
 		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(App.getContext(), R.raw.shader_fs);
 				
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);		
		
		shaderhandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position","a_TexCoordinate",  "a_Normal"});	

        // Set program handles for cube drawing.
        h_u_MVPMatrix = GLES20.glGetUniformLocation(shaderhandle, "u_MVPMatrix");
        h_u_MVMatrix = GLES20.glGetUniformLocation(shaderhandle, "u_MVMatrix"); 
        h_u_LightPos = GLES20.glGetUniformLocation(shaderhandle, "u_LightPos");
        h_u_Texture = GLES20.glGetUniformLocation(shaderhandle, "u_Texture");
        //h_u_Color = GLES20.glGetUniformLocation(shaderhandle, "u_Color");
        h_u_KaColor = GLES20.glGetUniformLocation(shaderhandle, "u_KaColor");
        h_u_KdColor = GLES20.glGetUniformLocation(shaderhandle, "u_KdColor");
        h_u_KsColor = GLES20.glGetUniformLocation(shaderhandle, "u_KsColor");
        h_u_UseTexture = GLES20.glGetUniformLocation(shaderhandle, "u_UseTexture");
        h_u_Ns = GLES20.glGetUniformLocation(shaderhandle, "u_Ns");
        h_u_opacity = GLES20.glGetUniformLocation(shaderhandle, "u_opacity");
        h_a_Position = GLES20.glGetAttribLocation(shaderhandle, "a_Position");     
        h_a_TexCoordinate = GLES20.glGetAttribLocation(shaderhandle, "a_TexCoordinate");    
        h_a_Normal = GLES20.glGetAttribLocation(shaderhandle, "a_Normal"); 

	}
	public static void use(){
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(shaderhandle);  
		
	}
	private static float[] temporarymatrix=new float[16];
	public static void setMatrices(View view,float[] Modelmatrix){
				Matrix.multiplyMM(temporarymatrix, 0, view.Viewmatrix(), 0, Modelmatrix, 0);
				// Pass in the modelview matrix.
				GLES20.glUniformMatrix4fv(h_u_MVMatrix, 1, false, temporarymatrix,0);
				
				Matrix.multiplyMM(temporarymatrix, 0, view.Projectionmatrix(), 0, temporarymatrix, 0);
			
			// Pass in the combined matrix.
			GLES20.glUniformMatrix4fv(h_u_MVPMatrix, 1, false, temporarymatrix,0);
	}
	public static void setLight(Light light,View view){
		float[] positionineyespace=light.Position.clone();
		positionineyespace[0]=-positionineyespace[0];
		Matrix.multiplyMV(positionineyespace, 0, view.Viewmatrix(), 0, positionineyespace, 0);
		// Pass in the light position in eye space.
		GLES20.glUniform3f(h_u_LightPos, positionineyespace[0], positionineyespace[1], positionineyespace[2]);
	}

	public static void setMaterial(Material material,float opacity){
		GLES20.glUniform1i(h_u_UseTexture, (material.texture==-1)?0/*false*/:1/*true*/);
		if(material.texture==-1){
			GLES20.glUniform4f(h_u_KaColor,material.color_coeffs[0],material.color_coeffs[1],material.color_coeffs[2],1);
			GLES20.glUniform4f(h_u_KdColor,material.color_coeffs[3],material.color_coeffs[4],material.color_coeffs[5],1);
			GLES20.glUniform4f(h_u_KsColor,material.color_coeffs[6],material.color_coeffs[7],material.color_coeffs[8],1);

			//GLES20.glUniform4f(h_u_Color,1,0,1,1);
			
			GLES20.glUniform1f(h_u_Ns, material.color_coeffs[9]);//Ns
		}
		GLES20.glUniform1f(h_u_opacity,(1- material.color_coeffs[10])*opacity);//dissolvance
		//GLES20.glUniform3f(h_u_LightPos, positionineyespace[0], positionineyespace[1], positionineyespace[2]);
	}
	public static void setTexture(int textureid){
		// Pass in the texture information
		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);

		// Tell the texture uniform sampler to use this texture in the
		// shader by binding to texture unit 0.
		GLES20.glUniform1i(h_u_Texture, 0);
		
		
	}
 	public static void setVertices(int GPUvertexdatahandle){
        

		// Pass in the position information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GPUvertexdatahandle);
		GLES20.glEnableVertexAttribArray(h_a_Position);
		GLES20.glVertexAttribPointer(h_a_Position, SIZE_a_Position_floats, GLES20.GL_FLOAT, false, STRIDE_bytes, OFFSET_a_Position_floats * Framework3D.BYTES_PER_FLOAT);

		// Pass in the color/texture information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GPUvertexdatahandle);
		GLES20.glEnableVertexAttribArray(h_a_TexCoordinate);
		GLES20.glVertexAttribPointer(h_a_TexCoordinate, SIZE_a_TexCoordinate_floats, GLES20.GL_FLOAT, false, STRIDE_bytes, OFFSET_a_TexCoordinate_floats * Framework3D.BYTES_PER_FLOAT);
		
		// Pass in the normal information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GPUvertexdatahandle);
		GLES20.glEnableVertexAttribArray(h_a_Normal);
		GLES20.glVertexAttribPointer(h_a_Normal, SIZE_a_Normal_floats, GLES20.GL_FLOAT, false, STRIDE_bytes, OFFSET_a_Normal_floats * Framework3D.BYTES_PER_FLOAT);
		
		// Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		
	}
	public static void prepare(){if(!initialized)initialize();}//forces the shaderprogram class to load and hence calls the static initializer.
	public static void reset(){initialized=false;}

}
