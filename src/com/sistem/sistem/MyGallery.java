package com.sistem.sistem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class MyGallery extends View {
	public volatile int[] imageids;
	private volatile Resources res;
	private volatile Bitmap[] bufs;
	public void setImages(Resources res,int[] imageids) {
		this.imageids = imageids;
		this.res=res;
		this.bufs=new Bitmap[imageids.length];
	}
	public int[] getImageIds() {
		return imageids;
	}
	
	volatile float state=0;
	
	public MyGallery(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}
	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	public MyGallery(Context context) {
		super(context);
		initialize();
	}
	void initialize(){
		start();
	}
	public void start(){

		startanimation();
	}
	protected void onDetachedFromWindow() {
		clean();
	}
	public void clean() {
		if(bufs!=null)
			for (int i = 0; i < bufs.length; i++) 
				if(bufs[i]!=null) {bufs[i].recycle();bufs[i]=null;}
		_rescount=0;
		lastimageid=-1;
	}
	int _rescount=0;
	void checkimage() {
		int currentimage=Math.round(state);
		for(int i=currentimage+3;i<=currentimage+imageids.length-3;i++) {
			int idx=mod(i,imageids.length);
				if(bufs[idx]!=null) {
					_rescount-=1;
					bufs[idx].recycle();
					bufs[idx]=null;
				}
			
		}
		for(int i=currentimage-2;i<=currentimage+2;i++) {
			int idx=mod(i,imageids.length);
			if(bufs[idx]==null) {
				Options opts=new Options();
				opts.inInputShareable=true;
				opts.inPurgeable=true;
				try {
					bufs[idx]=Util.getScaledBitmap(imageids[idx], res, this.getWidth(),opts);
					_rescount++;
					//throw new OutOfMemoryError();
				}catch(OutOfMemoryError ex) {
					//ex.printStackTrace();
					Log.e("MEMINFO","Ran out of memory: rescount="+Integer.toString(_rescount));
				}
				postInvalidate();
			}	
		}
		Log.d("MEMINFO", "rescount="+Integer.toString(_rescount));
		
	}
	
	
	Rect r=new Rect();

	static final float heightratio=0.65f;
	static final float imageinterval=0.7f;
	//static final float heightratio=0.48f;
	//static final float imageinterval=0.53f;
	static final float transparencycoefficient=0.5f;
	ExecutorService es=Executors.newSingleThreadExecutor();//Executors.newSingleThreadScheduledExecutor();
	Runnable checkimage_runnable=new Runnable() {
		
		@Override
		public void run() {
			checkimage();
		}
	};
	int lastimageid=-10000;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		r.set(0, 0, this.getWidth(),this.getHeight());
		canvas.clipRect(r);

		
		if(res!=null&&imageids!=null&&imageids.length>0){
			int currentimage=Math.round(state);
			if(lastimageid!=currentimage) {
						es.execute(checkimage_runnable);}
			lastimageid=currentimage;
			
			float delta=state-currentimage;
			
			drawimg(canvas,currentimage+1,1-transparencycoefficient*(delta-1)*(delta-1),imageinterval*(delta-1),heightratio);
			drawimg(canvas,currentimage,1-transparencycoefficient*delta*delta,imageinterval*delta,heightratio);
			drawimg(canvas,currentimage-1,1-transparencycoefficient*(delta+1)*(delta+1),imageinterval*(delta+1),heightratio);
		}
		//canvas.drawv state%images.size()
	}
	Paint p=new Paint();
	private void drawimg(Canvas canvas,int imgindex,float alpha,float pos,float heightratio){

		int actualalpha=Math.max(0,Math.min((int)Math.floor(alpha*256),255));
		//Bitmap img=images.get(mod(imgindex,images.size()));
		Bitmap img=bufs[mod(imgindex,imageids.length)];
		if(img==null) {
			//TODO: Write the routine when the bitmap has not been loaded.
			return;
		}
		float imgw=img.getWidth();
		float imgh=img.getHeight();
		float windoww=this.getWidth();
		float windowh=this.getHeight();
		float actualh=heightratio*windowh;
		float ratio=actualh/imgh;
		float actualw=ratio*imgw;
		float actualx=(windoww-actualw)*0.5f;
		float actualy=windowh*(pos+0.5f)-actualh*0.5f;
		p.setAlpha(actualalpha);
		canvas.drawBitmap(img,new Rect(0, 0, (int) imgw,(int)imgh) , new Rect((int)actualx,(int)actualy,(int)(actualx+actualw),(int)(actualy+actualh)), p);
		
	}
	static int mod(int x,int mod){
		return ((x%mod)+mod)%mod;
	}

	static float mod(float x,float mod){
		return ((x%mod)+mod)%mod;
	}
	
	
	
	
	
	float lastx,lasty;
	float downx,downy;
	boolean tapping;
	boolean slided;
	boolean down;
	MyTimer timer=new MyTimer(40);
	long lasttime;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action=event.getAction();
		float x=0,y=0;
		long time=	event.getEventTime();
		if(event.getPointerCount()>0){
			x=event.getX(0);
			y=event.getY(0);
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:

				if(event.getPointerCount()==1){
					this.downx=x;
					this.downy=y;
					this.tapping=true;
					this.slided=false;
					down=true;
					OnDown(x,y);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(event.getPointerCount()==1){
					//this.tapping=false;
					this.slided=true;
					if(Math.hypot(x-downx,y-downy)>5)this.tapping=false;
					if(time<=lasttime)return true;
					OnSlide(x,y,x-lastx,y-lasty,x-downx,y-downy,(int)(time-lasttime));
				}
				
				break;
			case MotionEvent.ACTION_UP:

				if(event.getPointerCount()==1){
					OnUp(x,y,x-downx,y-downy);
					if(tapping)OnTap(x,y);
					if(slided) OnSlideEnd(x,y,x-downx,y-downy);
					down=false;
				}else{
					this.downx=x;
					this.downy=y;
				}
				
				break;
		}
		lastx=x;
		lasty=y;
		lasttime=time;
		
		return true;
	}
	private void OnDown(float x,float y){
		timer.Stop();
	}
	private void OnTap(float x,float y){
		OnClickListener.onClick(this);
	}
	float statespeed;
	OnClickListener OnClickListener;
	
	private void OnSlide(float x,float y,float dx,float dy, float deltax, float deltay,final int dt){
		float windowh=this.getHeight();
		float heightratio=dy/windowh;
		float deltastate=heightratio/imageinterval;
		state+=deltastate;
		state=mod(state,imageids.length);
		statespeed=deltastate/dt*5;
		invalidate();
		
	}

	private void OnUp(float x,float y, float deltax, float deltay){
		
	}
	public  void startanimation(){
		
		this.endstate=Math.round(this.state)+1;
		timer.Do(new Runnable() {
			@Override
			public void run() {
				post(new Runnable() {
					
					@Override
					public void run() {

						state+=0.01f;
						
						if(state>=endstate){
							state=mod(endstate,imageids.length);
							startanimation();
						}
						invalidate();	
					}
				});
				
			}
		}, 5000,10);
	}
	public void pauseanimation(){
		timer.Stop();
		state=Math.round(state);
		invalidate();
	}
	
	float endstate;
	private void OnSlideEnd(float x,float y, float deltax, float deltay){
		
		timer.Do(new Runnable() {
			
			@Override
			public void run() {
				post(new Runnable() {
					
					@Override
					public void run() {

						state+=statespeed;
						float currentimage=Math.round(state);
						float deltastate=state-currentimage;
						statespeed+=-0.0015*deltastate;
						statespeed*=0.95f+0.32f*deltastate*deltastate;
						state=mod(state,imageids.length);
						if(Math.abs(deltastate)+Math.abs(statespeed)<0.0001f){timer.Stop(); startanimation();}
						invalidate();	
					}
				});
			}
		},0,5);
	}
	
	

	

}
