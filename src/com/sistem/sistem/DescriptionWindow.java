package com.sistem.sistem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


public class DescriptionWindow extends RelativeLayout {

	public DescriptionWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}
	public DescriptionWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	public DescriptionWindow(Context context) {
		super(context);
		initialize(context);
	}
	public static int margin=42;
	public static float backalpha=0.5f;
	public static float cornerradius=15f;
	
	private void initialize(final Context context){
		wv=new WebView(context);
		RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		lp1.alignWithParent=true;
		lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp1.setMargins(margin, margin, margin, margin+70);
		wv.setLayoutParams(lp1);
		wv.setBackgroundColor(Color.TRANSPARENT);
		this.addView(wv);
		
		xbutton=new Button(context);
		RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(55,55);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp2.setMargins(0, 15, 15, 0);
		xbutton.setLayoutParams(lp2);
		xbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.xbutton));
		this.addView(xbutton);
		
		detailsbutton=new Button(context);
		RelativeLayout.LayoutParams lp3=new RelativeLayout.LayoutParams(450,80);
		lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lp3.setMargins(0,0,0,30);
		detailsbutton.setLayoutParams(lp3);
		detailsbutton.setTextSize(15);
		detailsbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_1));//TODO: set the details button here
		detailsbutton.setText("詳しく見る →GO!");
		detailsbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,DescriptionActivity.class);
				intent.putExtra("eventid", currentcontentid);
				context.startActivity(intent);
			}
		});
		this.addView(detailsbutton);

		this.lp.gravity=Gravity.LEFT|Gravity.TOP;
		this.anmstate=0;
		updateanm();
		
	}
	public int currentcontentid;
	private RectF rect=new RectF();
	private Paint p=new Paint();
	
	public WebView wv;
	public Button xbutton;
	public Button detailsbutton;
	
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		
		rect.set(0, 0, canvas.getWidth(), canvas.getHeight());
		
		p.setARGB(Math.min(255,(int)(backalpha*256)), 0, 0,0);
		p.setStyle(Style.FILL);		
		canvas.drawRoundRect(rect, cornerradius, cornerradius, p);

		p.setARGB(255,255,64,255);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(2.5f);
		canvas.drawRoundRect(rect, cornerradius, cornerradius, p);
		super.dispatchDraw(canvas);
			
	}
	
	MyTimer timer=new MyTimer(10);
	float anmstate=0;
	float anmv=0;

	Runnable anmtask=new Runnable() {
		@Override
		public void run() {
			anmstate+=anmv;
			anmstate=Util.clamp(anmstate, 0, 1);
			updateanm();
			if(Util.isonedge(anmstate, 0, 1))timer.Stop();
		}
	};
	
	public void open(int contentid){
		currentcontentid=contentid;
		String html;
		do {
		String title="<h2>"+MyApplication.indicium.group_table[contentid].name+"</h2>";
		String description=MyApplication.indicium.group_table[contentid].description.replace("<html>", "").replace("</html>", "");
		html=("<html><font color='#ffffff'>"+title+description+"</font></html>");
		Log.d("verbose", html);
		if(html.contains("%s")) {Log.e("%s error", String.format("%%s appeared; title=%s, desc=%s",title,description));
		continue;
		}
		break;
		}
		while(true);
		
		this.setwebview(html);
		anmv=10f/300f;
	
		timer.Ensure(anmtask);
	}
	public void close(){
		anmv=-10f/300f;
		timer.Ensure(anmtask);
	}
	public void stop(){
		timer.Stop();
	}
	
	void updateanm(){
		this.post(new Runnable() {
			@Override
			public void run() {
				{
					final float w=0.84f;
					final float h=0.64f;
					lp.width=(int)(((View)getParent()).getWidth()*w*anmstate);
					lp.height=(int)(((View)getParent()).getHeight()*h*anmstate);
					lp.leftMargin=(int)(((View)getParent()).getWidth()*((0.5f+w*0.5f)-w*anmstate));
					lp.topMargin=(int)(((View)getParent()).getHeight()*((0.5f+h*0.5f)-h*anmstate));
					setLayoutParams(lp);
					if(anmstate<=0)setVisibility(View.GONE);
					else setVisibility(View.VISIBLE);
					if(anmstate>=1){
						wv.setVisibility(View.VISIBLE);
						xbutton.setVisibility(View.VISIBLE);
						detailsbutton.setVisibility(View.VISIBLE);
						
					}else
					{
						wv.setVisibility(View.GONE);
						xbutton.setVisibility(View.GONE);
						detailsbutton.setVisibility(View.GONE);
					}

					((View)getParent()).invalidate();
				}
			}
		});
	}
	FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(0,0);
	

	@SuppressLint("NewApi")
	public void setbackground(){
		wv.setBackgroundColor(0);
		if (Build.VERSION.SDK_INT >= 11) wv.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}
	void setwebview(String html){
		WebSettings settings = wv.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		setbackground();
	}
}
