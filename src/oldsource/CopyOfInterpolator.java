package oldsource;

public abstract class CopyOfInterpolator {
	
	public CopyOfInterpolator(int dimension) {
		this(dimension, null, null,null,null, Float.NaN);
	}
	public CopyOfInterpolator(int dimension,float[] endvalues,float[] endderivatives,float[] startvalues, float[] startderivatives) {
		this(dimension, endvalues, endderivatives,startvalues,startderivatives, Float.NaN);
	}
	public CopyOfInterpolator(int dimension,float[] endvalues,float[] endderivatives,float[] startvalues, float[] startderivatives,float preferredspeed) {
		this.dimension=dimension;
		this.startvalues=startvalues;
		this.startderivatives=startderivatives;
		this.endvalues=endvalues;
		this.endderivatives=endderivatives;
	}
	
	protected float duration;
	public float getDuration(){return duration;}
	protected float preferredspeed;
	public float getPreferredSpeed() {return preferredspeed;}
	
	boolean _isplaying;
	public boolean isPlaying(){return _isplaying;}
	public abstract float getValue(float t,int i);
	//
	public abstract float getDerivative(float t,int i);
	protected int dimension=0;
	public int getDimension() {	return dimension;}
	
	public float[] endvalues, endderivatives,startvalues,startderivatives;
	//public float start(float t){_isplaying=true;if(dimension<1)throw new IllegalStateException("Dimension must be positive."); return _duration=start(t);}
	public void start(float duration,boolean ispreferredduration){
		_isplaying=true;
		if(dimension<1)throw new IllegalStateException("Dimension must be positive.");
		if(duration<0)throw new IllegalStateException("Duration must be non-negative.");
		this.duration=duration;
		_start(duration,ispreferredduration);
	}
	public abstract void _start(float duration,boolean ispreferredduration);
	public abstract float getPreferredDuration();
	public void abort(float t){_isplaying=false;}
	public void stop(){_isplaying=false;}
	public float getEndvalue(int i){if(this.endvalues==null)return Float.NaN;return endvalues[i];}
	//
	public float getEndderivative(int i){if(this.endderivatives==null)return Float.NaN;return endderivatives[i];}
	
	/*public void ensurebuffer(int size){
	if(size>capacity){
		capacity=Math.max(capacity*2, size);
		startvalues=new float[capacity];
		startderivatives=new float[capacity];
	}
}*/
}
