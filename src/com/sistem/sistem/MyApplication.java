package com.sistem.sistem;


import com.deploygate.sdk.DeployGate;

import android.app.Application;
import android.graphics.Color;
public class MyApplication extends Application {
	public static anami.Indicium indicium;
    @Override
    public void onCreate() {
        super.onCreate();
        DeployGate.install(this);
        indicium=new anami.Indicium(getApplicationContext().getResources().openRawResource(R.raw.data_anami));
        
    }

	//"趣味研", "食品", "企画", "バンド", "部活", "本部", "その他"
	public static int[] colortable=new int[] {Color.CYAN, Color.RED,0xffff8000,0xffffff00,0xff80ff80,0xff0000ff,Color.GRAY,};
}