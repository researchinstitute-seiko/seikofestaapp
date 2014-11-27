package com.sistem.general.animation;


public class SingleAnimation {
	public int objectindex;
	@SuppressWarnings("rawtypes")
	public Property property;
	public Interpolator interpolator;
	public int chronos;
	//public Chronos start;
	//public Chronos end;
	//public SingleAnimation(){}
	public SingleAnimation (int objectindex,@SuppressWarnings("rawtypes") Property property, Interpolator interpolator,int chronos){
		this.objectindex=objectindex;
		this.property=property;
		this.interpolator=interpolator;
		this.chronos=chronos;
		//this.start=start;
		//this.end=end;
	}
	public boolean isrunning;
	//@SuppressWarnings("unchecked")
	public void start(Object[] list){
		//property.set(list[objectindex],interpolator.start);
		this.isrunning=true;
	}
	public void abort(Object[] list){
		this.isrunning=false;
	}
	//@SuppressWarnings("unchecked")
	public void end(Object[] list){
		//property.set(list[objectindex],interpolator.end);
		this.isrunning=false;
		
	}
	@SuppressWarnings("unchecked")
	public void frame(Object[] list, float t){
		property.set(list[objectindex], interpolator.getValue(t));
	}
}
