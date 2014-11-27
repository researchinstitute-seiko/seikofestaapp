package com.sistem.general.animation;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Animation {

	private Timer timer=new Timer();
	private int timerinterval;
	private boolean isrunning=false;
	public Animation() {this(40);}
	public Animation(int timerinterval) {this.timerinterval=timerinterval;}
	ArrayList<AnimationState> nowrunning=new ArrayList<AnimationState>();
	public void Run(AnimationSet animationset, Object[] objectlist){Run(animationset, objectlist, 0, 1);}
	public void Run(AnimationSet animationset, Object[] objectlist,float startt,float speed){
		AnimationState anms=new AnimationState();
		long timestamp=System.currentTimeMillis();
		anms.starttime=timestamp;
		anms.objectlist=objectlist;
		anms.set=animationset;
		anms.currentchronos=startt;
		anms.start=startt;
		anms.speed=speed;
		nowrunning.add(anms);
		animationset.start(objectlist);
		if(!isrunning){
			timer.schedule(new TimerTask() {	
				@Override
				public void run() {callback();	}
			}, 0,timerinterval);
		}
		anms.isrunning=true;
	}
	protected void callback(){
		long timestamp=System.currentTimeMillis();
		for(int i=0;i<nowrunning.size();i++){
			AnimationState anms=nowrunning.get(i);
			float t=(timestamp-anms.starttime)*anms.speed+anms.start;
			anms.currentchronos=t;
			if(t>=anms.set.duration||t<=0){
				t=Math.max(Math.min(t, anms.set.duration), 0);
				anms.set.frame(anms.objectlist,t );
				anms.set.end(anms.objectlist);
				nowrunning.remove(i);
				if(nowrunning.size()==0){timer.cancel();timer.purge();isrunning=false;}
				i--;
			}
			else{
				anms.set.frame(anms.objectlist, t);
			}
			
		}
	}
	
	
}
class AnimationState{
	public AnimationSet set;
	public Object[] objectlist;
	public long starttime;
	public float start;
	public float currentchronos;
	public boolean isrunning;
	public float speed;
	
}
