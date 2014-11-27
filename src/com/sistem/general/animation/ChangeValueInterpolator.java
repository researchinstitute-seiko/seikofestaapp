package com.sistem.general.animation;

public class ChangeValueInterpolator extends Interpolator {

public ChangeValueInterpolator(float start,float end) {
	super(start, end, 0);
}
@Override
	public float getValue(float t) {
		return end;
	}


}
