package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ServicePassword;
import com.nakedferret.simplepass.ServicePassword_;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.ui.BroadcastVaultReceiver.OnVaultInteractionListerner;

@EFragment(R.layout.frag_pass_input)
public class FragPassInput extends DialogFragment implements
		OnVaultInteractionListerner {

	private static final String ARG_VAULT_URI = "vault_uri";

	@ViewById
	EditText passwordInput;

	@ViewById
	Button cancelButton, unlockButton;

	@ViewById
	TextView infoText;

	@ViewById
	ProgressBar progressIndicator;

	private OnVaultInteractionListerner mListener;
	private Uri vaultUri;

	public static FragPassInput newInstance(Uri uri) {
		FragPassInput fragment = new FragPassInput_();
		Bundle args = new Bundle();
		args.putString(ARG_VAULT_URI, uri.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public FragPassInput() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnVaultInteractionListerner) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnVaultInteractionListerner");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vaultUri = Uri.parse(getArguments().getString(ARG_VAULT_URI));
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
			mListener.onVaultLocked(vaultUri);
	}

	@Click(R.id.unlockButton)
	void unlock() {
		requestUnlockVault(passwordInput.getText().toString());
		showProgress();
	}

	private void requestUnlockVault(String password) {
		Intent i = new Intent(getActivity(), ServicePassword_.class);
		i.setAction(ServicePassword.UNLOCK_VAULT);
		i.putExtra(ServicePassword.EXTRA_VAULT_PASSWORD, password);
		i.putExtra(ServicePassword.EXTRA_VAULT_URI, vaultUri.toString());
		getActivity().startService(i);
	}

	private void showProgress() {
		passwordInput.setEnabled(false);
		unlockButton.setEnabled(false);
		infoText.setText("Unlocking Vault...");

		infoText.setVisibility(View.VISIBLE);
		progressIndicator.setVisibility(View.VISIBLE);
	}

	// TODO: broadcast receiver cannot access listener for some reason
	public void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {

	}

	public void onVaultLocked(Uri vault) {

	}

}
