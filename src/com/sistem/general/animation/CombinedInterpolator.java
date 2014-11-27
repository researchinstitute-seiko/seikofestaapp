package com.sistem.general.animation;

import java.util.ArrayList;


public class CombinedInterpolator extends Interpolator {
	public CombinedInterpolator() {super(Float.NaN, Float.NaN, 0);}
	public ArrayList<Interpolator> entries=new ArrayList<Interpolator>();
	public Interpolator add(Interpolator entry){
		return add(entry,duration);
	}
	public Interpolator add(Interpolator entry,int chronos){
		entries.add(entry);
		if(chronos<duration)throw new IllegalArgumentException();
		entry._chronos=chronos;
		duration+=entry.duration;
		if(entries.size()==0)start=entry.start;
		end=entry.end;
		return entry;
	}
	//public boolean remove(SingleAnimation entry){return entries.remove(entry);}
	//public Interpolator remove(int index){return entries.remove(index);}
	public void clear(){entries.clear();duration=0;start=Float.NaN;end=Float.NaN;}
	public Interpolator getlastentry(){return entries.get(entries.size()-1);}
	Interpolator cache=null;
	@Override
	public float getValue(float t) {		
		if(cache!=null){
			if(cache._chronos<=t&&t<cache._chronos+cache.duration) return cache.getValue(t-cache._chronos);
		}
		if(t==duration)return entries.get(entries.size()-1).end;
		int i=rec_binarysearch(t,0,entries.size());
		if(i<0) return entries.get(~i).end;
		else{
			Interpolator item=entries.get(i);
			return item.getValue(t-item._chronos);
		}
	}
	
	private int rec_binarysearch(float t,int low,int high){
		if(low==high) return ~low;
		int test=(low+high)/2;
		Interpolator item=entries.get(test);
		if(item._chronos<=t&&t<item._chronos+item.duration) return test;
		else if(t<item._chronos)return rec_binarysearch(t,low,test);
		else return rec_binarysearch(t,test+1,high);
	}
	
	
}
