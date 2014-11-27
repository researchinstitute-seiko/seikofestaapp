package com.sistem.sistem.modelviewer2;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sistem.sistem.R;
import com.sistem.sistem.modelviewer2.Framework3D.*;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.RotateAroundUserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.TwoDUserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.UserInput;

public class MapActivity extends Activity {

	/** Hold a reference to our GLSurfaceView */
	private FrameworkSurfaceView mGLSurfaceView;
	private Framework3D framework;
int mode=0;
	UserInput ui_Lookover=new RotateAroundUserInput(framework, /*angradperpixel*/ Math.toRadians(0.25),
			Math.toRadians(0),Math.toRadians(70),150000f,150000f);
	/*
	UserInput ui_2D=new TwoDUserInput(framework,-155000f,67000f,-57560f,102400f,
			Math.toRadians(45),//angle_x
			Math.toRadians(30),//angle_y
			8f,//scale_y
			0.001f
			);
			*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		App.setContext(this);
		
		TestClass.testentrypoint();
		
		setContentView(R.layout.lesson_seven);
		mGLSurfaceView = (FrameworkSurfaceView) findViewById(R.id.gl_surface_view);
		Button button = (Button) findViewById(R.id.modebutton);
		
		
		try {
			framework=new Framework3D(this,mGLSurfaceView,new float[]{0.1f,0.5f,0.7f,1f}, /*culling*/false);
			framework.Prepare(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					float[] modelmatrix=new float[16];
					Matrix.setIdentityM(modelmatrix, 0);
					
					try {
						Intent intent=getIntent();
						Bundle extras=intent.getExtras();
						
						View view;
						if(extras==null) {
							view=new View(framework,0,7000,-150000,-40000,0,30000,0,1f,0,Math.toRadians(45),2500,200000,1f);
							
							}
						else {
							float[] pos=extras.getFloatArray("pos");
							view=new View(framework,pos[0],pos[1],pos[2],pos[3],pos[4],pos[5],0,1f,0,Math.toRadians(90),2500,200000,1f);
						}
							
						
						
						framework.set(
								
								new Layer[]{

										new ModelLayer(framework,R.raw.model_sistem,
												new StringMap(new String[]{"カシ淡色.jpg","クルミ alpha.jpg","タイル壁 alpha.jpg","マツ alpha.jpg","リノリウム灰.jpg","レンガ長手積み alpha.jpg","ログ1.jpg","鉛.jpg","屋根.jpg","屋根板アスファルト alpha.jpg","化粧しっくい.jpg","寄木張り2.jpg","古レンガ1.jpg","芝生茶 alpha.jpg","芝生緑 alpha.jpg","粗下塗り白.jpg","打ち放し.jpg","土 alpha.jpg","舗道レンガ5.jpg","木材3.jpg","屋根1.jpg"},
														new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p,R.drawable.q,R.drawable.r,R.drawable.s,R.drawable.t,R.drawable.i}),
														modelmatrix, true)
										,
										new AndronLayer(framework,modelmatrix, false),
										}, 
												view,
												new Light(100000f,200000f,300000f));
											

					}
			
		
					 catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			});
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(android.view.View arg0) {
				mode^=1;
				for (int i = 0; i < framework.layers.length; i++) {
					framework.layers[i].isvisible=i==mode;
				}
				framework.requestRender();
			}
        });
		

	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK)this.finish();
		return mGLSurfaceView.keyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		mGLSurfaceView.onPause();
	}

}