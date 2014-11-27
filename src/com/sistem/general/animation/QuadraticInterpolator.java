package com.sistem.general.animation;


public class QuadraticInterpolator extends Interpolator {

	public QuadraticInterpolator(float start,float end,float d,boolean isds,int duration) {
		super(start, end, duration);
		this.d=d;
		this.isds=isds;
	}
	boolean isds;
	float d;
	float a,b,c;

	@Override
	public float getValue(float t) {
		return c+t*(b+t*a);
	}
	

	@Override
	public void start() {
		super.start();
		long l2=duration*duration;
		if(isds){
				//[A=(xf-xi-l*vi)/l^2,B=vi,C=xi]
				a=(end-start-duration*d)/l2;
				b=d;
				c=start;
		}
		else{
				//i[A=-(xf-xi-l*vf)/l^2,B=(2*xf-2*xi+l*vf)/l,C=xi]
				float xdiff=end-start;
				a=-(xdiff-duration*d)/l2;
				b=2.0f*xdiff/duration+d;
				c=start;
		}
	}
	
	


}
class QuadraticBezier{
	public static float bezier(float x1,float x2,float x3,float t){
		//(c-b+a)*t^2+(b-2*a)*t+a
		return x1+t*(x1*2+x2+t*(x1-x2+x3));
	}
	public static float bezierdiff(float x1,float x2,float x3,float t){
		//2*(c-b+a)*t+b-2*a
		return x2-2*x1+t*2*(x1-x2+x3);
	}
	public static float bezierapprlen(float[] points, int dimension,int division)
	{
	    float len=0;
	    //if(buf.length<dimension)buf=new float[Math.max(buf.length*2, dimension)];
	    len+=getmagofdiff(points,dimension,0);
	    float delta=1.0f/(division*2);
	    for(int i=1;i<division;i++)
	    	len+=(getmagofdiff(points,dimension,(i*2-1)*delta)*2+getmagofdiff(points,dimension,(i*2)*delta))*2;
	    len+=getmagofdiff(points,dimension,1);
	    return len;    
	}  
	public static float getmagofdiff(float[] points,int dimension,float t){
		if(dimension==1)return bezierdiff(points[0],points[1],points[2],t);
		float len2=0;
	    for(int i=0;i<dimension;i++){
	    	len2+=CubicBezier.square(bezierdiff(points[i],points[i+dimension],points[i+dimension*2],t));
	    }
	    return (float)Math.sqrt(len2);
	}
	
}
