package com.nakedferret.simplepass;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.nakedferret.simplepass.ui.ActPasswordSelect_;

@EBean
public class OverlayManager implements OnTouchListener {

	@RootContext
	Service service;

	@RootContext
	Context context;

	@SystemService
	WindowManager windowManager;

	@StringRes
	static String START_OVERLAY_ACTION, STOP_OVERLAY_ACTION;

	@StringRes
	String notificationOverlayTitle, notificationOverlayText;

	private static int NOTIFICATION_ID = 1;
	public static boolean OVERLAY_SHOWN = false;

	private BroadcastReceiver receiver;
	private ImageView left, right;

	@AfterInject
	void init() {
		left = new ImageView(context);
		left.setImageResource(R.drawable.ic_left_overlay);
		left.setOnTouchListener(this);
		left.setContentDescription("Left Overlay"); // For testing
		// TODO: Export string

		right = new ImageView(context);
		right.setImageResource(R.drawable.ic_right_overlay);
		right.setOnTouchListener(this);
		right.setContentDescription("Right Overlay"); // For testing
		// TODO: Export string

		receiver = new OverlayBroadcastReceiver();

		LocalBroadcastManager broadcastManager = LocalBroadcastManager
				.getInstance(context);

		broadcastManager.registerReceiver(receiver, new IntentFilter(
				START_OVERLAY_ACTION));
		broadcastManager.registerReceiver(receiver, new IntentFilter(
				STOP_OVERLAY_ACTION));

		context.registerReceiver(receiver,
				new IntentFilter(STOP_OVERLAY_ACTION));
	}

	private LayoutParams getOverlayWindowManagerParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		return params;
	}

	void showViews() {
		WindowManager.LayoutParams params = getOverlayWindowManagerParams();

		params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
		windowManager.addView(left, params);

		params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		windowManager.addView(right, params);

		OVERLAY_SHOWN = true;
		service.startForeground(NOTIFICATION_ID, getForegroundNotification());
	}

	void hideViews() {
		windowManager.removeView(left);
		windowManager.removeView(right);
		OVERLAY_SHOWN = false;
		service.stopForeground(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {

		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			left.setImageResource(R.drawable.ic_left_overlay_pressed);
			right.setImageResource(R.drawable.ic_right_overlay_pressed);
			break;
		case MotionEvent.ACTION_UP:
			left.setImageResource(R.drawable.ic_left_overlay);
			right.setImageResource(R.drawable.ic_right_overlay);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (touchWithinOtherView(v, e))
				onOverlayTriggered();
			break;
		}
		return false;
	}

	private void onOverlayTriggered() {
		Intent i = new Intent(context, ActPasswordSelect_.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	private boolean touchWithinOtherView(View v, MotionEvent e) {

		// Get X, Y relative to the screen
		int[] touchLoc = new int[2];
		v.getLocationOnScreen(touchLoc);
		touchLoc[0] += (int) e.getX(1);
		touchLoc[1] += (int) e.getY(1);

		// Prepare other view Rect
		View otherView = (v == left) ? right : left;
		Rect otherViewRec = new Rect();
		int[] otherViewLoc = new int[2];

		otherView.getDrawingRect(otherViewRec);
		otherView.getLocationOnScreen(otherViewLoc);
		otherViewRec.offset(otherViewLoc[0], otherViewLoc[1]);

		// Check if within
		return otherViewRec.contains(touchLoc[0], touchLoc[1]);
	}

	public static boolean overlayRunning() {
		return OVERLAY_SHOWN;
	}

	private Notification getForegroundNotification() {
		Intent i = new Intent(STOP_OVERLAY_ACTION);
		PendingIntent hideOverlayIntent = PendingIntent.getBroadcast(context,
				0, i, PendingIntent.FLAG_ONE_SHOT);

		Notification n = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(notificationOverlayTitle)
				.setContentText(notificationOverlayText)
				.setContentIntent(hideOverlayIntent).build();

		return n;
	}

	class OverlayBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction = intent.getAction();

			if (START_OVERLAY_ACTION.equals(intentAction)) {
				showViews();
			} else if (STOP_OVERLAY_ACTION.equals(intentAction)) {
				hideViews();
			}

		}

	}

	public static void startOverlay(Context c) {
		Intent i = new Intent(START_OVERLAY_ACTION);
		LocalBroadcastManager.getInstance(c).sendBroadcast(i);
	}

	public static void stopOverlay(Context c) {
		Intent i = new Intent(STOP_OVERLAY_ACTION);
		LocalBroadcastManager.getInstance(c).sendBroadcast(i);
	}

	public void onDestroy() {
		hideViews();
		LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
		context.unregisterReceiver(receiver);
	}

}
