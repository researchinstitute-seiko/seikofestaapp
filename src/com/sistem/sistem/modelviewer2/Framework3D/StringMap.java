package com.sistem.sistem.modelviewer2.Framework3D;

import java.security.KeyException;
import java.util.Arrays;

public class StringMap{

	int _count;
	String[] Keys;
	int[] Values;
	int def_size=16;
	int capacity=0;

	public StringMap(){
		clear();
	}
	public StringMap(int capacity){
		def_size=capacity;
		clear();
	}
	/*public StringMap(int capacity,int def_value){
		def_size=capacity;
		GetOrThrow=false;
		this.def_value=def_value;
		clear();
	}*/
	public StringMap(String[] keys, int[] values)
	{
		MergeSort(Keys=keys,Values=values);
		capacity=_count=keys.length;
	}
	/*public StringMap(String[] keys, int[] values, int def_value){
		GetOrThrow=false;
		this.def_value=def_value;
		MergeSort(Keys=keys,Values=values);
		capacity=_count=keys.length;
	}
*/
	public void clear() {
		_count=0;
		capacity=def_size;
		Keys=new String[def_size];
		Values=new int[def_size];
	}

	public int indexOf(String Key){	return BinarySearch(Keys, Key,_count);}
	public String getKey(int index){return Keys[assertbounds(index)];}
	public int getValue(int index){	return Values[assertbounds(index)];}
	private int assertbounds(int index){	if(!checkbounds(index))throw new IndexOutOfBoundsException();return index;}
	private boolean checkbounds(int index){	if(index>=_count||index<0)return false;else return true;}
	public int get(String Key) throws KeyException{
		int index=indexOf(Key);
		if(index<0) throw new KeyException(String.format("Key not found. Key=\"%s\", Index=%s, KeyList=%s",Key,Integer.toString(index),stringarraytologform(Keys,100) ));
		return Values[index];
	}
	public void add(String Key, int Value) throws IllegalArgumentException{
		int i=indexOf(Key);
		if(i>=0)throw new IllegalArgumentException();
		addat(Key,Value,~i);
	}
	private void addat(String Key, int Value, int i){
		SetSize(_count+1);
		Insert(Keys,Key,i,_count);
		Insert(Values,Value,i,_count);
	}
	private void SetSize(int newsize){
		_count=newsize;
		if(capacity<newsize){
			if(capacity*2>=newsize)capacity=capacity*2;
			else capacity=newsize;
			Keys=Arrays.copyOf(Keys, capacity);
			Values=Arrays.copyOf(Values, capacity);	
			}
	}
	public void put(String Key,int Value){
		int i=indexOf(Key);
		if(i>=0)setat(i,Value);
		else {
			addat(Key,Value,~i);
		}
	}
	public void set(String Key, int Value){	setat(indexOf(Key),Value);	}
	public void setat(int index, int Value){Values[assertbounds(index)]=Value;}
	public void removeat(int index){
		for(int i=assertbounds(index);i<_count-1;i++){
			Keys[i]=Keys[i+1];
			Values[i]=Values[i+1];
		}
		SetSize(_count-1);
	}
	public boolean remove(String Key){
		int index=indexOf(Key);
		if(!checkbounds(index))return false;
		removeat(index);return true;
	}
	public int size() {	return _count;}
	public String[] Keys(){return Keys;}
	public int[] Values(){return Values;}
	
	static int BinarySearch(String[] array, String key,int count){
		return BinarySearch(array,key,0,count);
	}
	static int BinarySearch(String[] array, String key,int start, int end){
		int mid=0;
		int count=end-start;
		while(count>0){
			mid=(count>>1)+start;
			int compare=key.compareTo(array[mid]);
			if(compare>0)start=mid+1;
			else if(compare<0)end=mid;
			else return mid;
			count=end-start;
		}
		return ~start;
	}
	static int BinarySearchInsertion(String[] array, String key,int start, int end){
		int mid=0;
		int count=end-start;
		while(count>0){
			mid=(count>>1)+start;
			int compare=key.compareTo(array[mid]);
			if(compare>=0)start=mid+1;
			else end=mid;
			count=end-start;
		}
		return start;
	}
	
