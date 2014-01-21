package com.nakedferret.simplepass.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nakedferret.simplepass.ImportManager.MockAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.ViewHolder;

public class MockAccountAdapter extends ArrayAdapter<MockAccount> implements
		OnTouchListener {

	int layout = R.layout.listitem_mockaccount;

	public MockAccountAdapter(Context c, List<MockAccount> accounts) {
		super(c, 0, accounts);
	}

	public MockAccountAdapter(Context c) {
		super(c, 0, new ArrayList<MockAccount>());
	}

	@Override
	public View getView(int position, View r, ViewGroup parent) {
		if (r == null) {
			r = LayoutInflater.from(getContext())
					.inflate(layout, parent, false);
		}

		TextView text1 = ViewHolder.get(r, R.id.text1);
		TextView text2 = ViewHolder.get(r, R.id.text2);
		ImageButton showInfoButton = ViewHolder.get(r, R.id.showInfoButton);
		MockAccount a = getItem(position);

		text1.setText(a.getName() + "\n" + a.getCategory());
		text2.setText(a.getUsername() + "\n" + a.getPassword());
		showInfoButton.setOnTouchListener(this);

		return r;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		View p;
		TextView text2;

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			p = (View) v.getParent();
			text2 = ViewHolder.get(p, R.id.text2);
			text2.setVisibility(View.VISIBLE);
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			p = (View) v.getParent();
			text2 = ViewHolder.get(p, R.id.text2);
			text2.setVisibility(View.INVISIBLE);
			return true;
		default:
			return false;
		}
	}

}
