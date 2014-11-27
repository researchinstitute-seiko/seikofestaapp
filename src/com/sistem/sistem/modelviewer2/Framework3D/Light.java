package com.sistem.sistem.modelviewer2.Framework3D;

public class Light {

	public float[] Position;
	public Light(float x,float y,float z) {
		Position=new float[]{x,y,z,1};
	}

	public float[] Position() {
		return Position;
	}

}
