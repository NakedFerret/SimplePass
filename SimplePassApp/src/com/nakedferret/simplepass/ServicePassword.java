package com.nakedferret.simplepass;

import com.googlecode.androidannotations.annotations.EService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

@EService
public class ServicePassword extends Service {

	public ServicePassword() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
