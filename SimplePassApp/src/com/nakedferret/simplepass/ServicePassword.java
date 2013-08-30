package com.nakedferret.simplepass;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.UiThread;

@EService
public class ServicePassword extends Service {

	public static final String VAULT_UNLOCKED = "vault_unlocked";
	public static final String VAULT_LOCKED = "vault_locked";

	public static final String EXTRA_VAULT_URI = "vault_uri";
	public static final String EXTRA_VAULT_KEY = "vault_key";
	public static final String EXTRA_VAULT_IV = "vault_iv";

	public static final String UNLOCK_VAULT = "unlock_vault";
	public static final String LOCK_VAULT = "lock_vault";

	public static final String EXTRA_VAULT_PASSWORD = "vault_pass";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		handleIntent(i);
		return START_STICKY;
	}

	void handleIntent(Intent i) {
		String uriString = i.getStringExtra(EXTRA_VAULT_URI);
		Uri uri = Uri.parse(uriString);

		if (UNLOCK_VAULT.equals(i.getAction())) {
			String pass = i.getStringExtra(EXTRA_VAULT_PASSWORD);
			unlockVault(uri, pass);
		} else if (LOCK_VAULT.equals(i.getAction())) {
			lockVault(uri);
		}
	}

	@UiThread
	void dispatchIntent(String action) {

	}

	@Background
	void unlockVault(Uri uri, String pass) {
		// TODO: implement unlock vault
		// generate the key, check the hash,
		// and send an intent that the vault was unlocked if successful and
		// locked if failed

	}

	private void lockVault(Uri uri) {
		// TODO: implement lock vault
		// null the key and iv, and send an intent that the vault was locked
	}

}
