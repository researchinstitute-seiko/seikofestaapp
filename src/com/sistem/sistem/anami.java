
package com.sistem.sistem;

import java.io.*;
import android.graphics.*;
import android.text.format.Time;
public class anami{


    public static Bitmap getScaledBitmap(byte[] bytes, int newSize,BitmapFactory.Options opts) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;

        BitmapFactory.decodeByteArray(bytes,0,bytes.length, options);
        if ((options.outWidth == -1) || (options.outHeight == -1))
            return null;

        int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
                : options.outWidth;

        opts.inSampleSize = originalSize / newSize;
        Bitmap scaledBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length, opts);
        return scaledBitmap;     
    }

    public static int readint7bit(DataInputStream dis){
        int ret=0;
        byte temp;
        try{
            do{
                temp=dis.readByte();
                ret=ret<<7|temp&127;
            }while((temp&128)!=0);
        }
        catch(Exception ex){throw new Error(ex);}
        return ret;
    }

    private static long getfestatimemillis(){
        Time t=new Time();
		t.set(0, 0, 0, 26, 4, 2014);
        return t.toMillis(false);
    }




public static class Indicium{

    DataInputStream dis;
    InputStream is;
    public Indicium(InputStream is){
        dis=new DataInputStream(this.is=is);
        load();
    }

    //Tables
public Record_place_table[] place_table;
public Record_group_table[] group_table;

    private void load(){
        try{
            dis.mark(1000000000);
            if(dis.readInt()!=0x696e0100)throw new Error();
int datalen;
place_table = new Record_place_table[datalen=dis.readShort()];
for(int i=0;i<datalen;i++)place_table[i]=new Record_place_table(this,dis);
group_table = new Record_group_table[datalen=dis.readShort()];
for(int i=0;i<datalen;i++)group_table[i]=new Record_group_table(this,dis);
for(int i=0;i<datalen;i++)place_table[i].prepare();
for(int i=0;i<datalen;i++)group_table[i].prepare();

        }catch(Exception ex){throw new Error(ex);}
    }

}

public static class Indicium_Time{
    public boolean isall,isspan;
    public static long festatimemillis=getfestatimemillis();
    public Time start;
    public Time end;
    public Indicium_Time(DataInputStream dis){
        try{
            byte mode=dis.readByte();
            if(mode==2)isall=true;
            else{
                long start=dis.readLong();
                this.start=new Time();
                this.start.set(start+festatimemillis);
                if(mode==1){
                    isspan=true;
                    long end=dis.readLong();
                    this.end=new Time();
                    this.end.set(end+festatimemillis);
                }
            }
        }catch(Exception ex){throw new Error(ex);}
    }
}

public static class Indicium_Html{
    DataInputStream dis;
    int pos;
    public Indicium_Html(DataInputStream dis, int pos){
        this.dis=dis;
        this.pos=pos;
    }
    public String get(){
        try{
            dis.reset();
            dis.skip(pos);
            byte[] ret=new byte[dis.readInt()];
            dis.read(ret);
            return new String(ret, "UTF-8"); 

        }catch(Exception ex){throw new Error(ex);}
    }
}
public static class Indicium_Binary{
    DataInputStream dis;
    int pos;
    public Indicium_Binary(DataInputStream dis, int pos){
        this.dis=dis;
        this.pos=pos;
    }
    public byte[] get(){
        try{
            dis.reset();
            dis.skip(pos);
            byte[] ret=new byte[dis.readInt()];
            dis.read(ret);
            return ret; 

        }catch(Exception ex){throw new Error(ex);}
    }
}
public static class Indicium_Image{
    DataInputStream dis;
    int pos;
    Bitmap bitmap;
    public Indicium_Image(DataInputStream dis, int pos){
        this.dis=dis;
        this.pos=pos;


    }
    public Bitmap get(){
if(bitmap==null){

        try{
            dis.reset();
            dis.skip(pos);
            byte[] ret=new byte[dis.readInt()];
            dis.read(ret);
            bitmap=BitmapFactory.decodeByteArray(ret,0,ret.length);

        }catch(Exception ex){throw new Error(ex);}
}
return bitmap;}

}
public static class Indicium_Large_Image{
    DataInputStream dis;
    int pos;
    public Indicium_Large_Image(DataInputStream dis, int pos){
        this.dis=dis;
        this.pos=pos;
    }
    public Bitmap get(){
        return get(null);
    }
    public Bitmap get(BitmapFactory.Options opt){
        try{
            dis.reset();
            dis.skip(pos);
            byte[] ret=new byte[dis.readInt()];
            dis.read(ret);
            return BitmapFactory.decodeByteArray(ret,0,ret.length,opt);

        }catch(Exception ex){throw new Error(ex);}
    }
    public Bitmap get(BitmapFactory.Options opts,int size){
        try{
            dis.reset();
            dis.skip(pos);
            byte[] bytes=new byte[dis.readInt()];
            dis.read(bytes);
            return getScaledBitmap(bytes, size,opts);
        }catch(Exception ex){throw new Error(ex);}
        
    }
}

