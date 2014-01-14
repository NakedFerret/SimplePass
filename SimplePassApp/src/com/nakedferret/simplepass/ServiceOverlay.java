package com.nakedferret.simplepass;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.nakedferret.simplepass.OverlayViewManager.OnOverlayTriggerListener;
import com.nakedferret.simplepass.ui.ActPasswordSelect_;

@EService
public class ServiceOverlay extends Service implements OnOverlayTriggerListener {

	static final int NOTIFICATION_ID = 1;
	static final String STOP_ACTION = "stop";
	static final String START_ACTION = "start";

	private static boolean OVERLAY_RUNNING = false;

	@SystemService
	WindowManager windowManager;

	@StringRes
	String notificationOverlayTitle, notificationOverlayText;

	private OverlayViewManager overlayViewManager;
	private Notification notification;

	@AfterInject
	void initialize() {
		overlayViewManager = new OverlayViewManager(this, this);
		notification = getForegroundNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Android may send an empty intent to restart service
		if (intent == null)
			return START_STICKY;

		if (START_ACTION.equals(intent.getAction())) {
			showOverlay();
		} else if (STOP_ACTION.equals(intent.getAction())) {
			hideOverlay();
		}

		return START_STICKY;
	}

	private void showOverlay() {
		startForeground(NOTIFICATION_ID, notification);
		overlayViewManager.showViews();
		OVERLAY_RUNNING = true;
	}

	private void hideOverlay() {
		stopForeground(true);
		overlayViewManager.removeViews();
		OVERLAY_RUNNING = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		hideOverlay();
	}

	private Notification getForegroundNotification() {
		Intent i = new Intent(this, ServiceOverlay_.class);
		i.setAction(STOP_ACTION);
		PendingIntent hideOverlayIntent = PendingIntent.getService(this, 0, i,
				PendingIntent.FLAG_ONE_SHOT);

		Notification n = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(notificationOverlayTitle)
				.setContentText(notificationOverlayText)
				.setContentIntent(hideOverlayIntent).build();

		return n;
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

	public static boolean overlayRunning() {
		return OVERLAY_RUNNING;
	}

	public static void stopOverlay(Context c) {
		Intent i = new Intent(c, ServiceOverlay_.class);
		i.setAction(STOP_ACTION);
		c.startService(i);
	}

	public static void startOverlay(Context c) {
		Intent i = new Intent(c, ServiceOverlay_.class);
		i.setAction(START_ACTION);
		c.startService(i);
	}
}
