package com.nakedferret.simplepass;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

@EService
public class ServiceSimplePass extends Service {

	@Bean
	OverlayManager overlayManager;

	@Bean
	VaultManager vaultManager;

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder();
	}

	public class LocalBinder extends Binder {
		ServiceSimplePass getService() {
			return ServiceSimplePass.this;
		}

		public IWorkerListener getWorker() {
			return vaultManager;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		overlayManager.onDestroy();
	}

}
