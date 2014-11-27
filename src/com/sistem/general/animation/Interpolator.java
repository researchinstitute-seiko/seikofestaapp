package com.sistem.general.animation;

public abstract class Interpolator {
	
	public Interpolator() {	}
	public Interpolator(float start,float end,int duration) {
		this.start=start;
		this.end=end;
		this.duration=duration;
	}
	
	
	boolean _isplaying;
	public boolean isPlaying(){return _isplaying;}
	public abstract float getValue(float t);
	
	int _chronos;

	public int duration;
	public float start,end;
	public void start(){}
	
}
