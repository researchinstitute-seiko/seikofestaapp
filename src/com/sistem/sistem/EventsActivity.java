package com.sistem.sistem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.sistem.sistem.MultiSpinner.MultiSpinnerListener;
import com.sistem.sistem.anami.Indicium;
import com.sistem.sistem.anami.Record_group_table;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;

public class EventsActivity extends Activity{

	public class MyEventsAdapter extends ArrayAdapter<Object>{

		public MyEventsAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hitcount;
		}

		int[] strsearchhits=new int[256];
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View row = convertView;
			if(row==null){
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.event_entry, parent, false);
			}
			else {
				//((Bitmap)((ImageView)row.findViewById(R.id.iv)).getTag(1)).recycle();
			}

			TextView tv1 = (TextView)row.findViewById(R.id.tv1);
			TextView tv2 = (TextView)row.findViewById(R.id.tv2);
			TextView tvtype=(TextView)row.findViewById(R.id.tv_type);
			//textfilePath.setText(videoDescriptionList[position]);
			ImageView iv = (ImageView)row.findViewById(R.id.iv);
			TextView tv3=(TextView)row.findViewById(R.id.tv3);
			int highlight=sortby==1?hits[position]>>16:-1;
			final int idx=hits[position]&0xffff;
			anami.Record_group_table activity= MyApplication.indicium.group_table[idx];
			Bitmap bmThumbnail=activity.icon.get();//.get(new Options(),100);//
			if(bmThumbnail==null)bmThumbnail=Util.getScaledBitmap(R.drawable.noimg,getResources(),100);
			//bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFileList[position], Thumbnails.MICRO_KIND);
			iv.setImageBitmap(bmThumbnail);
			//iv.setTag(1, bmThumbnail);
			
			tv1.setTextColor(0xffffffff);
			//tv1.setText(activity.name);
			String title=activity.name;
			Spannable spannabletitle=new SpannableString(title);
			if(filterkeywords!=null) {
				int ofs=0;
				for (String keyword : filterkeywords) {
					int startofs=ofs;
					ofs=findall(title, keyword, strsearchhits, ofs,true);
					for (int i = startofs; i < ofs; i++) {
						spannabletitle.setSpan(new StyleSpan(Typeface.BOLD), strsearchhits[i], strsearchhits[i]+keyword.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					}
				}
			}
			tv1.setText(spannabletitle);
			
			int type=activity.type;
			tvtype.setText(anami.type.names[type]);
			tvtype.setBackgroundColor(MyApplication.colortable[type]);
			tvtype.setTextColor(Color.BLACK);

			CharSequence placetext=activity.place==null||activity.place.length==0?null:"場所: "+ Util.join(activity.place, ", ");
			CharSequence timetext=activity.time==null||activity.time.length==0?null:"時間: "+ Util.join(activity.time, ", ",highlight);
			CharSequence wholetext=placetext==null?timetext:timetext==null?placetext:TextUtils.concat(placetext,"  ",timetext);
			if(wholetext==null)tv3.setVisibility(View.GONE); else tv3.setVisibility(View.VISIBLE);
			
			tv3.setText(wholetext);
			
			if(activity.description==null) {
				tv2.setText("");
				tv2.setVisibility(View.GONE);
			}
			else {
				String text=Util.getplaintext(activity.description);//activity.description.replace("\r\n",	" ").replace("\n", " ").replace("\r", " "));
				SpannableString spannabletext=new SpannableString(text);
				CharSequence display=text;
				if(filterkeywords!=null) {
					int ofs=0;
					for (String keyword : filterkeywords) {
						int startofs=ofs;
						ofs=findall(text, keyword, strsearchhits, ofs,true);
						for (int i = startofs; i < ofs; i++) {
							spannabletext.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), strsearchhits[i], strsearchhits[i]+keyword.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					}
					final int CAPACITY=30;
					if(text.length()>CAPACITY) {
					Arrays.sort(strsearchhits,0,ofs);
					int i=Math.min(lookforbestpreview(strsearchhits, ofs, CAPACITY),text.length()-CAPACITY);
					if(i!=0)display=TextUtils.concat("...",(spannabletext.subSequence(i,Math.min(i+CAPACITY, spannabletext.length()))),"...");
					}
				}
				tv2.setText(display);
			}

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent=new Intent(EventsActivity.this, DescriptionActivity.class);
					intent.putExtra("eventid",idx);
					startActivity(intent);
				}
			});
			return row;
		}


	}
	private int findall(String src, String search, int[] output,int offset,boolean normalize) {
		if(normalize) {
			src=Util.normalize(src);
			search=Util.normalize(search);
		}
		int i=0;
		while(offset<output.length&&(i=src.indexOf(search, i))>=0) {output[offset++]=i;i++;}
		return offset;
	}
	private boolean contains(String src,String search,boolean normalize) {
		if(normalize) {
			src=Util.normalize(src);
			search=Util.normalize(search);
		}
		return src.contains(search);
	}


	private int lookforbestpreview(int[] searchhits, int searchhitcount, int capacity) {
		if(searchhits.length==0)return 0;
		int start=0,end=0;
		int maxstart=0, maxend=0;
		int max=-1;
		while(end<searchhitcount) {
			if(searchhits[end]-searchhits[start]>capacity) start++;
			else {
				if(searchhits[end]-searchhits[start]>max) {
					max=searchhits[end]-searchhits[start];
					maxstart=start;
					maxend=end;
				}
				end++;
			}
		}
		return Math.max(0, (searchhits[maxend]+searchhits[maxstart]-capacity)/2);
	}
	/** Called when the activity is first created. */
	boolean issearchfieldout=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_time_table);
		hitcount=MyApplication.indicium.group_table.length;
		hits=new Integer[512];
		//score=new int[hitcount=MyApplication.indicium.group_table.length];
		for (int i = 0; i < hits.length; i++) hits[i]=i;
		lv=(ListView) this.findViewById(R.id.listview);
		lv.setAdapter(adapter=new MyEventsAdapter(this, R.layout.event_entry));
		final AutoCompleteTextView searchfield=(AutoCompleteTextView)findViewById(R.id.searchtext);
		final ImageView searchbutton=(ImageView)findViewById(R.id.imageButton1);
		searchbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(issearchfieldout) {
					searchfield.setVisibility(View.GONE);
					searchbutton.setImageResource(R.drawable. ic_action_search);
					filterkeywords=null;
					refresh();
				}
				else {
					searchfield.setVisibility(View.VISIBLE);
					searchbutton.setImageResource(R.drawable.navigation_cancel);
					
				}
				issearchfieldout=!issearchfieldout;
			}
		});
		searchfield.addTextChangedListener(new  TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(searchfield.getText().length()==0)filterkeywords=null;
				else filterkeywords=searchfield.getText().toString().split(" ");
				refresh();
			}
		});
        MultiSpinner multiSpinner1 = (MultiSpinner) findViewById(R.id.spinner1);
        multiSpinner1.setItems(Arrays.asList(anami.type.names),"分類...",new MultiSpinnerListener() {
			@Override
			public void onItemsSelected(boolean[] selected) {
				int typefilter=0;
				for (int i = 0; i < selected.length; i++) 
					if(selected[i])typefilter|=1<<i;
				EventsActivity.this.filtertype=typefilter;
				
				refresh();
			}
		});
        MultiSpinner multiSpinner2 = (MultiSpinner) findViewById(R.id.spinner2);
        multiSpinner2.setItems(Arrays.asList(new String[] {"講堂","小講堂","外ステージ","ライブハウス", "教室","その他"}),"場所...",new MultiSpinnerListener() {
			@Override
			public void onItemsSelected(boolean[] selected) {
				int placefilter=0;
				for (int i = 0; i < selected.length; i++) 
					if(selected[i])placefilter|=1<<i;
				EventsActivity.this.filterplace=placefilter;
				refresh();
			}
		});
        Spinner multiSpinner3 = (Spinner) findViewById(R.id.spinner3);
        multiSpinner3.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
        		new String[] {"名前で並べ替え","時間で並べ替え","ランダムに並べ替え"}));
        multiSpinner3.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int selected, long arg3) {
				sortby=selected;
				refresh();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				sortby=1;
			}});
		//setListAdapter(adapter=new MyEventsAdapter(EventsActivity.this, R.layout.thumbnail_entry));
	
		
	}
	ListView lv;
	private Integer[] hits;
	int hitcount;
	private String[] filterkeywords;
	private int filterplace;
	private int filtertype;
	private int sortby;
	private ArrayAdapter<Object> adapter;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.time_table, menu);
        
	}

	protected void refresh() {
		int count=0;
		
		for (int i = 0; i < MyApplication.indicium.group_table.length; i++) {
			int hittimes=ishit(MyApplication.indicium.group_table[i],true);
			for (int j = 0; j < hittimes; j++) hits[count++]=i|j<<16;
		}

		this.hitcount=count;
		if(sortby==2) {
			Util.ShuffleArray(hits,hitcount);
		}else if(sortby==0) {
			//"名前で並べ替え"
			Arrays.sort(hits,0,hitcount,new Comparator<Integer>() {			@Override
			public int compare(Integer arg0, Integer arg1) {
				return Util.normalize(MyApplication.indicium.group_table[arg0&0xffff].name).compareToIgnoreCase(Util.normalize(MyApplication.indicium.group_table[arg1&0xffff].name));
			}});
		}else {
			//"時間で並べ替え"
			
		Arrays.sort(hits,0,hitcount,new Comparator<Integer>() {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				anami.Record_group_table p0,p1;
				p0=MyApplication.indicium.group_table[arg0&0xffff];
				p1=MyApplication.indicium.group_table[arg1&0xffff];

				anami.Indicium_Time q0=p0.time.length==0?null:p0.time[arg0>>16];
				anami.Indicium_Time q1=p1.time.length==0?null:p1.time[arg1>>16];
				return Util.TimeComparator.getdef().compare(q0, q1);
			}});
		}
		//for (int i = 0; i < hits.length; i++) hits[i]=hits[i]&0xffff;
		adapter.notifyDataSetChanged();
	}
	private int ishit(anami.Record_group_table record,boolean strict) {
		if(strict) {
			if(filterplace!=0) {
				int placeindex=getplaceindex(record);
				if((filterplace&placeindex)==0)return 0;
			}
			if(filtertype!=0) {
				if(((1<<record.type)&filtertype)==0)return 0;
			}
			if(filterkeywords!=null) {
				for (String str : filterkeywords) {
					/*boolean flagplace=false;
					for (anami.Record_place_table place : record.place) 
						if(contains(place.name,str,true)) {flagplace=true;break;}*/
					boolean flagname=contains(record.name,str,true);
					//boolean flagtype=record.type!=-1&&contains(anami.type.names[record.type],str,true);
					boolean flagdescription=contains(record.description,str,true);
					//boolean flagdetailedtype=record.activity_type!=-1&&contains(anami.activity_type.names[record.activity_type],str,true);
					if(!(/*flagplace||*/flagname/*||flagtype*/||flagdescription/*||flagdetailedtype*/))return 0;
					
				}
			}
			
			return sortby==1?Math.max(record.time.length,1):1;
		}
		else {
			return 0;//todo
		}
		
	}

	private int getplaceindex(Record_group_table record) {
		//"講堂","小講堂","外ステージ","ライブハウス", "教室","その他"
		int ret=0;
		if(record.place==null||record.place.length==0)return 32;
		for (anami.Record_place_table place : record.place) {
			if(place.name.equals("講堂"))ret|=1;
			else if(place.name.equals("小講堂"))ret|=2;
			else if(place.name.equals("外ステージ"))ret|=4;
			else if(place.name.startsWith("ライブハウス"))ret|=8;
			else if(place!=null)ret|=16;
		}
		return ret;
	}

/*	private int getcomparison(Record_group_table record) {
		switch (sortby) {
		case value:
			
			break;

		default:
			break;
		}
	}*/
}