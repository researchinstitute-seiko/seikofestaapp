package com.sistem.general.animation;


public class LinearInterpolator extends Interpolator{

	public LinearInterpolator(float start,float end,int duration) {
		super(start, end, duration);
	}
	@Override
	public float getValue(float t) {
		float param=((float)t/duration);
		return start*(1-param)+end*param;
	}
	
}
