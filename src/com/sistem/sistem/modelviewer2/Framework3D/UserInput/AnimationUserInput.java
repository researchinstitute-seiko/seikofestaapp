package com.sistem.sistem.modelviewer2.Framework3D.UserInput;

import java.util.Timer;
import java.util.TimerTask;

import android.os.SystemClock;

import com.sistem.sistem.modelviewer2.Framework3D.Framework3D;

public class AnimationUserInput  extends UserInput {

	public volatile double angradx;
	public volatile double angrady;
	public volatile float cameradistance;
	public volatile float cameradistancescale;
	public volatile double fixedangrady;
	public volatile double fixedangradx;
	public volatile float near;
	public volatile float far;
	
	
	public AnimationUserInput(Framework3D framework) {
		super(framework);
	}

	@Override
	public void attached() {
		float x=framework.view.Camera[0]-framework.view.LookAt[0];
		float y=framework.view.Camera[1]-framework.view.LookAt[1];
		float z=framework.view.Camera[2]-framework.view.LookAt[2];
		cameradistance=len(x,y,z);
		fixedangradx=angradx=-Math.atan2(x, z);
		fixedangrady=angrady=Math.atan2(y, Math.hypot(x, z));	
		near=framework.view.near;
		far=framework.view.far;cameradistancescale=1;
		
		starttimemilli=SystemClock.uptimeMillis();
		timer.scheduleAtFixedRate(timertask=new MyTimerTask(callback), 0, intervalmillisecs);
		
		
	}
	//Timer mechanism -- begin
	private int intervalmillisecs=10;

	private Timer timer=new Timer();
	private volatile long starttimemilli;
	private Runnable callback =new Runnable(){

		@Override
		public void run() {
			//do some work;
			long elapsedmilli=SystemClock.uptimeMillis()-starttimemilli;
			angradx=fixedangradx+elapsedmilli*0.001*Math.toRadians(45);
			angrady=fixedangrady+Math.toRadians(20*Math.sin(elapsedmilli*0.001/50*(Math.PI*2)));
			cameradistancescale=(float)Math.pow(2,Math.sin(elapsedmilli*0.001/40*(Math.PI*2)));
			setcamerapos();
		}
		
	};
	private TimerTask timertask;
	class MyTimerTask extends TimerTask
	{
		Runnable runnable;
		public MyTimerTask(Runnable runnable){this.runnable=runnable;}
		@Override
		public void run() {runnable.run();}
		
	};
	//Timer mechanism -- end
	private void setcamerapos(){

		framework.view.near=near*cameradistancescale;
		framework.view.far=far*(float)Math.max(Math.sqrt(cameradistancescale),cameradistancescale);
		float x=(float)(cameradistance*cameradistancescale*Math.sin(-angradx)*Math.cos(angrady))+framework.view.LookAt[0];
		float y=(float)(cameradistance*cameradistancescale*Math.sin(angrady))+framework.view.LookAt[1];
		float z=(float)(cameradistance*cameradistancescale*Math.cos(-angradx)*Math.cos(angrady))+framework.view.LookAt[2];
		framework.view.Camera[0]=x;
		framework.view.Camera[1]=y;
		framework.view.Camera[2]=z;
		framework.requestRender();
	}

	@Override
	public void detached() {
		timertask.cancel();
		
	}

}
