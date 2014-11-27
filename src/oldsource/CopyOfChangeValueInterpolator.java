package oldsource;

public class CopyOfChangeValueInterpolator extends Interpolator {

	public CopyOfChangeValueInterpolator(float[] endvalues,float[] endderivatives) {
		super(endvalues.length, endvalues, endderivatives);
	}
	public CopyOfChangeValueInterpolator(float[] endvalues) {
		super(endvalues.length, endvalues, new float[endvalues.length]);
	}

	@Override
	public float getValue(float t, int i) {		return endvalues[i];	}
	@Override
	public float getDerivative(float t, int i) {return endderivatives[i];}
	@Override
	public void _start(float duration, boolean ispreferredduration) {}
	@Override
	public float getPreferredDuration() {return 0;}

}
