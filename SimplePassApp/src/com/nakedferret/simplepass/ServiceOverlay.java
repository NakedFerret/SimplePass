package com.nakedferret.simplepass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

@EService
public class ServiceOverlay extends Service {

	@Bean
	OverlayViewManager overlayViewManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		overlayViewManager.destroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
