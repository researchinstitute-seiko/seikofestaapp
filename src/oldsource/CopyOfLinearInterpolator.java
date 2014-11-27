package oldsource;


public class CopyOfLinearInterpolator extends Interpolator{
	public CopyOfLinearInterpolator(float[] endvalues, float speed) {
		super(endvalues.length,endvalues,new float[endvalues.length],speed);
	}
	public CopyOfLinearInterpolator(float[] endvalues) {
		this(endvalues,Float.NaN);
	}
	@Override
	public float getValue(float t, int i) {
		return endderivatives[i]*t+startvalues[i];
	}
	/*@Override
	public float getDerivative(float t, int i) {
		return endderivatives[i];
	}*/
	@Override
	public void _start(float duration, boolean ispreferredduration) {
		for(int i=0;i<dimension;i++) endderivatives[i]=(endvalues[i]-startvalues[i])/duration;
	}
	@Override
	public float getPreferredDuration() {
		return CubicBezier.getlen(endvalues, 0, dimension)/preferredspeed;
	}
}
