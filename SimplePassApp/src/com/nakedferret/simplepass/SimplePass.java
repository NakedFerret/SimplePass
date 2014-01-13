package com.nakedferret.simplepass;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.StrictMode;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.JoinView;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.ServicePassword.LocalBinder;

@EApplication
public class SimplePass extends Application implements ServiceConnection {

	private IWorkerListener worker;
	private IUIListener uiListener;

	@Override
	public void onCreate() {
		super.onCreate();
		setStrictMode();
		bindServicePassword();
		initializeDB();
	}

	private void setStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().build());
	}

	private void bindServicePassword() {
		Intent serviceIntent = ServicePassword_.intent(this).get();
		startService(serviceIntent);
		bindService(serviceIntent, this, 0);
	}

	@Background
	void initializeDB() {
		ActiveAndroid.initialize(this);
		// Insert default Category
		ActiveAndroid
				.execSQL("insert or ignore into category (_id, name) values (1, 'Default')");

		JoinView jv = JoinView.build("account_w_cat", Account.class)
				.inner(Category.class).onIdAnd("category").create();
		Cache.addView(jv);
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

		Vault v = Vault.load(Vault.class, ContentUris.parseId(vaultUri));
		byte[] key = Utils.getKey(password, v.salt, v.iterations);
		onVaultUnlocked(vaultUri, key, v.iv);
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

	public void onAccountSelected(Uri accountUri) {
		getSelectedAccountForKeyboard(accountUri);
	}

	@Background
	void getSelectedAccountForKeyboard(Uri accountUri) {
		Account a = worker.getDecryptedAccount(accountUri);
		alertKeyboardAccountSelected(a);
	}

	@UiThread
	void alertKeyboardAccountSelected(Account a) {
		ServiceKeyboard.alertAccountSelected(this, a);
	}

	public boolean isVaultUnlocked(Uri vaultUri) {
		return worker.isVaultUnlocked(vaultUri);
	}

}
