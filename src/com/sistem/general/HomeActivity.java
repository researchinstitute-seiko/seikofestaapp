package com.sistem.general;

import java.io.IOException;
import java.util.*;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.sistem.sistem.R;
import com.sistem.sistem.R.id;
import com.sistem.sistem.R.layout;
import com.sistem.sistem.R.menu;
import com.sistem.sistem.R.raw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.Time;
import android.view.Display;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.nineoldandroids.animation.*;

public class HomeActivity extends Activity {

	static final String KEY_LASTACCESS="LASTACCESS";
	static final Time TIME_FESTA_BEGINS=getfestatimebegin();
	static final Time TIME_FESTA_ENDS=getfestatimeend();
	private static Time getfestatimebegin(){Time t=new Time();t.set(0, 0, 9, 1, 5, 2014); return t;}//Needs replacement
	private static Time getfestatimeend(){Time t=new Time();t.set(0, 0, 18, 2, 5, 2014); return t;}//Needs replacement
	boolean isduringfesta;
	boolean isfirsttime;
	boolean isfirsttimeofday;
	//boolean isfirsttimeduringfestaofday;
	Time thisaccesstime=new Time();
	Time lastaccesstime=new Time();
	
	int windowwidth;
	int windowheight;
	SurfaceView vv;
	MediaPlayer mp=new MediaPlayer();
	TextView news;
	LinearLayout buttonslayout;
	Button[] buttons;
	MyGallery gallery;
	FrameLayout newswindow;
	AnimatorSet prefadein=new AnimatorSet();
	AnimatorSet fadein=new AnimatorSet();
	LinearLayout thewholething;

	String vid_home_fname;
	String vid_festa_home_fname;
	String vid_opening_fname;
	String vid_festa_opening_fname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Display display = getWindowManager().getDefaultDisplay();
		this.windowwidth=display.getWidth();
		this.windowheight=display.getHeight();
		
		vid_home_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_home;
		vid_festa_home_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_festa_home;
		vid_opening_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_opening;
		vid_festa_opening_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_festa_opening;
		

		thewholething = (LinearLayout)this.findViewById(R.id.theWholeThing);
		vv = (SurfaceView)this.findViewById(R.id.VideoView);
		mp.setDisplay(vv.getHolder());
		
		news=(TextView)this.findViewById(R.id.news);
		buttonslayout=(LinearLayout)this.findViewById(R.id.Buttons);
		buttons=new Button[]{(Button)this.findViewById(R.id.btn1),
				(Button)this.findViewById(R.id.btn2),
				(Button)this.findViewById(R.id.btn3),
				(Button)this.findViewById(R.id.btn4),
				(Button)this.findViewById(R.id.btn5),
				(Button)this.findViewById(R.id.btn6)};
		buttonslayout.setClipChildren(false);
		gallery=(MyGallery)this.findViewById(R.id.gallery);
		newswindow=(FrameLayout)this.findViewById(R.id.newswindow);
		
		
		
		
		
		
		//ANIMATIONS
		newswindow.addView(new View(this));
		
		ArrayList<Animator> animators=new ArrayList<Animator>();

		/*
		animators.add(ObjectAnimator.ofFloat(newswindow, "translationX", windowwidth,windowwidth).setDuration(1));
		for(int i=0;i<buttons.length;i++)
			animators.add(ObjectAnimator.ofFloat(buttons[i], "translationX", windowwidth,windowwidth).setDuration(1));
		prefadein.playTogether(animators);
		prefadein.start();
		
		/*/
		//newswindow.setVisibility(View.INVISIBLE);
		
		animators.add(ObjectAnimator.ofFloat(newswindow, "alpha", 0f).setDuration(0));
		for(int i=0;i<buttons.length;i++)
			animators.add(ObjectAnimator.ofFloat(buttons[i], "alpha", 0f).setDuration(0));

		animators.add(ObjectAnimator.ofFloat(gallery, "alpha", 0f).setDuration(0));
		prefadein.playTogether(animators);

		//*/
		animators=new ArrayList<Animator>();
		animators.add(ObjectAnimator.ofFloat(newswindow, "translationX", 0,300).setDuration(1));
		ObjectAnimator anm=ObjectAnimator.ofFloat(newswindow, "translationX", 300,0).setDuration(500);
		anm.setStartDelay(1);
		animators.add(anm);
		
		animators.add(ObjectAnimator.ofFloat(newswindow, "alpha", 0f,1).setDuration(500));
		for(int i=0;i<buttons.length;i++){
			int anmdelay=300+i*250;
			anm=ObjectAnimator.ofFloat(buttons[i], "translationX", windowwidth,0).setDuration(500);
			
			anm.setStartDelay(anmdelay);
			animators.add(anm);
			anm=ObjectAnimator.ofFloat(buttons[i], "alpha", 0,1).setDuration(500);
			anm.setStartDelay(anmdelay);
			animators.add(anm);
			
		}
		anm=ObjectAnimator.ofFloat(gallery, "alpha", 0,1).setDuration(1000);
		anm.setStartDelay(300+buttons.length*250);
		animators.add(anm);
		fadein.playTogether(animators);
		//fadein.start();
		
		

		//CONTENTS
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.item1);
		gallery.images.add(b);
		news.setText("NEWS HOT!!");
		

	}
	public void starthomeanimation(){
		skiphomeanimation();
	}
	public void skiphomeanimation(){
		
		
		
		fadein.start();
		fadein.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				thewholething.postInvalidate();
				news.postInvalidate();
				newswindow.postInvalidate();
				
				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_LASTACCESS, thisaccesstime.toMillis(false));	
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();


		prefadein.start();
		
		//MEDIA

		try {
		if(isfirsttimeofday){
				
					mp.setDataSource(isduringfesta?vid_festa_opening_fname:vid_opening_fname);
					mp.prepare();
				/*vv.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
					}
				});**/
				
				mp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						try {
							mp.setDataSource(vid_home_fname);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mp.setLooping(true);
						mp.prepareAsync();
						/*
						vv.setOnPreparedListener(new OnPreparedListener() {
							
							@Override
							public void onPrepared(MediaPlayer mp) {
								mp.setLooping(true);
								
							}
						});*/
						mp.start();
						starthomeanimation();
					}
				});
	
			
			}
			else{
	
					mp.setDataSource(isduringfesta?vid_festa_home_fname:vid_home_fname);
				
				mp.setLooping(true);
				/*
				vv.setOnPreparedListener(new OnPreparedListener() {
				    @Override
				    public void onPrepared(MediaPlayer mp) {
				    	mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
				        mp.setLooping(true);
				    }
				});*/
				mp.prepare();
				
				mp.start();
				skiphomeanimation();
			}
		mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//TIME 
				thisaccesstime.setToNow();
				isduringfesta=Time.compare(thisaccesstime, TIME_FESTA_BEGINS)>0&&Time.compare(thisaccesstime, TIME_FESTA_ENDS)<0;
				if(savedInstanceState==null){
					isfirsttime=true;
				}
				else{
					isfirsttime=!savedInstanceState.containsKey(KEY_LASTACCESS);
				}
				if(!isfirsttime){
					
					lastaccesstime.set(savedInstanceState.getLong(KEY_LASTACCESS));
					if(lastaccesstime.yearDay!=thisaccesstime.yearDay)isfirsttimeofday=true;
					//if(isduringfesta&&isfirsttimeofday)isfirsttimeduringfestaofday=true;
				}
				else {isfirsttimeofday=true;}//isfirsttimeduringfestaofday=isduringfesta;}
				
	}
}
