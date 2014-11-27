package com.sistem.sistem;
 
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import org.apache.http.entity.ByteArrayEntity;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.sistem.sistem.modelviewer2.MapActivity;
 
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.AbsoluteLayout.LayoutParams;
import android.view.WindowManager;
 
public class QRActivity extends Activity {
     
    private CameraSurfaceView mSurfaceView;
    private Camera mCamera;
    private SurfaceView sv2;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout fl=new FrameLayout(this);
        LayoutParams svparams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,0,0);
        mSurfaceView = new CameraSurfaceView(this);
        mSurfaceView.setOnClickListener(onClickListener);
        fl.addView(mSurfaceView,svparams);
        sv2=new SurfaceView(this);
        sv2.setBackgroundColor(Color.TRANSPARENT);
        fl.addView(sv2,svparams);
        sv2.setZOrderMediaOverlay(true);
        this.setContentView(fl);
        
    }
     
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder holder = mSurfaceView.getHolder();
        if(holder!=null)holder.addCallback(callback);
        SurfaceHolder holder2=sv2.getHolder();
        holder2.setFormat(PixelFormat.RGBA_8888);
        if(holder2!=null)holder2.addCallback(callback2);
        
    }
     
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // �������ꂽ�Ƃ�
            mCamera = Camera.open();
            try {
                // �v���r���[���Z�b�g����
                mCamera.setPreviewDisplay(holder);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // �ύX���ꂽ�Ƃ� 
        	Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        	//mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size previewSize=previewSizes.get(4);//getOptimalPreviewSize(previewSizes, width, height);
            if(display.getRotation() == Surface.ROTATION_0){
                mCamera.setDisplayOrientation(90);
            	mSurfaceView.mPreviewSizeX=previewSize.height;
            	mSurfaceView.mPreviewSizeY=previewSize.width;
            	mSurfaceView.swapped=true;
            	}
            else if(display.getRotation() == Surface.ROTATION_90){
            	mCamera.setDisplayOrientation(0);
            	mSurfaceView.mPreviewSizeX=previewSize.width;
            	mSurfaceView.mPreviewSizeY=previewSize.height;
            	mSurfaceView.swapped=false;
            	}
            else if(display.getRotation() == Surface.ROTATION_180){
                mCamera.setDisplayOrientation(0);            
            	mSurfaceView.mPreviewSizeX=previewSize.height;
            	mSurfaceView.mPreviewSizeY=previewSize.width;
            	mSurfaceView.swapped=true;
            	}
            else if(display.getRotation() == Surface.ROTATION_270){
                mCamera.setDisplayOrientation(180);
            	mSurfaceView.mPreviewSizeX=previewSize.width;
            	mSurfaceView.mPreviewSizeY=previewSize.height;
            	mSurfaceView.swapped=false;
            	}
            //Camera.Size previewSize = previewSizes.get(0);
            int p_width=previewSize.width;
            int p_height=previewSize.height;
            //parameters.setPreviewSize(previewSize.width, previewSize.height);
            
            parameters.setPreviewSize(p_width, p_height);
            
            // width, height��ύX����
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            //timerHandler.post(timerRunnable);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // �j��ꂽ�Ƃ�
            mCamera.release();
            mCamera = null;
        }
    };
/*
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            //Do work here.
        	if (mCamera != null) {
                mCamera.autoFocus(autoFocusCallback);
            }
        }

    };*/
    private SurfaceHolder.Callback callback2 = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            
        }

        Paint paint=new Paint();
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Canvas c=holder.lockCanvas();
            //c.drawColor(0x00000000);
            float centerx=(float)width/2;
            float centery=(float)height/2;
            paint.setStrokeWidth(2f);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            c.drawRect(centerx-half, centery-half, centerx+half, centery+half, paint);
            holder.unlockCanvasAndPost(c);
        }
        
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };
    
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // �I�[�g�t�H�[�J�X
            if (mCamera != null) {
            	
                mCamera.autoFocus(autoFocusCallback);
            }
        }
    };
     
    private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                camera.setOneShotPreviewCallback(previewCallback);
            }
        }
    };

    static final int half=200;
    private PreviewCallback previewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        	// �ǂݍ��ޔ͈�
            int previewWidth = camera.getParameters().getPreviewSize().width;
            int previewHeight = camera.getParameters().getPreviewSize().height;
             
            int centerx=mSurfaceView.mPreviewSizeX/2;
            int centery=mSurfaceView.mPreviewSizeY/2;
            int halfinpreview=(int) (half*(mSurfaceView.mPreviewSizeX/mSurfaceView.displayedwidth));
            int x= centerx-halfinpreview;
            int y=centery-halfinpreview;
            if(mSurfaceView.swapped){int temp=x;x=y;y=temp;}
            // �v���r���[�f�[�^���� BinaryBitmap �𐶐� 
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    data, previewWidth, previewHeight, x ,y,halfinpreview*2, halfinpreview*2, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
             
            // �o�[�R�[�h��ǂݍ���
            Reader reader = new MultiFormatReader();
            Result result = null;
            try {
                result = reader.decode(bitmap);
                
                //String binary = result.getText();
                
                byte[] binary=result.getRawBytes();
                char[] buffer=new char[binary.length*3-1];
                String table="0123456789ABCDEF";
                		
                for(int i=0;i<binary.length;i++){
                	if(i!=0)buffer[i*3-1]='-';
                	buffer[i*3]=table.charAt((binary[i]>>4)&0xf);
                	buffer[i*3+1]=table.charAt(binary[i]&0xf);
                	
                }

                String text=result.getText();//new String(buffer);
                text=text.replace("\r\n", "\n");
                text=text.replace('\r', '\n');
                
                String[] separated = text.split("\n");
                if(separated[0].startsWith("SiSTEM")) {
                	int datacount=Integer.parseInt(separated[1]);
                	for (int j = 0; j < datacount; j++) {
                		String dataentry=separated[2+j];
                		String[] fields=dataentry.split(",");
                		
						int mode=Integer.parseInt(fields[0]);
						switch (mode) {
						case 1:
							float camerax=Float.parseFloat(fields[1]);
							float cameray=Float.parseFloat(fields[2]);
							float cameraz=Float.parseFloat(fields[3]);
							float lookatx=Float.parseFloat(fields[4]);
							float lookaty=Float.parseFloat(fields[5]);
							float lookatz=Float.parseFloat(fields[6]);
							Intent intent=new Intent(QRActivity.this, MapActivity.class);
							intent.putExtra("pos", new float[] {camerax,cameray,cameraz,lookatx,lookaty,lookatz});
							startActivity(intent);
							break;
						case 2:
							String dname=fields[1];
							intent=new Intent(QRActivity.this,EventsActivity.class);
							for (int k = 0; k <  MyApplication.indicium.group_table.length; k++) {
								if(MyApplication.indicium.group_table[k].name==dname) {
									intent.putExtra("eventid", k);
									startActivity(intent);
									break;
								}
							}
							break;

						}
						
					}
                }
                else {
                    Toast.makeText(getApplicationContext(), "これはSiSTEMのQRコードではありません。", Toast.LENGTH_SHORT).show();
                }
                
                
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "QRコードが見つかりませんでした。", Toast.LENGTH_SHORT).show();
            }

            //timerHandler.post(timerRunnable);
        }
    };
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
