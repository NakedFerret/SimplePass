package com.nakedferret.simplepass;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;
import com.nakedferret.simplepass.ServicePassword.LocalBinder;

@EApplication
public class ApplicationSimplePass extends Application implements
		ServiceConnection {

	private IWorkerListener worker;
	private IUIListener uiListener;

	@Override
	public void onCreate() {
		super.onCreate();
		Intent serviceIntent = ServicePassword_.intent(this).get();
		startService(serviceIntent);
		bindService(serviceIntent, this, 0);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LocalBinder binder = (LocalBinder) service;
		attachWorker(binder.getService());
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		detachWorker();
	}

	public void attachUIListener(IUIListener listener) {
		uiListener = listener;
	}

	public void detachUIListener() {
		uiListener = null;
	}

	public void attachWorker(IWorkerListener listener) {
		worker = listener;
	}

	public void detachWorker() {
		worker = null;
	}

	@Background
	public void createVault(String name, String password, int iterations) {
		Uri vaultUri = worker.createVault(name, password, iterations);
		onVaultCreated(vaultUri);
	}

	@Background
	public void createAccount(Uri vault, Uri group, String name,
			String username, String password) {
		Uri accountUri = worker.createAccount(vault, group, name, username,
				password);
		onAccountCreated(accountUri);
	}

	@Background
	public void unlockVault(Uri vaultUri, String password) {
		boolean unlocked = worker.unlockVault(vaultUri, password);

		if (!unlocked) {
			onVaultUnlockedFailed(vaultUri);
			return;
		}

		Cursor c = getContentResolver().query(vaultUri, null, null, null, null);
		ContentValues vault = Utils.getVault(c);
		byte[] salt = vault.getAsByteArray(Vault.COL_SALT);
		int iter = vault.getAsInteger(Vault.COL_ITERATIONS);
		byte[] key = Utils.getKey(password, salt, iter);
		byte[] iv = vault.getAsByteArray(Vault.COL_SALT);

		onVaultUnlocked(vaultUri, key, iv);
	}

	@Background
	public void lockVault(Uri vault) {
		worker.lockVault(vault);
		onVaultLocked(vault);
	}

	@UiThread
	void onVaultCreated(Uri vault) {
		uiListener.onVaultCreated(vault);
	}

	@UiThread
	void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {
		uiListener.onVaultUnlocked(vault, key, iv);
	}

	@UiThread
	void onVaultUnlockedFailed(Uri vaultUri) {
		uiListener.onVaultUnlockedFailed(vaultUri);
	}

	@UiThread
	void onVaultLocked(Uri vault) {
		uiListener.onVaultLocked(vault);
	}

	@UiThread
	void onAccountCreated(Uri account) {
		uiListener.onAccountCreated(account);
	}

	public boolean isVaultUnlocked(Uri vaultUri) {
		return worker.isVaultUnlocked(vaultUri);
	}

}
