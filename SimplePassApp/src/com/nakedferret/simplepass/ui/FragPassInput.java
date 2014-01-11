package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;

@EFragment(R.layout.frag_pass_input)
public class FragPassInput extends DialogFragment {

	@ViewById
	EditText passwordInput;

	@ViewById
	Button cancelButton, unlockButton;

	@ViewById
	TextView infoText;

	@ViewById
	ProgressBar progressIndicator;

	@App
	SimplePass app;

	@FragmentArg
	String vaultUriString;

	private IFragListener mListener;
	private Uri vaultUri;

	public FragPassInput() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (IFragListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnVaultInteractionListerner");
		}
	}

	@AfterViews
	void init() {
		vaultUri = Uri.parse(vaultUriString);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.enterVaultPasswordTitle);
	}

	@AfterViews
	void initUI() {
		infoText.setVisibility(View.INVISIBLE);
		progressIndicator.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Click(R.id.cancelButton)
	void cancel() {
		if (mListener != null)
			mListener.onCancel();
	}

	@Click(R.id.unlockButton)
	void unlock() {
		String password = passwordInput.getText().toString();
		app.unlockVault(vaultUri, password);
		showProgress();
	}

	private void showProgress() {
		passwordInput.setEnabled(false);
		unlockButton.setEnabled(false);
		infoText.setText("Unlocking Vault...");
		infoText.setVisibility(View.VISIBLE);
		progressIndicator.setVisibility(View.VISIBLE);
	}

	public void onPasswordIncorrect() {
		passwordInput.setEnabled(true);
		unlockButton.setEnabled(true);
		infoText.setText("Wrong Password!");
		progressIndicator.setVisibility(View.INVISIBLE);
	}

}
