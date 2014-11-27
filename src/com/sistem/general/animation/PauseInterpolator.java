package com.sistem.general.animation;

public class PauseInterpolator extends Interpolator {

	public PauseInterpolator(float value, int duration) {
		super(value,value, duration);
	}
	public PauseInterpolator(int duration) {
		super(Float.NaN,Float.NaN, duration);
	}

	@Override
	public float getValue(float t) {
		return end;
	}

}
