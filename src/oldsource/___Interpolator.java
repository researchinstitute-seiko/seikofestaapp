package oldsource;

public abstract class __Interpolator {
	
	public Interpolator() {}
	public Interpolator(float end, float speed) {
		this(dimension, endvalues, endderivatives,startvalues,startderivatives, Float.NaN);
	}
	public Interpolator(int dimension,float[] endvalues,float[] endderivatives,float[] startvalues, float[] startderivatives,float preferredspeed) {
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
	public abstract float getValue(float t);
	//
	public abstract float getDerivative(float t);
	public float start,end;
	//public float start(float t){_isplaying=true;if(dimension<1)throw new IllegalStateException("Dimension must be positive."); return _duration=start(t);}
	public void start(float duration,boolean ispreferredduration){
		_isplaying=true;
		if(duration<0)throw new IllegalStateException("Duration must be non-negative.");
		this.duration=duration;
		_start(duration,ispreferredduration);
	}
	public abstract void _start(float duration,boolean ispreferredduration);
	public abstract float getPreferredDuration();
	public void abort(float t){_isplaying=false;}
	public void stop(){_isplaying=false;}
	
}
