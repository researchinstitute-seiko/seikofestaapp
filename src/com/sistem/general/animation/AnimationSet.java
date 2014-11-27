package com.sistem.general.animation;

import java.util.ArrayList;

public class AnimationSet {

	public AnimationSet() {	}
	public ArrayList<SingleAnimation> values=new ArrayList<SingleAnimation>();
	int duration=0;
	
	public SingleAnimation add(SingleAnimation animation){
		values.add(animation);
		duration=Math.max(duration,animation.chronos+animation.interpolator.duration);
		return animation;
	}
	public void clear(){values.clear();duration=0;}
	
	
	public void frame(Object[] objectlist, float t){
		for(int i=0;i<values.size();i++){
			SingleAnimation anm=values.get(i);
			if(anm.chronos<=t&&t<=anm.chronos+anm.interpolator.duration){
				if(!anm.isrunning)anm.start(objectlist);
				anm.frame(objectlist,t-anm.chronos);
				
			}
			else if(anm.isrunning)anm.end(objectlist);
		}
			
	}
	public void start(Object[] objectlist){
		
	}
	public void abort(Object[] objectlist){
		
	}
	public void end(Object[] objectlist){
		for(int i=0;i<values.size();i++){
			SingleAnimation anm=values.get(i);
			if(anm.isrunning)anm.end(objectlist);
		}
	}
}
 





