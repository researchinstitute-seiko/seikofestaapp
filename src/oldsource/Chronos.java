package oldsource;

public class Chronos {
	public Chronos() {}
	public Chronos(Chronos relative, float offset){this.relative=relative;this.offset=offset;}
	public Chronos relative;
	public float offset;
	public float temp;
	public static Chronos createFree(){return new Chronos();}
	public Chronos createDelayed(float delay){return new Chronos(this,delay);}
}