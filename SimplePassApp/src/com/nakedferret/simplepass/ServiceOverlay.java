package com.nakedferret.simplepass;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.SystemService;
import com.nakedferret.simplepass.OverlayViewManager.OnOverlayTriggerListener;
import com.nakedferret.simplepass.ui.ActPasswordSelect_;

@EService
public class ServiceOverlay extends Service implements OnOverlayTriggerListener {

	static final int NOTIFICATION_ID = 1;
	static final String STOP_ACTION = "stop";

	@SystemService
	WindowManager windowManager;

	@SystemService
	InputMethodManager inputMethodManager;

	private OverlayViewManager overlayViewManager;

	@Override
	public void onCreate() {
		super.onCreate();
		startForeground(NOTIFICATION_ID, foregroundNotification());

		overlayViewManager = new OverlayViewManager(this);
		overlayViewManager.showViews();
		overlayViewManager.setOnOverlayTriggeredListener(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Android may send an empty intent to restart service
		if (intent != null && STOP_ACTION.equals(intent.getAction())) {
			stopSelf();
		}

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true); // True means remove the notification set on the
								// call to startForeground
		overlayViewManager.removeViews();
	}

	private Notification foregroundNotification() {
		String title = getResources().getString(
				R.string.notification_overlay_title);

		String text = getResources().getString(
				R.string.notification_overlay_text);

		Notification n = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
				.setContentText(text)
				.setContentIntent(hideOverlayActivityPendingIntent()).build();

		return n;
	}

	private PendingIntent hideOverlayActivityPendingIntent() {

		Intent i = new Intent(getApplicationContext(), ServiceOverlay_.class);
		i.setAction(STOP_ACTION);
		return PendingIntent
				.getService(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void OnOverlayTriggered() {
		Intent i = new Intent(getApplicationContext(), ActPasswordSelect_.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

}
