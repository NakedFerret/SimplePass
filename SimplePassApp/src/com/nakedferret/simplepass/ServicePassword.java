package com.nakedferret.simplepass;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.EService;

@EService
public class ServicePassword extends Service implements IWorkerListener {

	private Map<Uri, Vault> unlockedVaults = new HashMap<Uri, Vault>();

	public class LocalBinder extends Binder {
		ServicePassword getService() {
			return ServicePassword.this;
		}
	}

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder();
	}

	@Override
	public boolean unlockVault(Uri uri, String pass) {

		Vault v = Vault.load(Vault.class, ContentUris.parseId(uri));

		Utils.log(this, "Got the vault: " + v.name);
		if (v.unlock(pass)) {
			Utils.log(this, "vault unlock successful");
			unlockedVaults.put(uri, v);
			return true;
		}

		Utils.log(this, "vault unlock failed");
		return false;
	}

	@Override
	public Uri createVault(String name, String password, int iterations) {
		Vault v = Vault.createVault(name, password, iterations);
		v.save();
		return ContentProvider.createUri(Vault.class, v.getId());
	}

	@Override
	public Uri createAccount(Uri vault, Uri group, String name,
			String username, String password) {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public void lockVault(Uri vault) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVaultUnlocked(Uri vault) {
		return false;
		// TODO Auto-generated method stub

	}

}
