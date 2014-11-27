package com.sistem.general.animation;

public abstract class Property<T> {
	public abstract void set(T object, float value);
	//public abstract float get(T object);	
/*
	public float initialvalue;
	public float initialderivative;
	//SingleAnimation nowplayinganimation;
	public Object target;
	public PropertyPath path;
	
	public Property(Object target, PropertyPath path, float initialvalue, float initialderivative){
		this.initialvalue=initialvalue;
		this.initialderivative=initialderivative;
		this.target=target;
		this.path=path;
	}
	
	public Property(Object target, PropertyPath path,float initialvalue){
		this(target,path,initialvalue, 0);
	}
	public void set(float value){
		synchronized(target){
			path.set(target, value);
		}
	}

*/
}