public static class Record_place_table {

    private Indicium database;
    public Record_place_table(Indicium database,DataInputStream dis){
        this.database=database;
        try{
        int datalen;type=dis.readShort();
name=dis.readUTF();
floor=anami.readint7bit(dis);
datalen=dis.readUnsignedShort();
vertices=new float[datalen];
for(int i=0;i<datalen;i++){
vertices[i]=dis.readFloat();
}
rotationclockwise=anami.readint7bit(dis);
stairwayleft=dis.readShort();
stairwayright=dis.readShort();
restroomtype=dis.readShort();
datalen=dis.readUnsignedShort();
group_table=new Record_group_table[datalen];
id_group_table=new int[datalen];
for(int i=0;i<datalen;i++){
id_group_table[i]=dis.readShort();
}
        }catch(Exception ex){throw new Error(ex);} }
public int type;
public String name;
public int floor;
public float[] vertices;
public int rotationclockwise;
public int stairwayleft;
public int stairwayright;
public int restroomtype;
private int[] id_group_table;
public Record_group_table[] group_table;

    public void prepare(){

for(int i=0;i<group_table.length;i++)group_table[i]=this.database.group_table[id_group_table[i]];
        
    }
}

public static class Record_group_table {

    private Indicium database;
    public Record_group_table(Indicium database,DataInputStream dis){
        this.database=database;
        try{
        int datalen;name=dis.readUTF();
type=dis.readShort();
detailed_type=dis.readShort();
description=dis.readUTF();
icon=new Indicium_Image(dis,dis.readInt());
datalen=dis.readUnsignedShort();
place=new Record_place_table[datalen];
id_place=new int[datalen];
for(int i=0;i<datalen;i++){
id_place[i]=dis.readShort();
}
datalen=dis.readUnsignedShort();
time=new Indicium_Time[datalen];
for(int i=0;i<datalen;i++){
time[i]=new Indicium_Time(dis);
}
        }catch(Exception ex){throw new Error(ex);} }
public String name;
public int type;
public int detailed_type;
public String description;
public Indicium_Image icon;
private int[] id_place;
public Record_place_table[] place;
public Indicium_Time[] time;

    public void prepare(){

for(int i=0;i<place.length;i++)place[i]=this.database.place_table[id_place[i]];
        
    }
}

public static class ObjectType{

public static final int Floor = 0;
public static final int Room = 1;
public static final int Staircase = 2;
public static final int Restroom = 3;
public static final String[] names = new String[]{"Floor", "Room", "Staircase", "Restroom"};
}

public static class StairWay{

public static final int None = 0;
public static final int Up = 1;
public static final int Down = 2;
public static final String[] names = new String[]{"None", "Up", "Down"};
}

public static class RestRoomType{

public static final int Male = 0;
public static final int Female = 1;
public static final int Both = 2;
public static final String[] names = new String[]{"Male", "Female", "Both"};
}

public static class type{

public static final int 趣味研 = 0;
public static final int 食品 = 1;
public static final int 企画 = 2;
public static final int バンド = 3;
public static final int 部活 = 4;
public static final int 本部 = 5;
public static final int その他 = 6;
public static final String[] names = new String[]{"趣味研", "食品", "企画", "バンド", "部活", "本部", "その他"};
}

public static class detailed_type{

public static final int 体験型展示 = 0;
public static final int 研究発表 = 1;
public static final int 食事 = 2;
public static final String[] names = new String[]{"体験型展示", "研究発表", "食事"};
}
}
