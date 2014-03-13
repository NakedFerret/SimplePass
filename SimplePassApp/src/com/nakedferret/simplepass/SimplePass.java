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
import com.nakedferret.simplepass.ServiceSimplePass.LocalBinder;
import com.nakedferret.simplepass.VaultManager.ResultListener;

@EApplication
public class SimplePass extends Application implements ServiceConnection {

	public VaultManager vaultManager;

	@Override
	public void onCreate() {
		super.onCreate();
		setStrictMode();
		bindService();
		initializeDB();
	}

	private void setStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().build());
	}

	private void bindService() {
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

		// TODO: remove this test code
		// Testing FragImportEditAccount
		ActiveAndroid
				.execSQL("insert or ignore into category (_id, name) values (2, 'Social')");

		JoinView jv = JoinView.build("account_w_cat", Account.class)
				.inner(Category.class).onIdAnd("category").create();
		Cache.addView(jv);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LocalBinder binder = (LocalBinder) service;
		this.vaultManager = binder.getService().vaultManager;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.vaultManager = null;
	}

	public void onAccountSelected(Long accountId) {

		final ResultListener<Account> l = new ResultListener<Account>() {
			@Override
			public void onResult(Account a) {
				ServiceKeyboard.alertAccountSelected(SimplePass.this, a);
			}
		};

		vaultManager.getDecryptedAccount(accountId, l);
	}
}