	static long BinarySearchStarttoEnd(String[] array, String key,int count){
		return BinarySearchStarttoEnd(array,key,0,count);
	}
	static long BinarySearchStarttoEnd(String[] array, String key,int start, int end){

		int count=end-start;
		int mid=0;
		int high=-1;
		int low=-1;
		
		while(count>0){
			
			mid=(count>>1)+start;
			int compare=key.compareTo(array[mid]);
			if(compare>0)start=mid+1;
			else if(compare<0)end=mid;
			else{
			//Correspondence
				high=low=mid;
				count=end-high;
				while(count>0){
					mid=(count>>1)+high;
					compare=key.compareTo(array[mid]);
					if(compare<0)end=mid;
					else high=mid+1;
					count=end-high;
				}
				count=low-start;
				while(count>0){
					mid=(count>>1)+start;
					compare=key.compareTo(array[mid]);
					if(compare>0)start=mid+1;
					else low=mid;
					count=end-high;
				}
				return low<<32&high;
			}
			count=end-start;
		}
		return ~start;
		
	}
	static void MergeSort(String[] keys, int[] values){
		MergeSort(keys,values,keys.clone(),values.clone(),0,keys.length);
	}
	static void Insert(String[] array,String item,int index,int end){
		for(int i=end;i>index;i--){
			array[i]=array[i-1];
		}
		array[index]=item;
	}
	static void Insert(int[] array,int item,int index,int end){
		for(int i=end;i>index;i--){
			array[i]=array[i-1];
		}
		array[index]=item;
	}
	static void MergeSort(String[] keys, int[] values,String[] keysbuf, int[] valuesbuf,int start,int count){

		int end=start+count;
		if(count<=8){
			for(int i=start+1;i<end;i++)
			{
				int insindex=BinarySearchInsertion(keys,keys[i],start,i);
				Insert(keys,keys[i],insindex,i);
				Insert(values,values[i],insindex,i);
			}
			return;
		}
		int half=count>>1;
		int mid=start+half;
		MergeSort(keysbuf,valuesbuf,keys,values,start,half);
		MergeSort(keysbuf,valuesbuf,keys,values,mid,count-half);
		int i1=start;
		int i2=mid;
		int i=start;
		if(keysbuf[mid].compareTo(keysbuf[mid-1])>=0){
			System.arraycopy(keysbuf, start, keys, start, count);
			System.arraycopy(valuesbuf, start, values, start, count);
		}else{
		while(i1<mid&&i2<end){
			int compare=keysbuf[i1].compareTo(keysbuf[i2]);
			if(compare>0){
				keys[i]=keysbuf[i2];
			values[i]=valuesbuf[i2++];
			}
			else {
				keys[i]=keysbuf[i1];
				values[i]=valuesbuf[i1++];
				}
			i++;
			
		}
			System.arraycopy(keysbuf, i2, keys, i, end-i2);
			System.arraycopy(valuesbuf, i2, values, i, end-i2);
			i+=end-i2;
			System.arraycopy(keysbuf, i1, keys, i, mid-i1);
			System.arraycopy(valuesbuf, i1, values, i, mid-i1);
		}
		
		
		
	}
	static String stringarraytologform(String[] s, int maxcount) {
	     if(s.length==0)return "";
	     StringBuilder builder = new StringBuilder();
	     int i=0;
	     builder.append("{");
	     while(true)
	     {
	    	 builder.append("\"");
	         builder.append(s[i]);
	    	 builder.append("\"");
	         if(++i>=s.length)break;
	         else if(i>=maxcount){
	        	 builder.append(", ...(len=");
	        	 builder.append(s.length);
	        	 builder.append(")");
	         }
	         builder.append(", ");
	     }
	     builder.append("}");
	     return builder.toString();
	 }

}