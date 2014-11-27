package com.sistem.sistem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyTimer {
	private int interval;
	public MyTimer(int interval){
		this.interval=interval;	
	}
	public MyTimer(){
		this.interval=10;
	}
	ScheduledExecutorService es=Executors.newSingleThreadScheduledExecutor();
	ScheduledFuture<?> nowrunning;
	public void Do(Runnable task,int delay,int interval){
		Stop();
		//timer=new Timer();
		nowrunning = es.scheduleAtFixedRate(task, delay, interval, TimeUnit.MILLISECONDS);
		//timer.scheduleAtFixedRate(task, delay, interval);
	}
	public void Do(Runnable task){
		Do(task,0,interval);
	}
	public void Do(Runnable task,int delay){
		Do(task,delay,interval);
	}
	public void Stop(){
//		if(timer!=null){timer.cancel();timer.purge();timer=null;}
		if(nowrunning!=null){
			nowrunning.cancel(true);
			nowrunning=null;
		}
	}
	public void Ensure(Runnable task){
		//if(timer==null)Do(task);
		if(nowrunning==null)Do(task);
	}
}
