package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.SystemService;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.R;

@EFragment(R.layout.frag_change_keyboard)
public class FragChangeKeyboard extends Fragment {

	@SystemService
	InputMethodManager inputMethodManager;

	private IFragListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (IFragListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IFragListener");
		}
	}

	@Click(R.id.changeKeyboardButton)
	void onChangeKeyboard() {
		inputMethodManager.showInputMethodPicker();
	}

	@Click(R.id.doneButton)
	void onDone() {
		if (mListener != null)
			mListener.onKeyboardChanged();
	}
}
