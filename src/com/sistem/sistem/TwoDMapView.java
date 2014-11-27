package com.sistem.sistem;

import java.util.ArrayList;

import com.sistem.sistem.anami.Record_place_table;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.UserInput;
import com.sistem.sistem.modelviewer2.Framework3D.UserInput.vec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class TwoDMapView extends View {
	
	public TwoDMapView(Context context) {
		super(context);
		init(null, 0);
	}

	public TwoDMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public TwoDMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}
Bitmap[] bitmaps;

	private void init(AttributeSet attrs, int defStyle) {
		bitmaps=new Bitmap[] {BitmapFactory.decodeResource(getResources(), R.drawable.restroom_m),
				BitmapFactory.decodeResource(getResources(), R.drawable.restroom_f),
				BitmapFactory.decodeResource(getResources(), R.drawable.restroom_both),};
		
		
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.TwoDMapView, defStyle, 0);

		a.recycle();


	}

 @Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// TODO Auto-generated method stub
	super.onSizeChanged(w, h, oldw, oldh);
	UpdateBoundInfo();
}
 int paddingLeft,paddingTop, paddingRight,paddingBottom, contentWidth,contentHeight;
 void UpdateBoundInfo() {
		// TODO: consider storing these as member variables to reduce
		// allocations per draw cycle.
		paddingLeft = getPaddingLeft();
		paddingTop = getPaddingTop();
		paddingRight = getPaddingRight();
		paddingBottom = getPaddingBottom();
		contentWidth = getWidth() - paddingLeft - paddingRight;
		contentHeight = getHeight() - paddingTop - paddingBottom;
		
		float w=contentWidth/scaleX;
		float h=contentHeight/scaleY;
		float halfw=w*0.5f;
		float halfh=h*0.5f;
		boundingbox2.left=centerX-halfw;
		boundingbox2.right=centerX+halfw;
		boundingbox2.top=centerY-halfh;
		boundingbox2.bottom=centerY+halfh;
		
 }
 
 	int floor=2;
 	float scaleX=0.005f;
 	float scaleY=-0.005f;
 	float centerX=10000,centerY=0;
 
 	RectF boundingbox1=new RectF();
 	RectF boundingbox2=new RectF();
 	//RectF boundingboxform=new RectF();
 	public static RectF getBoundingBox(float[] vertices,RectF result) {
 		float xmin=-Float.MAX_VALUE;
 		float xmax=Float.MAX_VALUE;
 		float ymin=-Float.MAX_VALUE;
 		float ymax=Float.MAX_VALUE;
 		for (int i = 0; i < vertices.length; i+=2) {
			float x=vertices[i];
			float y=vertices[i+1];
			if(x<xmin)xmin=x;
			if(x>xmax)xmax=x;
			if(y<ymin)ymin=y;
			if(y>ymax)ymax=y;
		}
 		result.left=xmin;result.right=xmax;
 		result.top=ymin;result.bottom=ymax;
 		return result;
 	}
 	
 	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(contentWidth==0)UpdateBoundInfo();
		if(MyApplication.indicium==null) return;
		for (anami.Record_place_table place : MyApplication.indicium.place_table) {
			if (place.floor==floor&&getBoundingBox(place.vertices,boundingbox1).intersect(boundingbox2)) {
				_draw(place,canvas);				
			}
		}
		
	}

	Path path=new Path();
	Paint paint=new Paint();
	Paint textpaint=new Paint();
	/*
	void setpaths() {
		for (int i = 0; i < MyApplication.indicium.place_table.length; i++) {
			Path p=paths[i]=new Path();
			
			
		}
	}*/
	float[] pos=new float[3];
	float[] verts=new float[8];
	
	private void _draw(Record_place_table place, Canvas canvas) {
		//float[] lines=new float[2*place.vertices.length];
		
		
		//Paint settings;;
		switch (place.type) {
		case anami.ObjectType.Floor:
			paint.setColor(Color.GRAY);
			paint.setStyle(Style.FILL);
			canvas.drawPath(makepath(place,this.path), paint);
			break;
		case anami.ObjectType.Restroom:
			
			
			canvas.drawBitmapMesh(bitmaps[place.restroomtype], 1, 1, makeverts(place,verts), 0, null, 0, paint);
			break;
		case anami.ObjectType.Room:

			Util.average(place, pos	, 0);
			float x=pos[0],y=pos[1];
			int count=place.group_table.length;
			paint.setColor(count==0?Color.GRAY:count==1?MyApplication.colortable[place.group_table[0].type]:0xffff8080);
			paint.setStyle(Style.FILL);
			textpaint.setTextSize(12);
			textpaint.setTextAlign(Align.CENTER);
			canvas.drawPath(makepath(place,this.path), paint);
			canvas.drawText((count==0||count>=2?place.name:place.group_table[0].name).replace(' ','\n' ).replace('・', '\n'),(x-centerX)*scaleX+contentWidth/2, (y-centerY)*scaleY+contentHeight/2, textpaint);
			break;
		case anami.ObjectType.Staircase:

			paint.setColor(Color.GRAY);
			paint.setStyle(Style.FILL);
			for (int i = 0; i < 5; i++) {
				float t= (0.2f*i);
				float x1=place.vertices[0]*(1-t)+place.vertices[0]*t;
				float y1=place.vertices[1]*(1-t)+place.vertices[1]*t;
				float x2=place.vertices[0]*(1-t+0.2f)+place.vertices[0]*(t+0.2f);
				float y2=place.vertices[1]*(1-t+0.2f)+place.vertices[1]*(t+0.2f);
				float x4=place.vertices[2]*(1-t)+place.vertices[2]*t;
				float y4=place.vertices[3]*(1-t)+place.vertices[3]*t;
				float x3=place.vertices[2]*(1-t+0.2f)+place.vertices[2]*(t+0.2f);
				float y3=place.vertices[3]*(1-t+0.2f)+place.vertices[3]*(t+0.2f);
				x=(x1+x2+x3+x4)*0.25f;
				y=(y1+y2+y3+y4)*0.25f;
				this.path.reset();
				path.moveTo(((x1-x)*0.9f+x-centerX)*scaleX+contentWidth/2, ((y1-y)*0.9f+y-centerY)*scaleY+contentHeight/2);
				path.lineTo(((x2-x)*0.9f+x-centerX)*scaleX+contentWidth/2, ((y2-y)*0.9f+y-centerY)*scaleY+contentHeight/2);
				path.lineTo(((x3-x)*0.9f+x-centerX)*scaleX+contentWidth/2, ((y3-y)*0.9f+y-centerY)*scaleY+contentHeight/2);
				path.lineTo(((x4-x)*0.9f+x-centerX)*scaleX+contentWidth/2, ((y4-y)*0.9f+y-centerY)*scaleY+contentHeight/2);
				path.close();

				canvas.drawPath(makepath(place,this.path), paint);
			}
			
			break;
		}
		
	}

	private float[] makeverts(Record_place_table place, float[] verts) {

		Util.average(place, pos	, 0);
		float x=pos[0],y=pos[1];
		verts[0]=((place.vertices[0]-x)*0.9f+x-centerX)*scaleX+contentWidth/2;
		verts[1]=((place.vertices[1]-y)*0.9f+y-centerY)*scaleY+contentHeight/2;
		 verts[2]=((place.vertices[2]-x)*0.9f+x-centerX)*scaleX+contentWidth/2;
		verts[3]=((place.vertices[3]-y)*0.9f+y-centerY)*scaleY+contentHeight/2;
		 verts[4]=((place.vertices[6]-x)*0.9f+x-centerX)*scaleX+contentWidth/2;
		 verts[5]=((place.vertices[7]-y)*0.9f+y-centerY)*scaleY+contentHeight/2;
		 verts[6]=((place.vertices[4]-x)*0.9f+x-centerX)*scaleX+contentWidth/2;
		verts[7]=((place.vertices[5]-y)*0.9f+y-centerY)*scaleY+contentHeight/2;
		return verts;
	}

	private Path makepath(Record_place_table place, Path path) {
		Util.average(place, pos	, 0);
		float x=pos[0],y=pos[1];
		float scale=1.0f;//(place.type==anami.ObjectType.Floor||place.type==anami.ObjectType.Restroom)?0.9f:1.0f;
		path.reset();
		path.moveTo(((place.vertices[0]-x)*scale+x-centerX)*scaleX+contentWidth/2, ((place.vertices[1]-y)*scale+y-centerY)*scaleY+contentHeight/2);
		
		for (int i = 2; i < place.vertices.length; i+=2) {
			path.lineTo(((place.vertices[i]-x)*scale+x-centerX)*scaleX+contentWidth/2, ((place.vertices[i+1]-y)*scale+y-centerY)*scaleY+contentHeight/2);
		}
		path.close();
		return path;
	}
	
	
	UserInput ui=new UserInput(null) {
		
		@Override
		public boolean slide(float x, float y, float dx, float dy) {
			centerX-=dx;
			centerY+=dy;
			return true;
		}
		@Override
		public boolean zoom(float dscale) {
			scaleX*=dscale;
			scaleY*=dscale;
			return true;
		}
		
		@Override
		public void detached() {}
		
		@Override
		public void attached() {}
	};
/*	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
	}*/
	
	

}
