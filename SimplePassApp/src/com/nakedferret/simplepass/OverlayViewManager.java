package com.nakedferret.simplepass;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class OverlayViewManager implements OnTouchListener {

	private Context context;
	private ImageView left, right;
	private WindowManager windowManager;
	private OnOverlayTriggerListener listener;

	public OverlayViewManager(Context c, OnOverlayTriggerListener listener) {
		this.context = c;
		this.listener = listener;
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

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
	}

	void setOnOverlayTriggeredListener(OnOverlayTriggerListener l) {
		listener = l;
	}

	void removeViews() {
		windowManager.removeView(left);
		windowManager.removeView(right);
	}

	interface OnOverlayTriggerListener {
		public void OnOverlayTriggered();
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
				listener.OnOverlayTriggered();

			break;
		}
		return false;
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

}
