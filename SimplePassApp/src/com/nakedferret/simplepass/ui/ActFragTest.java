package com.nakedferret.simplepass.ui;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ui.BroadcastVaultReceiver.OnVaultInteractionListerner;
import com.nakedferret.simplepass.ui.FragCreateAccount.OnAccountCreatedListener;
import com.nakedferret.simplepass.ui.FragCreateVault.OnVaultCreatedListener;
import com.nakedferret.simplepass.ui.FragListAccount.OnAccountSelectedListener;
import com.nakedferret.simplepass.ui.FragListVault.OnVaultSelectedListener;

@EActivity(R.layout.act_frag_test)
public class ActFragTest extends ActFloating implements OnVaultCreatedListener,
		OnAccountCreatedListener, OnAccountSelectedListener,
		OnVaultSelectedListener, OnVaultInteractionListerner {

	private BroadcastVaultReceiver receiver;

	private ProgressDialog dialog;

	@AfterViews
	void initializeInterface() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragListVault());
		t.commit();
	}

	@Override
	public void onVaultCreated(Uri uri) {

	}

	@Override
	public void onVaultSelected(Uri uri) {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, FragPassInput.newInstance(uri));
		t.addToBackStack(null);
		t.commit();
	}

	@Override
	// The user entered the correct password
	public void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {
		FragmentManager m = getSupportFragmentManager();

		m.popBackStack(); // Remove the password input fragment

		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, FragListAccount.newInstance(vault));
		t.addToBackStack(null);
		t.commit();
	}

	@Override
	// The user pressed the back button
	public void onVaultLocked(Uri vault) {

	}

	@Override
	public void onAccountCreated(Uri uri) {

	}

	@Override
	public void onAccountSelected(Uri uri) {

	}

	private void replaceFragment(Fragment f, boolean addToStack) {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		if (addToStack)
			t.addToBackStack(null);
		t.commit();
	}

}
