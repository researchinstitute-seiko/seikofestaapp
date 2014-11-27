package com.sistem.sistem.modelviewer2.Framework3D;

public class Quaternion {
	public static void setMatrix3D(float[] matrix, float[] quat)
	{	 
	   matrix[0]  = (1.0f - (2.0f * ((quat[2] * quat[2]) + (quat[3] * quat[3]))));
	   matrix[1]  = (2.0f * ((quat[1] * quat[2]) - (quat[3] * quat[0])));
	   matrix[2]  = (2.0f * ((quat[1] * quat[3]) + (quat[2] * quat[0])));
	   matrix[3] = 0.0f;
	   matrix[4]  = (2.0f * ((quat[1] * quat[2]) + (quat[3] * quat[0])));
	   matrix[5]  = (1.0f - (2.0f * ((quat[1] * quat[1]) + (quat[3] * quat[3]))));
	   matrix[6]  = (2.0f * ((quat[2] * quat[3]) - (quat[1] * quat[0])));
	   matrix[7] = 0.0f;
	   matrix[8]  = (2.0f * ((quat[1] * quat[3]) - (quat[2] * quat[0])));
	   matrix[9]  = (2.0f * ((quat[2] * quat[3]) + (quat[1] * quat[0])));
	   matrix[10] = (1.0f - (2.0f * ((quat[1] * quat[1]) + (quat[2] * quat[2]))));
	   matrix[11] = 0.0f;
	   matrix[12]  = 0.0f;
	   matrix[13]  = 0.0f;
	   matrix[14] = 0.0f;
	   matrix[15] = 1.0f;

	}
	public static void normalize(float[] quat, float tolerance){
		float mag2=quat[0]*quat[0]+quat[1]*quat[1]+quat[2]*quat[2]+quat[3]*quat[3];
		if((mag2-1)*(mag2-1)>tolerance){
			float mag=(float) Math.sqrt(mag2);
			quat[0]=quat[0]/mag;
			quat[1]=quat[1]/mag;
			quat[2]=quat[2]/mag;
			quat[3]=quat[3]/mag;
		}
	}
	public void conjugate(float[] quat)
	{
		quat[0]=-quat[0];quat[1]=-quat[1];quat[2]=-quat[2];
	}
	public void multiply (float[] result,float[] q1, float[] q2) 
	{
		float X=q1[3] * q2[0] + q1[0] * q2[3] + q1[1] * q2[2] - q1[2] * q2[1];
		float Y=q1[3] * q2[1] + q1[1] * q2[3] + q1[2] * q2[0] - q1[0] * q2[2];
		float Z=q1[3] * q2[2] + q1[2] * q2[3] + q1[0] * q2[1] - q1[1] * q2[0];
		float W=q1[3] * q2[3] - q1[0] * q2[0] - q1[1] * q2[1] - q1[2] * q2[2];
		result[0]=X;
		result[1]=Y;
		result[2]=Z;
		result[3]=W;
	}
	public void apply (float[] vector, float[] quat) 
	{	 
	 
		vector[3]=0;//turn a vec4 into a quaternion
		multiply(vector,quat,vector);
		conjugate(quat);
		multiply(vector,vector,quat);
		conjugate(quat);
		vector[3]=1;//turn the quaternion back to a vec4;
	}
	public void getQuatFromAxis(float[] quat,float x, float y,float z, float angle)
	{
		float sinAngle;
		angle *= 0.5f;
		float l2=x*x+y*y+z*z;
		float l=(float) Math.sqrt(l2);
		sinAngle = (float) Math.sin(angle);
		
		quat[0] = (x/l * sinAngle);
		quat[1] = (y/l * sinAngle);
		quat[2] = (z/l * sinAngle);
		quat[3] = (float) Math.cos(angle);
	}
	public void getQuatFromEuler(float[] quat, float pitch, float yaw, float roll)
	{
		// Basically we create 3 Quaternions, one for pitch, one for yaw, one for roll
		// and multiply those together.
		// the calculation below does the same, just shorter
	 
		float p = (float)(pitch * (Math.PI/180) / 2.0);
		float y = (float)(yaw * (Math.PI/180) / 2.0);
		float r = (float)(roll * (Math.PI/180) / 2.0);
	 
		float sinp = (float) Math.sin(p);
		float siny = (float) Math.sin(y);
		float sinr = (float) Math.sin(r);
		float cosp = (float) Math.cos(p);
		float cosy = (float) Math.cos(y);
		float cosr = (float) Math.cos(r);
	 
		quat[0]= sinr * cosp * cosy - cosr * sinp * siny;
		quat[1] = cosr * sinp * cosy + sinr * cosp * siny;
		quat[2] = cosr * cosp * siny - sinr * sinp * cosy;
		quat[3] = cosr * cosp * cosy + sinr * sinp * siny;
	 
		normalize(quat,0);
	}
}
