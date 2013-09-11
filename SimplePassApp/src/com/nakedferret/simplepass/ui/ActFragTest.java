package com.nakedferret.simplepass.ui;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.ApplicationSimplePass;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.IUIListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;

@EActivity(R.layout.act_frag_test)
public class ActFragTest extends ActFloating implements IUIListener,
		IFragListener {

	@App
	ApplicationSimplePass app;

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
	public void requestCreateAccount() {
		showFragCreateAccount();
	}

	@Override
	public void onAccountCreated(Uri vaultUri) {
		showFragListAccount(vaultUri);
	}

	@Override
	public void onAccountSelected(Uri uri) {
		Utils.log(this, "account selected: " + uri.toString());
	}

	@Override
	public void onCancel() {
		FragmentManager m = getSupportFragmentManager();
		m.popBackStack();
	}

	private void showFragListVault() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragListVault());
		t.commit();
	}

	private void showFragListAccount(Uri vault) {
		FragmentManager m = getSupportFragmentManager();
		m.popBackStack(); // Remove the password input fragment
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, FragListAccount.newInstance(vault));
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragPassInput(Uri vaultUri) {
		fragPasswordInput = FragPassInput.newInstance(vaultUri);

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, fragPasswordInput);
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragCreateAccount() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragCreateAccount_());
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragCreateVault() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragCreateVault_());
		t.addToBackStack(null);
		t.commit();

	}

}
