package com.nakedferret.simplepass;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.EService;

@EService
public class ServicePassword extends Service {

	public static final String VAULT_UNLOCKED = "vault_unlocked";
	public static final String VAULT_LOCKED = "vault_locked";

	public static final String EXTRA_VAULT_URI = "vault_uri";
	public static final String EXTRA_VAULT_KEY = "vault_key";
	public static final String EXTRA_VAULT_IV = "vault_iv";

	public ServicePassword() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
