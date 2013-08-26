package com.nakedferret.simplepass;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.EService;

@EService
public class ServicePassword extends Service {

	public static final String ACTION = "password";
	public static final IntentFilter FILTER = new IntentFilter(ACTION);

	public ServicePassword() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
