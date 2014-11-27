package com.sistem.sistem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.sistem.sistem.R;
import com.sistem.sistem.modelviewer2.MapActivity;

import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.*;

public class HomeActivity extends Activity implements SurfaceHolder.Callback {


	
	/// GLOBAL VARIABLES
	
	public static HomeActivity _default;
	
	public int windowwidth;
	public int windowheight;
	//VideoView vv;
	SurfaceView sv;
	LinearLayout buttonslayout;
	MyButton1[] buttons;
	MyGallery gallery;
	SwipingText newswindow;
	LinearLayout thewholething;
	DescriptionWindow dw;
	FrameLayout root;
	MyButton1 btnskip;
	/*
	String vid_home_fname;
	String vid_festa_home_fname;
	String vid_opening_fname;
	String vid_festa_opening_fname;
	*/

	
	///ACTIVITY EVENTS
	SurfaceHolder holder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		//FOR DEBUG:

		Intent intent = new Intent(HomeActivity.this, TwoDMapActivity.class);
		startActivity(intent);
		//END
		
		
		Log.d("DDDDDDDDD", "#<onCreate");
		_default=this;
		initializeTimeInfo();		
		setScreenSize();
		setContentView(R.layout.activity_home);	
		initializeViews();	
		holder=sv.getHolder();
		holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//newswindow.addView(new View(this));		
		setupAnimations();		
        setDefaultContents();		
		setEventListeners();		
		//skiphomeanimation();
		Log.d("DDDDDDDDD", "#onCreate>");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		Log.d("DDDDDDDDD", "#<surfaceDestroyed");
		stop();
		Log.d("DDDDDDDDD", "#surfaceDestroyed>");
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("DDDDDDDDD", "#<surfaceCreated");
		start();
		Log.d("DDDDDDDDD", "#surfaceCreated>");
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("DDDDDDDDD", "#<surfaceChanged");
		Log.d("DDDDDDDDD", "#surfaceChanged>");
				
	}
	/*
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("DDDDDDDDD", "#<onSaveInstanceState");
		outState.putLong(KEY_LASTACCESS, thisaccesstime.toMillis(false));

		Log.d("DDDDDDDDD", "#onSaveInstanceState>");
	}
	*/
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//gallery.pauseanimation();
		//newswindow.stop();
		
		//vv.stopPlayback();
		Log.d("DDDDDDDDD", "#<onPause");
		stop();
		
		Log.d("DDDDDDDDD", "#onPause>");
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//gallery.startanimation();
		//newswindow.startswipe();
		//vvstartplayback_home();
		//skiphomeanimation();
		//startVideo();
		Log.d("DDDDDDDDD", "#<onResume");

		resetViews();
		
		if(nowplayingmp==null&&sv.getHolder().getSurface().isValid())start();
		else if(nowplayingmp!=null)Log.d("DDDDD", "nowplayingmp!=null");
		else if (!sv.getHolder().getSurface().isValid())Log.d("DDDDD", "surface invalid");
		if(imageids!=null)	Util.ShuffleArray(imageids,contentids);
		gallery.clean();
		gallery.invalidate();
		
		Log.d("DDDDDDDDD", "#onResume>");
	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("DDDDDDDDD", "#<onStart");
		loadTimeInfo();
		//startVideo();
		//skiphomeanimation();
		Log.d("DDDDDDDDD", "#onStart>");
	}
	
	/*
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d("DDDDDDDDD", "#<onRestoreInstanceState");
		loadTimeInfo(savedInstanceState);
		Log.d("DDDDDDDDD", "#onRestoreInstanceState>");
	}*/
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("DDDDDDDDD", "#<onStop");
		this.isfirsttime=false;
		this.showopening=false;
		saveTimeInfo();
		Log.d("DDDDDDDDD", "#onStop>");
	}

	private void stop() {
		Log.d("DDDDDDDDD", "<stop");
		gallery.pauseanimation();
		newswindow.stop();
		fadein.end();
		if(op!=null) {op.release();op=null;}
		if(home_s!=null) {home_s.release();home_s=null;}
		if(home_l!=null) {home_l.release();home_l=null;}
		if(home_e!=null) {home_e.release();home_e=null;}
		if(home_sound!=null) {home_sound.release();home_sound=null;}
		nowplayingmp=null;
		//this.dw.wv.destroy();
		Log.d("DDDDDDDDD", "stop>");
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);

		return true;
	}
	
	
	

	///VIEWS
	
	private void initializeViews() {
		Log.d("DDDDDDDDD", "<initializeViews");
		thewholething = (LinearLayout)this.findViewById(R.id.theWholeThing);
		//vv = (VideoView)this.findViewById(R.id.VideoView);
		sv=(SurfaceView)this.findViewById(R.id.BackVideo);
		//news=(TextView)this.findViewById(R.id.news);
		buttonslayout=(LinearLayout)this.findViewById(R.id.Buttons);
		buttons=new MyButton1[]{(MyButton1)this.findViewById(R.id.btn1),
				(MyButton1)this.findViewById(R.id.btn2),
				(MyButton1)this.findViewById(R.id.btn3),
				(MyButton1)this.findViewById(R.id.btn4),
				(MyButton1)this.findViewById(R.id.btn5),
				(MyButton1)this.findViewById(R.id.btn6)};
		btnskip=(MyButton1)this.findViewById(R.id.btnskip);
		/*buttons[0].setIcon(getResources().getDrawable(R.drawable.ic_action_search));
		buttons[1].setIcon(getResources().getDrawable(R.drawable.ic_action_search));
		buttons[2].setIcon(getResources().getDrawable(R.drawable.ic_action_search));
		buttons[3].setIcon(getResources().getDrawable(R.drawable.ic_action_search));
		buttons[4].setIcon(getResources().getDrawable(R.drawable.ic_action_search));
		buttons[5].setIcon(getResources().getDrawable(R.drawable.ic_action_search));*/
		buttonslayout.setClipChildren(false);
		gallery=(MyGallery)this.findViewById(R.id.gallery);
		newswindow=(SwipingText)this.findViewById(R.id.newswindow);
		dw=(DescriptionWindow)this.findViewById(R.id.descriptionWindow1);
		
		root=(FrameLayout)this.findViewById(R.id.root);
		resetViews();
		Log.d("DDDDDDDDD", "initializeViews>");
	}
	AnimatorSet prefadein;	
	private void resetViews(){

		Log.d("DDDDDDDDD", "<resetViews");
		
			if(prefadein==null){
				prefadein=new AnimatorSet();
				ArrayList<Animator> animators=new ArrayList<Animator>();
				animators.add(ObjectAnimator.ofFloat(newswindow, "alpha", 0f).setDuration(0));
				for(int i=0;i<buttons.length;i++)
					animators.add(ObjectAnimator.ofFloat(buttons[i], "alpha", 0f).setDuration(0));
		
				animators.add(ObjectAnimator.ofFloat(gallery, "alpha", 0f).setDuration(0));
				prefadein.playTogether(animators);
				newswindow.setVisibility(View.VISIBLE);
			}
			
			prefadein.start();
			Log.d("DDDDDDDDD", "resetViews>");
		}
	@SuppressWarnings("deprecation")
	private void setScreenSize() {
		Log.d("DDDDDDDDD", "<setScreenSize");
			Display display = getWindowManager().getDefaultDisplay();
			this.windowwidth=display.getWidth();
			this.windowheight=display.getHeight();
			Log.d("DDDDDDDDD", "setScreenSize>");

		}
		
	///CONTENTS
	int[] contentids,imageids;
	private void setDefaultContents() {
		Log.d("DDDDDDDDD", "<setDefaultContents");
		final ViewTreeObserver vto = gallery.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
			@Override
            public void onGlobalLayout() {
        		Log.d("DDDDDDDDD", "#<onGlobalLayout");
            	if(imageids==null||imageids.length==0){
	        		imageids=new int[]{R.drawable.ba,R.drawable.bb,R.drawable.bc,R.drawable.bd,R.drawable.be,R.drawable.bf,R.drawable.bg,R.drawable.bh,R.drawable.bi,R.drawable.bj,R.drawable.bk,R.drawable.bl,R.drawable.bm,R.drawable.bn,R.drawable.bo,R.drawable.bp,R.drawable.bq,R.drawable.br,R.drawable.bs,R.drawable.bt,R.drawable.bu,R.drawable.bv,R.drawable.bw,R.drawable.bx,R.drawable.by,R.drawable.bz,R.drawable.baa,R.drawable.bab,R.drawable.bac,R.drawable.bad,R.drawable.bae,R.drawable.baf,R.drawable.bag,R.drawable.bah,R.drawable.bai,R.drawable.baj,R.drawable.bak,R.drawable.bal,R.drawable.bam,R.drawable.ban,R.drawable.bao,R.drawable.bap};
	        		String[] eventnames=new String[] {"美術部","ぶいえいす","文映会","物理科学部","Mr. Chicken","地理歴史巡検部","地学天文学部","横浜ばな奈","コンピューター部","クレープハウス1.2.3","デュエルアカデミア","Drink・Bar","英語道楽倶楽部","英語劇・教室劇","フランクちゃんとフルトくん","NTGz(がんけん)","将棋の王将","早撃ちジョニー","７５枚目のジョーカー","俺のケバブ","高三英語劇","交通研究部","氷菓","リブラDX","横濱萬才フィルム","魔女の卓球便","麺の達人","題名のない音楽喫茶","Pokemon Center Seiko","The Quizbler","生物部","聖書研究会","焼きそば　麺`s RUNRUN","助っ人バスケット","数学研究会","俊造チャレンジ","鉄板職人","SVC","TrickXHark","宇宙戦艦ヤマト研究会","グランドフィナーレ","恋愛偏差値"};
	        		contentids=new int[eventnames.length];
	        		for (int i = 0; i < contentids.length; i++) {
	        			for (int j = 0; j < MyApplication.indicium.group_table.length; j++)
	        				if(Util.normalize(eventnames[i]).equalsIgnoreCase(Util.normalize(MyApplication.indicium.group_table[j].name)))
	        				{contentids[i]=j;break;}
					}
	        		
	        		
	        		Util.ShuffleArray(imageids,contentids);
	        		gallery.setImages(getResources(), imageids);
	        		gallery.postInvalidate();
            	}
            	if(vto.isAlive()) {
            		vto.removeGlobalOnLayoutListener(this);
            	}
            	else {
            		gallery.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            	}
        		Log.d("DDDDDDDDD", "#onGlobalLayout>");
            }
        });
		
		newswindow.setText("4月26日　9:00　第55回聖光学院文化祭開催しました！！");

		Log.d("DDDDDDDDD", "setDefaultContents>");
	}


	
	///EVENT LISTENERS
	private void setEventListeners() {
		Log.d("DDDDDDDDD", "<setEventListeners");
			gallery.OnClickListener=new OnClickListener() {
				//this.lp
				@Override
				public void onClick(View v) {
					Log.d("DDDDDDDDD", "@<gallery.onClick");
						
					    dw.setwebview("<html><font color='white'>%s</font></html>");
					    gallery.pauseanimation();
					    dw.open(contentids[(int)Math.round(MyGallery.mod(gallery.state,gallery.imageids.length))]);
					
						Log.d("DDDDDDDDD", "@gallery.onClick>");
					}
			};
			dw.xbutton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					Log.d("DDDDDDDDD", "@<dw.xbutton.onClick");
					dw.close();
					gallery.startanimation();

					Log.d("DDDDDDDDD", "@dw.xbutton.onClick>");
				}
			});
			buttons[4].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri uri = Uri.parse( "http://seikofesta.official.jp/2014/index.html" );
					startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
				}
			});
			buttons[3].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, ThumbnailListView.class);
					startActivity(intent);
				}
			});

			buttons[1].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, EventsActivity.class);
					startActivity( new Intent( intent) );
				}
			});
			buttons[0].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO:MAP
					Intent intent = new Intent(HomeActivity.this, TwoDMapActivity.class);
					startActivity(intent);
				}
			});
			buttons[2].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO:QR Reader
					Intent intent = new Intent(HomeActivity.this, QRActivity.class);
					startActivity(intent);
				}
			});
			buttons[5].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO:ABOUT
					Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
					startActivity(intent);
				}
			});
			btnskip.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(nowplayingmp==op)oncomplete.onCompletion(op);
				}
			});
			Log.d("DDDDDDDDD", "setEventListeners>");
		}
		
	
	///ANIMATIONS & VIDEO
	AnimatorSet fadein=new AnimatorSet();
	private void setupAnimations() {
		Log.d("DDDDDDDDD", "<setupAnimations");
		ArrayList<Animator> animators=new ArrayList<Animator>();
		
		ObjectAnimator anm;

		animators.add(ObjectAnimator.ofFloat(btnskip, "alpha", 1,0).setDuration(300));
		anm=ObjectAnimator.ofFloat(newswindow, "alpha", 0,1).setDuration(1000);
		anm.setStartDelay(1000);
		animators.add(anm);
		/*
		anm=ObjectAnimator.ofInt(newswindow, "backgroundColor", 0x00ffffff,0x80ffffff).setDuration(1000);
		anm.setStartDelay(1000);
		animators.add(anm);*/
		for(int i=0;i<buttons.length;i++){
			int anmdelay=300+i*250;
			anm=ObjectAnimator.ofFloat(buttons[i], "translationX", windowwidth,0).setDuration(1000);
			anm.setStartDelay(anmdelay);
			animators.add(anm);
			anm=ObjectAnimator.ofFloat(buttons[i], "alpha", 0,1).setDuration(1000);
			anm.setStartDelay(anmdelay);
			animators.add(anm);
			
		}
		anm=ObjectAnimator.ofFloat(gallery, "alpha", 0,1).setDuration(1000);
		anm.setStartDelay(300+buttons.length*250);
		animators.add(anm);
		fadein.playTogether(animators);
		Log.d("DDDDDDDDD", "setupAnimations>");
	}

	private void starthomeanimation(){

		Log.d("DDDDDDDDD", "<starthomeanimation");
		newswindow.startswipe();
		gallery.start();
		fadein.start();
		//skiphomeanimation();
		Log.d("DDDDDDDDD", "starthomeanimation>");
	}
	private void skiphomeanimation(){

		Log.d("DDDDDDDDD", "<skiphomeanimation");
		gallery.start();
		newswindow.startswipe();
		root.post(new Runnable() {
			@Override
			public void run() {
				fadein.start();
				fadein.end();
			}
		});
		Log.d("DDDDDDDDD", "skiphomeanimation>");
	}
	private void stophomeanimation() {

		Log.d("DDDDDDDDD", "<stophomeanimation");
		gallery.pauseanimation();
		newswindow.stop();
		Log.d("DDDDDDDDD", "stophomeanimation>");
	}
	

	MediaPlayer nowplayingmp;
	MediaPlayer op, home_s,home_l,home_e, home_sound;
	OnCompletionListener oncomplete=new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.d("DDDDDDDDD", "@<op.onCompletion");
			op.release();
			op=null;
			
			vvstartplayback_home();
			root.post(new Runnable() {
				
				@Override
				public void run() {

					Log.d("DDDDDDDDD", "<op.onCompletion.post");
					
					starthomeanimation();
					Log.d("DDDDDDDDD", "op.onCompletion.post>");
				}
			});
			Log.d("DDDDDDDDD", "@op.onCompletion>");
		}
	};
	
	private void start() {
		Log.d("DDDDDDDDD", "<start");
		if(!sv.getHolder().getSurface().isValid()) {
			skiphomeanimation();
		}
		else {
			
			if(showopening){
				es.execute(new Runnable() {
					
					@Override
					public void run() {

						if(op==null)op=MediaPlayer.create(getApplicationContext(), isduringfesta?R.raw.vid_festa_opening:R.raw.vid_opening);
						if(op==null)
							Log.d("MEDIAINFO", "Video Not Loaded");
						nowplayingmp=op;
						root.post(new Runnable() {
							
							@Override
							public void run() {
								videofittosize(op);
							}
						});
						op.start();
						op.setDisplay(holder);		
						loadvideos();
						op.setOnCompletionListener(oncomplete);
					}
				});
	
			}
			else{
				es.execute(new Runnable() {
					public void run() {

						Log.d("DDDDDDDDD", "%<execute 453");
						loadvideos();
						vvstartplayback_home();
						Log.d("DDDDDDDDD", "%execute 453>");
					}
				});
				skiphomeanimation();
			}
		}
		Log.d("DDDDDDDDD", "start>");
	}
	private void loadvideos() {
		if(home_s==null)
			home_s=MediaPlayer.create(getApplicationContext(), R.raw.vid_home_s);
		if(home_l==null)
			home_l=MediaPlayer.create(getApplicationContext(), R.raw.vid_home_l);
		if(home_e==null)
			home_e=MediaPlayer.create(getApplicationContext(), R.raw.vid_home_e);
		if(home_sound==null)
			home_sound=MediaPlayer.create(getApplicationContext(), R.raw.snd_home);
		if(home_s==null||home_l==null||home_e==null) {
			Log.d("MEDIAINFO", "Video Not Loaded");
			
		}
	}
	
	ExecutorService es=Executors.newSingleThreadExecutor();
	
	/*
	private void ensureSurfaceCreated(final Runnable action) {
		if(holder.getSurface().isValid()) {
			action.run();
		}
		else {
			holder.addCallback(new Callback() {
				
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					// TODO Auto-generated method stub
					action.run();
				}
				
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width,
						int height) {
					// TODO Auto-generated method stub

					action.run();
				}
			});
		}
	}
/*
	private void setVideoPaths() {
		vid_home_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_home;
		vid_festa_home_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_festa_home;
		vid_opening_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_opening;
		vid_festa_opening_fname = "android.resource://" + getPackageName() + "/" + R.raw.vid_festa_opening;
		
	}*/
	void vvstartplayback_home(){

		Log.d("DDDDDDDDD", "<vvstartplayback_home");
		nowplayingmp=home_s;
		final MediaPlayer mp=home_s;
		root.post(new Runnable() {
			
			@Override
			public void run() {
				videofittosize(home_s);
			}
		});
		if(mp!=null) {
			mp.start();
			mp.setDisplay(holder);
			home_sound.start();
			home_sound.setLooping(true);
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.d("DDDDDDDDD", "@<homes.onCompletion");
					es.execute(new Runnable() {
						public void run() {
							nowplayingmp=home_l;
							home_s.release();
							home_s=null;
							if(home_l!=null) {
								home_l.setLooping(true);
								home_l.start();
								home_l.setDisplay(holder);
							}
						}
					});
					Log.d("DDDDDDDDD", "@homes.onCompletion>");
					
				}
			});
		}
		Log.d("DDDDDDDDD", "vvstartplayback_home>");
	}
	private void videofittosize(MediaPlayer mp){
		
		Log.d("DDDDDDDDD", "<videofittosize");
		if(mp==null)return;
		int w=mp.getVideoWidth();//BUG when resuming
		int h=mp.getVideoHeight();
		//float ar=(float)w/h;
		int ww=root.getWidth();
		int wh=root.getHeight();
		float scale=Math.max((float)ww/w,(float)wh/h);
		if(Float.isInfinite(scale)||Float.isNaN(scale)) {
			Log.d("SVINFO", String.format("scale went into NaN or Infinity. vw=%d, vh=%d, ww=%d, wh=%d", w,h,ww,wh));
			scale=1;			
		}
		int aw=(int)(w*scale);
		int ah=(int)(h*scale);
		int ax=(ww-aw)/2;
		int ay=(wh-ah)/2;
		
		
		FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(aw, ah, Gravity.CENTER);
		lp.leftMargin=ax;
		lp.rightMargin=ax;
		lp.topMargin=ay;
		lp.bottomMargin=ay;
		Log.d("SVINFO", String.format("x=%d y=%d w=%d h=%d", ax,ay,aw,ah));
		sv.setLayoutParams(lp);
		
		Log.d("DDDDDDDDD", "videofittosize>");
	}
	
	
	
	///TIME INFO
	static final String KEY_LASTACCESS="LASTACCESS";
	static final Time TIME_FESTA_BEGINS=getfestatimebegin();
	static final Time TIME_FESTA_ENDS=getfestatimeend();
	private static Time getfestatimebegin(){Time t=new Time();t.set(0, 0, 9, 26, 4, 2014); return t;}
	private static Time getfestatimeend(){Time t=new Time();t.set(0, 0, 18, 27, 4, 2014); return t;}
	boolean isduringfesta;
	boolean isfirsttime;
	boolean showopening;
	Time thisaccesstime=new Time();
	Time lastaccesstime=new Time();
	
	private void initializeTimeInfo() {
		Log.d("DDDDDDDDD", "<initializeTimeInfo");
			thisaccesstime.setToNow();
			isduringfesta=Time.compare(thisaccesstime, TIME_FESTA_BEGINS)>0&&Time.compare(thisaccesstime, TIME_FESTA_ENDS)<0;
			isfirsttime=true;
			showopening=true;
			Log.d("DDDDDDDDD", "initializeTimeInfo>");
		}
	private void saveTimeInfo() {

		FileOutputStream fos;
		try {
			fos = openFileOutput(KEY_LASTACCESS, Context.MODE_PRIVATE);
			fos.write(ByteBuffer.allocate(8).putLong(thisaccesstime.toMillis(false)).array());
			fos.close();
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	private void loadTimeInfo() {
		Log.d("DDDDDDDDD", "<loadTimeInfo");
		File file = getBaseContext().getFileStreamPath(KEY_LASTACCESS);
		boolean fileexists=file.exists();
		if(!fileexists){
			isfirsttime=true;
		}
		else{
			isfirsttime=!fileexists;
		}
		if(!isfirsttime){

			long millisecs;
			try {

				FileInputStream fis;
				fis = openFileInput(KEY_LASTACCESS);
				ByteBuffer bb=ByteBuffer.allocate(8);
				fis.read(bb.array());// .putLong(thisaccesstime.toMillis(false)).array());
				millisecs=bb.asLongBuffer().get();
				lastaccesstime.set(millisecs);
				if(lastaccesstime.hour!=thisaccesstime.hour)showopening=true;else showopening=false;
				fis.close();
				//FOR SHOW
				showopening=false;
			} catch (FileNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		else {showopening=true;}//isfirsttimeduringfestaofday=isduringfesta;}

		Log.d("DDDDDDDDD", "loadTimeInfo>");
	}
		
	
	
	

}
