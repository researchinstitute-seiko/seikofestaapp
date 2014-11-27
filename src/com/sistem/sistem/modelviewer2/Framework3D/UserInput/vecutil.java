package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

public class vecutil {
	
	

	public static void normalize(float[] vec){
		float len=len(vec[0],vec[1],vec[2]);
		vec[0]/=len;
		vec[1]/=len;
		vec[2]/=len;
	}
	public static void crossproduct(float[] result,float[] p1,float[] p2){
		float x=p1[1]*p2[2]-p1[2]*p2[1];
		float y=p1[2]*p2[0]-p1[0]*p2[2];
		float z=p1[0]*p2[1]-p1[1]*p2[0];
		result[0]=x;
		result[1]=y;
		result[2]=z;
		
	}
	public static void add(float[] result,float[] p1,float[] p2){
		result[0]=p1[0]+p2[0];
		result[1]=p1[1]+p2[1];
		result[2]=p1[2]+p2[2];
	}
	public static void substract(float[] result,float[] p1,float[] p2){
		result[0]=p1[0]-p2[0];
		result[1]=p1[1]-p2[1];
		result[2]=p1[2]-p2[2];
	}
	public static void scale(float[] result,float scale,float[] p){
		result[0]=scale*p[0];
		result[1]=scale*p[1];
		result[2]=scale*p[2];
	}

	public static double clamp(double x,double min,double max){
		return Math.max(min, Math.min(max, x));
	}

	public static float clamp(float x,float min,float max){
		return Math.max(min, Math.min(max, x));
	}
	public static float len2(float x, float y, float z){
		return x*x+y*y+z*z;
	}
	public static float len(float x,float y,float z){
		return (float) Math.sqrt(len2(x,y,z));
	}
	//Calculates (v1-v2).(v3-v4)
	public static float subdot(vec v1,vec v2,vec v3,vec v4) {
		return (v1.x-v2.x)*(v3.x-v4.x)+(v1.y-v2.y)*(v3.y-v4.y)+(v1.z-v2.z)*(v3.z-v4.z);
	}
	public static float subcross2d(vec v1,vec v2,vec v3,vec v4) {
		return (v1.x-v2.x)*(v3.y-v4.y)-(v1.y-v2.y)*(v3.x-v4.x);
	}
	//Calculates (v1-v2).(v3-v4)
		public static float dist2(vec v1,vec v2) {
			return subdot(v1,v2,v1,v2);
		}
	
	public static  int intersect2d(vec p1,vec p2, vec p3, vec  p4, vec output) {
		// First find Ax+By=C values for the two lines
		double A1 = p2.y- p1.y;
		double B1 = p1.x - p2.x;
		double C1 = A1 * p1.x + B1 * p1.y;

		double A2 = p4.y - p3.y;
		double B2 = p3.x - p4.x;
		//double C2 = A2 * p3.x + B2 * p3.y;

		double det = (A1 * B2) - (A2 * B1);

		if (Math.abs(det) <= 0.00001) {
			if ((A1 * p3.x) + (B1 * p3.y) == C1)return -1;else return 1;
		} else {
			double Dx=p3.x-p1.x,Dy=p3.y-p1.y;
			
			double u= (Dx*A2+Dy*B2)/det;
			double v= (Dx*A1+Dy*B1)/det;
			output.x=(float)u;
			output.y=(float)v;
			return 0;
		}
	}
	public static float getht(vec lp1,vec lp2,vec p) {
		return subdot(lp1,p,lp2,lp1)/dist2(lp1,lp2);
	}
	public static  float getdistance2(vec lp1,vec lp2, vec p) {
		/*vec buf=vec.getnew();
		float ret= buf.setpoint(lp1, lp2, getht(lp1,lp2,p)).sub(p).len();
		buf.recycle();
		return ret;*/
		vec buf1=vec.getnew(),buf2=vec.getnew();
		buf1.set(lp2).sub(lp1);
		buf2.set(lp1).sub(p);
		vec buf3=vec.getnew().setcross(buf1, buf2);
		float ret=buf3.len2()-buf1.len2();
		buf1.recycle();
		buf2.recycle();
		buf3.recycle();
		return ret;
		
	}
	
	
}
