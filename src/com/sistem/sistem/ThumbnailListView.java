package com.sistem.sistem;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailListView extends ListActivity{

String[] videoFileList = {
  "http://seikofesta.official.jp/2014/img/OpeningFinal_R.mp4",
  "http://seikofesta.official.jp/2014/img/ADVETRTISEMENTGAME .mp4",
  "http://seikofesta.official.jp/2014/img/E-1GP.mp4",
  "http://seikofesta.official.jp/2014/img/intro.mp4",
  "http://seikofesta.official.jp/2014/img/komyulyoku.mp4",
  "http://seikofesta.official.jp/2014/img/MrSeiko.mp4",
  "http://seikofesta.official.jp/2014/img/nodo.mp4",
  "http://seikofesta.official.jp/2014/img/pic.mp4",
  "http://seikofesta.official.jp/2014/img/quiz.mp4",
  "http://seikofesta.official.jp/2014/img/sokkuri.mp4",
  "http://seikofesta.official.jp/2014/img/syoudou.mp4",
  "http://seikofesta.official.jp/2014/img/zuttomo.mp4",
  "http://seikofesta.official.jp/2014/img/ITPRMovie.mp4",
  "http://seikofesta.official.jp/2014/img/SeikoFesta%20PromotionVideoR.mp4",
};
String[] videoDescriptionList=
{
		"聖光祭　オープニング",
		"企画 \"Advertisement Game\" 26日 11:00~, 11:50~, 14:30~  27日 10:00~, 11:10~  @外ステ",
		"企画 \"E-1 GP\" 26日 14:30~15:00  27日 12:50~13:20  @外ステージ",
		"企画 \"イントロクイズ\" 26日 13:00~13:30  27日 13:20~13:50  @外ステージ",
		"企画 \"コミュ力の窓\" 26日 10:30~11:00  27日 12:20~12:50  @外ステージ",
		"企画 \"Mr.聖光\" 26日 15:10~15:50  27日 14:20~15:00  @外ステージ",
		"企画 \"のど自慢\" 26日 13:30~14:30  27日 14:50~15:20  @外ステージ",
		"企画 \"絵企画\" 26日 12:30~13:00  27日 10:40~11:10  @外ステージ",
		"企画 \"聖光生クイズ\" 27日 11:20~  @外ステージ",
		"企画 \"そっくりさん選手権\" 26日 11:40~12:00  27日 10:10~11:40  @外ステージ",
		"企画 \"笑動ステーション\" 26日 11:10~11:40  27日 11:50~12:20  @外ステージ",
		"企画 \"ズットモグランプリ\" 26日 12:00~12:30  @外ステージ",
		"聖光祭2014 総合技術研究所PR動画",
		"聖光祭2014 スローガン発表動画",
};

public class MyThumbnaildapter extends ArrayAdapter<String>{

 public MyThumbnaildapter(Context context, int textViewResourceId,
   String[] objects) {
  super(context, textViewResourceId, objects);
  // TODO Auto-generated constructor stub
  thumbnails=new Bitmap[objects.length];
 }
 Bitmap[] thumbnails;
 ExecutorService exs=Executors.newFixedThreadPool(4);
 @Override
 public View getView(final int position, View convertView, ViewGroup parent) {
  // TODO Auto-generated method stub
 
  View row = convertView;
  if(row==null){
   LayoutInflater inflater=getLayoutInflater();
   row=inflater.inflate(R.layout.thumbnail_entry, parent, false);
  }
 
  TextView textfilePath = (TextView)row.findViewById(R.id.filePath);
  textfilePath.setText(videoDescriptionList[position]);
  final ImageView imageThumbnail = (ImageView)row.findViewById(R.id.thumbnail);
  if(thumbnails[position]!=null) {
	  imageThumbnail.setImageBitmap(thumbnails[position]);
  }
  else {
	  imageThumbnail.setImageBitmap(null);
  exs.execute(new Runnable() {

	@Override
	public void run() {
		
		  final Bitmap bmThumbnail;
	        thumbnails[position]=bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFileList[position], Thumbnails.MICRO_KIND);
	        imageThumbnail.post(new Runnable() {
				
				@Override
				public void run() {
			        imageThumbnail.setImageBitmap(bmThumbnail);
				}
			});
	}
	
});
  }
        
 row.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		Intent intent=new Intent(ThumbnailListView.this, PlayVideoActivity.class);
		intent.putExtra("Url", Uri.parse(videoFileList[position]));
		startActivity(intent);
	}
});
  return row;
 }


}

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setListAdapter(new MyThumbnaildapter(ThumbnailListView.this, R.layout.thumbnail_entry, videoFileList));
      
  }
}