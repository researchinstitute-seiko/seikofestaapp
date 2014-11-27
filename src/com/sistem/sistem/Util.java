package com.sistem.sistem;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import com.sistem.sistem.anami.Indicium_Time;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.BitmapFactory.Options;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.text.style.StyleSpan;

public class Util {

	public static float clamp(float value,float min,float max){
		return Math.min(max, Math.max(min, value));
	}
	public static double clamp(double value,double min,double max){
		return Math.min(max, Math.max(min, value));
	}
	public static int clamp(int value,int min,int max){
		return Math.min(max, Math.max(min, value));
	}
	public static boolean isonedge(float value,float min,float max){
		return value<=min||value>=max;
	}
	public static boolean isonedge(int value,int min,int max){
		return value<=min||value>=max;
	}
	public static boolean isonedge(double value,double min,double max){
		return value<=min||value>=max;
	}
	public static Bitmap getScaledBitmap(int id, Resources res, int newSize) {
		return getScaledBitmap(id,res,newSize,new Options());   
	}
	public static Bitmap getScaledBitmap(int id, Resources res, int newSize,Options opts) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inInputShareable = true;
		options.inPurgeable = true;
		BitmapFactory.decodeResource(res, id, options);
		if ((options.outWidth == -1) || (options.outHeight == -1))
			return null;

		int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
				: options.outWidth;

		opts.inSampleSize = originalSize / newSize;
		try {
			return BitmapFactory.decodeResource(res, id,opts);
		}catch(OutOfMemoryError ex) {
			System.gc();
			return BitmapFactory.decodeResource(res, id,opts);
		}

	}
	public static void ShuffleArray(int[] array,int[] accompaniment)
	{
		int index;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			if (index != i)
			{
				int temp=array[index];
				array[index]=array[i];
				array[i]=temp;
				temp=accompaniment[index];
				accompaniment[index]=accompaniment[i];
				accompaniment[i]=temp;
			}
		}
	}
	public static void ShuffleArray(int[] array)
	{
		int index;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			if (index != i)
			{
				int temp=array[index];
				array[index]=array[i];
				array[i]=temp;
			}
		}
	}
	public static String join(Collection<?> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;                  
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}
	public static String join(String[] s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < s.length; i++) {
			String string = s[i];
			builder.append(string);
			if (i==s.length-1) {
				break;                  
			}
			builder.append(delimiter);

		}
		return builder.toString();
	}
	public static String join(anami.Record_place_table[] s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < s.length; i++) {
			String string = String.format("%d階 %s", s[i].floor,s[i].name);
			builder.append(string);
			if (i==s.length-1) {
				break;                  
			}
			builder.append(delimiter);

		}
		return builder.toString();
	}
	public static void average(anami.Record_place_table activity_place,
			float[] pos, int offset) {
		float xsum=0,ysum=0;
		for (int i = 0; i < activity_place.vertices.length; i+=2) {
			xsum+=activity_place.vertices[i];
			ysum+=activity_place.vertices[i+1];
		}
		pos[offset]=xsum*2/activity_place.vertices.length;
		pos[offset+1]=ysum*2/activity_place.vertices.length;
		pos[offset+2]=(activity_place.floor-1)*3000f;
	}
	public static void normalize(char[] str) {
		for (int i = 0; i < str.length; i++) {
			char c = str[i];
			c=replacerange(c,'Ａ','Ｚ','A');
			c=replacerange(c,'ａ','ｚ','a');
			c=replacerange(c,'A','Z','a');
			c=replacerange(c,'ァ',(char)0x30f4,'ぁ');
			c=replacerange(c,'０','９','0');
			str[i]=c;
		}
	}
	public static String normalize(String str) {
		char[] buf=str.toCharArray();
		normalize(buf);
		return new String(buf);
	}
	public static char replacerange(char c, char small, char big, char rightsmall) {
		if(btwc(c,small,big)) return (char) (c-(small-rightsmall));else return c;
	}
	public static boolean btwc(int val, int small, int big) {
		return small<=val&&val<=big;
	}
	public static boolean btwc(char val, char small, char big) {
		return small<=val&&val<=big;
	}
	public static void ShuffleArray(Object[] array) {
		ShuffleArray(array,array.length);
	}
	public static String timetostring(anami.Indicium_Time time) {
		if(time==null)return null;
		if(time.isall)return "all";
		else if(time.start.toMillis(false)==anami.Indicium_Time.festatimemillis)return null;
		else if(!time.isspan){return time.start.format("%d日 %H:%M");}
		else return time.start.format("%d日 %H:%M")+" ~ " +time.end.format("%H:%M");
	}
	public static CharSequence join(Indicium_Time[] time, String delimiter, int highlight) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		for (int i = 0; i < time.length; i++) {
			String string = timetostring(time[i]);
			if(string==null)continue;
			//CharSequence cs=new SpannableString(string);
			if(builder.length()>0)builder.append(delimiter);
			builder.append(string);
			if(i==highlight)builder.setSpan(new StyleSpan(Typeface.BOLD), builder.length()-string.length(), builder.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return builder.toString();
	}
	public static String join(Indicium_Time[] time, String delimiter) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		for (int i = 0; i < time.length; i++) {
			String string = timetostring(time[i]);
			if(string==null)continue;
			if(builder.length()>0)builder.append(delimiter);
			builder.append(string);
		}
		return builder.toString();
	}
	public static String getplaintext(String html) {
		StringBuilder ret=new StringBuilder();
		html=html.replace("\r\n",	" ").replace("\n", " ").replace("\r", " ").replace("  ", " ");
		int depth=0;
		for (int i = 0; i < html.length(); i++) {
			char letter=html.charAt(i);
			if(letter=='<')depth++;
			else if(letter=='>') {
				depth--;
				if(depth==0)ret.append(' ');
			}
			else if(depth==0) {
				ret.append(letter);
			}
		}
		return ret.toString();
	}
	public static void ShuffleArray(Object[] array, int count) {
		int index;
		Random random = new Random();
		for (int i = count - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			if (index != i)
			{
				Object temp=array[index];
				array[index]=array[i];
				array[i]=temp;
			}
		}
	}
	public static  class TimeComparator implements Comparator<anami.Indicium_Time>{

		static TimeComparator def;
		public static TimeComparator getdef() {
			if(def==null)def=new TimeComparator();
			return def;
		}
		@Override
		public int compare(Indicium_Time p0, Indicium_Time p1) {
			Time q0,q1;
			
		q0=p0==null?null:p0.start;
		if(q0!=null&&q0.toMillis(false)==anami.Indicium_Time.festatimemillis)q0=null;
		q1=p1==null?null:p1.start;
		if(q1!=null&&q1.toMillis(false)==anami.Indicium_Time.festatimemillis)q1=null;
		long r0=q0==null?Long.MAX_VALUE:q0.toMillis(false);
		long r1=q1==null?Long.MAX_VALUE:q1.toMillis(false);
		return r0>r1?1:r0==r1?0:-1;
		}

	}
}

