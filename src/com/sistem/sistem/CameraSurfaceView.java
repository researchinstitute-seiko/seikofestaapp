package com.sistem.sistem;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

	List<Camera.Size>  mSupportedPreviewSizes;
	//Camera.Size mPreviewSize;
	int mPreviewSizeX;
	int mPreviewSizeY;
	float displayedx;
	float displayedwidth;
	float displayedy;
	float displayedheight;
	boolean swapped;
	
	public CameraSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mPreviewSizeX != 0) {
           //mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        	int p_width=mPreviewSizeX;
        	int p_height=mPreviewSizeY;

           float viewratio=(float)width/height;
           					//(float)height/width;
           float nativeratio=(float)p_width/p_height;
                   //(float)p_height/p_width;
           boolean zoommode=true;
	            if((viewratio>=nativeratio)^(zoommode))//‰æ–Ê‚ªƒJƒƒ‰‚æ‚è‰¡’·
	            {
	            	displayedwidth=height*nativeratio;
	            	displayedx=(width-displayedwidth)/2;
	            	displayedheight=height;
	            	displayedy=0;
	            	
	            	//mSurfaceView.layout(0, 0, width, height);

	                setMeasuredDimension((int) displayedwidth, height);
	                this.getHolder().setFixedSize((int) displayedwidth, height);
	            }
	            else//‰æ–Ê‚ªƒJƒƒ‰‚æ‚èc’·
	            {
	            	displayedwidth=width;
	            	displayedx=0;
	            	displayedheight=width/nativeratio;
	            	displayedy=(height-displayedheight)/2;
	                setMeasuredDimension( width, (int) displayedheight);
	                this.getHolder().setFixedSize(width, (int)displayedheight);
	            }
        }
        else {
        	setMeasuredDimension(width,height);
	        displayedwidth=width;
	        displayedheight=height;
	        displayedx=0;
	        displayedy=0;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	if(changed)
    		layout((int)displayedx,(int)displayedy,(int)(displayedwidth+displayedx),(int)(displayedy+displayedheight));
    }
	private static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	

}
