package com.sistem.sistem;

import com.sistem.sistem.modelviewer2.MapActivity;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

public class DescriptionActivity extends Activity {

	private WebView wv;
	private MyButton1 button;
	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		wv=(WebView)findViewById(R.id.wv1);
		button=(MyButton1)findViewById(R.id.button1);
		iv=(ImageView)findViewById(R.id.imageview);
		
		Intent intent=getIntent();
		final int eventid=intent.getIntExtra("eventid",-1);
		//setwebview(String.format("<html><h2>%s</h2><p>test description</p><p>場所： %s</p><p>時間： %s</p><p>待ち時間： N/A</p></html>",
		//		MyApplication.indicium.group_table[eventid].name,
		//		Util.join(MyApplication.indicium.group_table[eventid].place,", "),"N/A"));
		//String uriPath = "android.resource://"+getPackageName()+"/drawable/";
		
		anami.Record_group_table activity=MyApplication.indicium.group_table[eventid];
		String description=activity.description.replace("<html>", "").replace("</html>", "");
		String place=(activity.place!=null&&activity.place.length!=0)?String.format("<p>場所: %s</p>\r\n",Util.join(activity.place, ", ")):"";
		String time=(activity.time!=null&&activity.time.length!=0)?String.format("<p>時間: %s</p>\r\n",Util.join(activity.time, ", ")):"";
		
		String html="<html>\r\n" + 
		String.format("<font color='#000000'><h1 style:'text-align:center'>%s</h1>\r\n",activity.name) +
		String.format("<p>%s</p>\r\n",description) +place+time+		"</font></html>";
		iv.setImageResource(R.drawable.poster);
		setwebview(html);
		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(DescriptionActivity.this,MapActivity.class);
				float[] pos=new float[6];
				
				Util.average(MyApplication.indicium.group_table[eventid].place[0],pos,3);
				pos[0]=pos[3]+1000;
				pos[1]=pos[4]+000;
				pos[2]=pos[5]+000;
				intent.putExtra("pos", pos);
				startActivity(intent);
			}
		});
	}
	@SuppressLint("NewApi")
	void setbackground(){
		wv.setBackgroundColor(0xd0ffffff);
		if (Build.VERSION.SDK_INT >= 11) wv.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		
		/*drawable.setBounds(0,0,wv.getWidth(),wv.getHeight());
		drawable.setAlpha(64);
		wv.setBackgroundDrawable(drawable);*/
	}
	void setwebview(String html){
		wv.loadData(html,  "text/html; charset=utf-8", "utf-8");
		setbackground();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.description, menu);
		return true;
	}

}
