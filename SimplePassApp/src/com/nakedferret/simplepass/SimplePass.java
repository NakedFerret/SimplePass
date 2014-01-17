package com.nakedferret.simplepass;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.StrictMode;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.JoinView;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.ServiceSimplePass.LocalBinder;
import com.nakedferret.simplepass.utils.Utils;

@EApplication
public class SimplePass extends Application implements ServiceConnection {

	private IWorker worker;
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
		Intent serviceIntent = ServiceSimplePass_.intent(this).get();
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
		attachWorker(binder.getWorker());
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

	public void attachWorker(IWorker listener) {
		worker = listener;
	}

	public void detachWorker() {
		worker = null;
	}

	@Background
	public void createVault(String name, String password, int iterations) {
		Long vaultId = worker.createVault(name, password, iterations);
		onVaultCreated(vaultId);
	}

	@Background
	public void createAccount(Long vaultId, Long groupId, String name,
			String username, String password) {
		Long accountId = worker.createAccount(vaultId, groupId, name, username,
				password);
		onAccountCreated(accountId);
	}

	@Background
	public void unlockVault(Long vaultId, String password) {
		boolean unlocked = worker.unlockVault(vaultId, password);

		if (!unlocked) {
			onVaultUnlockedFailed(vaultId);
			return;
		}

		Vault v = Vault.load(Vault.class, vaultId);
		byte[] key = Utils.getKey(password, v.salt, v.iterations);
		onVaultUnlocked(vaultId, key, v.iv);
	}

	@Background
	public void lockVault(Long vaultId) {
		worker.lockVault(vaultId);
		onVaultLocked(vaultId);
	}

	@UiThread
	void onVaultCreated(Long vaultId) {
		uiListener.onVaultCreated(vaultId);
	}

	@UiThread
	void onVaultUnlocked(Long vaultId, byte[] key, byte[] iv) {
		uiListener.onVaultUnlocked(vaultId, key, iv);
	}

	@UiThread
	void onVaultUnlockedFailed(Long vaultId) {
		uiListener.onVaultUnlockedFailed(vaultId);
	}

	@UiThread
	void onVaultLocked(Long vaultId) {
		uiListener.onVaultLocked(vaultId);
	}

	@UiThread
	void onAccountCreated(Long accountId) {
		uiListener.onAccountCreated(accountId);
	}

	public void onAccountSelected(Long accountId) {
		getSelectedAccountForKeyboard(accountId);
	}

	@Background
	void getSelectedAccountForKeyboard(Long accountId) {
		Account a = worker.getDecryptedAccount(accountId);
		Utils.log(this, "account: " + a);
		alertKeyboardAccountSelected(a);
	}

	@UiThread
	void alertKeyboardAccountSelected(Account a) {
		ServiceKeyboard.alertAccountSelected(this, a);
	}

	public boolean isVaultUnlocked(Long vaultId) {
		return worker.isVaultUnlocked(vaultId);
	}

}
