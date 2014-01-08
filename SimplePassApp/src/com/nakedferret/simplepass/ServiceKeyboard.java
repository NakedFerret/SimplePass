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

import com.nakedferret.simplepass.ServicePassword.LocalBinder;
import com.nakedferret.simplepass.ui.ActFragTest_;

public class ServiceKeyboard extends ServiceSimpleKeyboard implements
		ServiceConnection {

	private ActivityListener activityListener = new ActivityListener();
	static final String FILTER = "activity_listener";
	static final String ACTIVITY_EVENT_EXTRA = "activity_event";

	private static final String ACCOUNT_SELECTED = "acct_selected";
	private static final String USERNAME_EXTRA = "user";
	private static final String PASSWORD_EXTRA = "password";

	private static final int USERNAME_BUTTON = 1;
	private static final int PASSWORD_BUTTON = 2;
	private static final int CLEAR_BUTTON = 3;
	private static final int BACK_BUTTON = 4;

	public static final String INPUT_ID_EXTRA = "input_extra";
	public static final String INPUT_ID = "com.nakedferret.simplepass.id";
	public static final String LOGGED_IN = "logged_in";

	private String username;
	private String password;
	private boolean fillInMode;

	private ServicePassword service = null;

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

		if (!editorIsLocal(info) && !fillInMode) {

			showAccountSelection();
			requestHideSelf(0);

		} else if (fillInMode) {

			setKeyboard(fillInKeyboard);
		}
	}

	private boolean editorIsLocal(EditorInfo info) {
		if (info.extras == null)
			return false;

		String editorId = info.extras.getString(INPUT_ID_EXTRA);
		return INPUT_ID.equals(editorId);
	}

	@Override
	public void onKey(int code, int[] keyCodes) {

		switch (code) {
		case USERNAME_BUTTON:
		case PASSWORD_BUTTON:
			commitTextAndClear(code);
			if (username != null || password != null)
				break;
		case BACK_BUTTON:
			exitFillInMode();
			break;
		case CLEAR_BUTTON:
			getCurrentInputConnection().deleteSurroundingText(1000, 1000);
			break;
		default:
			super.onKey(code, keyCodes);
		}
	}

	private void commitTextAndClear(int code) {
		String text = (code == USERNAME_BUTTON) ? username : password;
		if (text == null)
			return;

		getCurrentInputConnection().commitText(text, 1);
		if (code == USERNAME_BUTTON)
			username = null;
		else
			password = null;
	}

	class ActivityListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent i) {
			String event = (String) i.getExtras().get(ACTIVITY_EVENT_EXTRA);

			if (LOGGED_IN.equals(event))
				showAccountSelection();
			else if (ACCOUNT_SELECTED.equals(event)) {
				enterFillInMode(i);
			}
		}

	}

	private void enterFillInMode(Intent intent) {
		username = intent.getExtras().getString(USERNAME_EXTRA);
		password = intent.getExtras().getString(PASSWORD_EXTRA);
		fillInMode = true;
	}

	private void exitFillInMode() {
		username = null;
		password = null;
		fillInMode = false;
		setNextKeyboard();
		requestHideSelf(0);
	}

	private void showAccountSelection() {
		Intent i = new Intent(getApplicationContext(), ActFragTest_.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
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
