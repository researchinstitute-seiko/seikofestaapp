package com.sistem.sistem.modelviewer2.Framework3D;

import android.app.Application;
import android.content.Context;

public class App extends Application{
	//Set the android:name attribute of your <application> tag in 
	//the AndroidManifest.xml to point to your new class, e.g. android:name="com.***.***.App"
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
    public static void setContext(Context context){
    	mContext=context;
    }

    public static Context getContext(){
        return mContext;
    }
}