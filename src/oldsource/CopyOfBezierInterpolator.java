package oldsource;

public class CopyOfBezierInterpolator extends Interpolator {
	public CopyOfBezierInterpolator(float[] endvalues, float[] endderivatives,float speed) {
		super(endvalues.length,endvalues,endderivatives,speed);
		this.coeffs=new float[endvalues.length*4];
	}

	public CopyOfBezierInterpolator(float[] endvalues, float[] endderivatives) {
		this(endvalues,endderivatives,Float.NaN);
	}
	
	
	static float[] buffer=new float[256];
	float[] coeffs;
	
	@Override
	public float getValue(float t, int i) {
		return coeffs[i*dimension+3]+t*(coeffs[i*dimension+2]+t*(coeffs[i*dimension+1]+t*coeffs[i*dimension]));
	}

	@Override
	public float getDerivative(float t, int i) {
		return coeffs[i*dimension+2]+t*(2*coeffs[i*dimension+1]+t*3*coeffs[i*dimension]);
	}
	
	

	@Override
	public void _start(float duration,boolean ispreferredduration) {

		float l2=duration*duration;
		float l3=l2*duration;
		for(int i=0;i<dimension;i++){
			//[A=-(2*(xf-xi)+l*(vi+vf))/l^3,B=(3*(xf-xi)+l*(2*vi+vf))/l^2,C=vi,D=xi]
			//solve for the coefficients
			float xdiff=endvalues[i]-startvalues[i];
			float vsum=startderivatives[i]+endderivatives[i];
			coeffs[i*4]=-(2*xdiff+duration*vsum)/l3;
			coeffs[i*4+1]=(3*xdiff+duration*(startderivatives[i]+vsum))/l2;
			coeffs[i*4+2]=startderivatives[i];
			coeffs[i*4+3]=startvalues[i];
		}
	}

	@Override
	public float getPreferredDuration(){
		for(int i=0;i<dimension;i++){
			buffer[i]=startvalues[i];
			buffer[i+dimension]=startvalues[i]+startderivatives[i]/(3*preferredspeed);
			buffer[i+2*dimension]=endvalues[i]-endderivatives[i]/(3*preferredspeed);
			buffer[i+3*dimension]=endvalues[i];
		}
		return CubicBezier.bezierapprlen(buffer, dimension, 16)/preferredspeed;
	}

}
class CubicBezier{
	
    //static float[] buf=new float[32];
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
		if(dimension==1)return bezierdiff(points[0],points[1],points[2],points[3],t);
		float len2=0;
	    for(int i=0;i<dimension;i++){
	    	len2+=square(bezierdiff(points[i],points[i+dimension],points[i+dimension*2],points[i+dimension*3],t));
	    }
	    return (float)Math.sqrt(len2);
	}
	public static float getlen(float[] points,int offset,int dimension){
		if(dimension==1)return points[offset];
	    float len2=0;
	    for(int i=0;i<dimension;i++)	    	len2+=points[offset+i]*points[offset+i];
	    return (float)Math.sqrt(len2);
	}
	public static float square(float x){return x*x;}
	public static float bezier(float x1,float x2,float x3,float x4, float t){
		float d1=3*(x2-x1);
		float d2=3*(x3-x2);
		//(dx-3*cx+3*bx-ax)*t^3+(3*cx-6*bx+3*ax)*t^2+(3*bx-3*ax)*t+ax
		return x1+t*(d1+t*(d2-d1+t*(x4-d2-x1)));
	}
	public static float bezierdiff(float x1,float x2,float x3,float x4,float t){
		//3*((dx-3*cx+3*bx-ax)*t^2+(2*cx-4*bx+2*ax)*t+bx-ax)
		float d1=x2-x1;
		float d2=x3-x2;
		return 3*(d1+t*(2*(d2-d1)+t*(x4-3*d2-x1)));
	}
}
