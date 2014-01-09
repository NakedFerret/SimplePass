package com.nakedferret.simplepass.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.IUIListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;

@EActivity(R.layout.act_frag_password_select)
public class ActPasswordSelect extends ActFloating implements IUIListener,
		IFragListener {

	@App
	SimplePass app;

	private FragPassInput fragPasswordInput;

	@AfterViews
	void initializeInterface() {
		app.attachUIListener(this);
		showFragListVault();
	}

	@Override
	public void onVaultSelected(Uri vaultUri) {
		if (app.isVaultUnlocked(vaultUri))
			showFragListAccount(vaultUri);
		else
			showFragPassInput(vaultUri);
	}

	@Override
	public void requestCreateVault() {
		showFragCreateVault();
	}

	@Override
	public void onVaultCreated(Uri vaultUri) {
		showFragListAccount(vaultUri);
	}

	@Override
	// The user entered the correct password
	public void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {
		
		showFragListAccount(vault);
	}

	@Override
	public void onVaultUnlockedFailed(Uri vault) {
		fragPasswordInput.onPasswordIncorrect();
	}

	@Override
	public void onVaultLocked(Uri vault) {
		// Can't think of when this will be called...
	}

	@Override
	public void requestCreateAccount(Uri vaultUri) {
		showFragCreateAccount(vaultUri);
	}

	@Override
	public void onAccountCreated(Uri vaultUri) {
		// FragCreateAccount is currently showing, so pop it out to show FragListAccount
		FragmentManager m = getFragmentManager();
		m.popBackStack();
	}

	@Override
	public void onAccountSelected(Uri accountUri) {
		app.onAccountSelected(accountUri);
		finish();
	}

	@Override
	public void onCancel() {
		FragmentManager m = getFragmentManager();
		m.popBackStack();
	}

	private void showFragListVault() {
		FragmentManager m = getFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragListVault_());
		t.commit();
	}

	private void showFragCreateVault() {
		FragmentManager m = getFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragCreateVault_());
		t.addToBackStack(null);
		t.commit();

	}

	private void showFragPassInput(Uri vaultUri) {
		fragPasswordInput = FragPassInput_.builder()
				.vaultUriString(vaultUri.toString()).build();

		FragmentManager m = getFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, fragPasswordInput);
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragListAccount(Uri vault) {

		Fragment f = FragListAccount_.builder()
				.vaultUriString(vault.toString()).build();

		FragmentManager m = getFragmentManager();
		m.popBackStack(); // Remove the password input fragment
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragCreateAccount(Uri vaultUri) {
		Fragment f = FragCreateAccount_.builder()
				.vaultUriString(vaultUri.toString()).build();

		FragmentManager m = getFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		app.detachUIListener();
	}

}
