package com.nakedferret.simplepass;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.inputmethodservice.Keyboard;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.SystemService;
import com.nakedferret.simplepass.ServiceSimplePass.LocalBinder;

@EService
public class ServiceKeyboard extends ServiceSimpleKeyboard implements
		ServiceConnection {

	@SystemService
	InputMethodManager inputMethodManager;

	private ActivityListener activityListener = new ActivityListener();
	static final String FILTER = "activity_listener";
	static final String ACTIVITY_EVENT_EXTRA = "activity_event";

	private static final String ACCOUNT_SELECTED = "acct_selected";
	private static final String USERNAME_EXTRA = "user";
	private static final String PASSWORD_EXTRA = "password";

	private static final int USERNAME_BUTTON = 1;
	private static final int PASSWORD_BUTTON = 2;
	private static final int DONE_BUTTON = 3;
	private static final int CLEAR_BUTTON = 4;
	private static final int CANCEL_BUTTON = 5;

	private String username;
	private String password;
	private boolean fillInMode;

	private ServiceSimplePass service = null;

	private Keyboard fillInKeyboard;

	@Override
	public void onCreate() {
		super.onCreate();
		LocalBroadcastManager.getInstance(this).registerReceiver(
				activityListener, new IntentFilter(FILTER));
		// TODO: bind to ServicePassword
	}

	@Override
	public void onInitializeInterface() {
		super.onInitializeInterface();
		fillInKeyboard = new Keyboard(this, R.xml.fill_in);
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);

		if (fillInMode) {
			setKeyboard(fillInKeyboard);
		} else {
			requestHideSelf(0);
			inputMethodManager.showInputMethodPicker();
		}
	}

	@Override
	public void onKey(int code, int[] keyCodes) {

		switch (code) {
		case USERNAME_BUTTON:
			getCurrentInputConnection().commitText(username, 1);
			break;
		case PASSWORD_BUTTON:
			getCurrentInputConnection().commitText(password, 1);
			break;
		case DONE_BUTTON:
			cancelPasswordInput();
			inputMethodManager.showInputMethodPicker();
			break;
		case CANCEL_BUTTON:
			cancelPasswordInput();
			break;
		case CLEAR_BUTTON:
			getCurrentInputConnection().deleteSurroundingText(1000, 1000);
			break;
		default:
			super.onKey(code, keyCodes);
		}
	}

	class ActivityListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent i) {
			String event = (String) i.getExtras().get(ACTIVITY_EVENT_EXTRA);

			if (ACCOUNT_SELECTED.equals(event)) {
				enterFillInMode(i);
			}
		}

	}

	private void enterFillInMode(Intent intent) {
		username = intent.getExtras().getString(USERNAME_EXTRA);
		password = intent.getExtras().getString(PASSWORD_EXTRA);
		fillInMode = true;
	}

	private void cancelPasswordInput() {
		username = null;
		password = null;
		fillInMode = false;
		requestHideSelf(0);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		LocalBinder localBinder = (LocalBinder) binder;
		service = localBinder.getService();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (service != null)
			unbindService(this);
	}

	public static void alertAccountSelected(Context c, Account a) {
		Intent i = new Intent(ServiceKeyboard.FILTER);
		i.putExtra(ServiceKeyboard.ACTIVITY_EVENT_EXTRA, ACCOUNT_SELECTED);
		i.putExtra(USERNAME_EXTRA, a.decryptedUsername);
		i.putExtra(PASSWORD_EXTRA, a.decryptedPassword);
		LocalBroadcastManager.getInstance(c).sendBroadcast(i);
	}

}
