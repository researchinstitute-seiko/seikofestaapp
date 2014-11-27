package oldsource;


public class CopyOfQuadraticInterpolator extends Interpolator {

	public CopyOfQuadraticInterpolator(float[]endvalues,float[] endderivatives,float speed) {
		super(endvalues.length, endvalues, endderivatives, speed);
		this.startsmoothed=false;
		this.coeffs=new float[endvalues.length*3];
	}

	public CopyOfQuadraticInterpolator(float[]endvalues,float[] endderivatives) {
		this(endvalues, endderivatives, Float.NaN);
	}
	public CopyOfQuadraticInterpolator(float[]endvalues,float speed) {
		super(endvalues.length, endvalues, null, speed);
		this.startsmoothed=true;
		this.coeffs=new float[endvalues.length*3];
	}

	public CopyOfQuadraticInterpolator(float[]endvalues) {
		this(endvalues, Float.NaN);
	}
	boolean startsmoothed;
	float[] coeffs;

	@Override
	public float getValue(float t, int i) {
		return coeffs[i*dimension+2]+t*(coeffs[i*dimension+1]+t*(coeffs[i*dimension]));
	}

	@Override
	public float getDerivative(float t, int i) {
		return coeffs[i*dimension+1]+t*(2*coeffs[i*dimension+1]);
	}
	

	@Override
	public void _start(float duration, boolean ispreferredduration) {

		float l2=duration*duration;
		if(startsmoothed){
			for(int i=0;i<dimension;i++){
				//[A=(xf-xi-l*vi)/l^2,B=vi,C=xi]
				coeffs[i*3+0]=(endvalues[i]-startvalues[i]-duration*startderivatives[i])/l2;
				coeffs[i*3+1]=startderivatives[i];
				coeffs[i*3+2]=startvalues[i];
			}
		}
		else{
			for(int i=0;i<dimension;i++){
				//i[A=-(xf-xi-l*vf)/l^2,B=(2*xf-2*xi+l*vf)/l,C=xi]
				float xdiff=endvalues[i]-startvalues[i];
				coeffs[i*3+0]=-(xdiff-duration*endderivatives[i])/l2;
				coeffs[i*3+1]=2*xdiff/duration+endderivatives[i];
				coeffs[i*3+2]=startvalues[i];
			}
		}
	}
	
	

	
	public static float[] buffer=new float[256];
	@Override
	public float getPreferredDuration(){
		if(startsmoothed){
			for(int i=0;i<dimension;i++){
				buffer[i]=startvalues[i];
				buffer[i+dimension]=startvalues[i]+startderivatives[i]/(2*preferredspeed);
				buffer[i+2*dimension]=endvalues[i];
			}
		}
		else{
			for(int i=0;i<dimension;i++){
				buffer[i]=startvalues[i];
				buffer[i+dimension]=endvalues[i]-endderivatives[i]/(2*preferredspeed);
				buffer[i+2*dimension]=endvalues[i];
			}			
		}
		return QuadraticBezier.bezierapprlen(buffer, dimension, 16)/preferredspeed;
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
