package com.nakedferret.simplepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ServicePassword;
import com.nakedferret.simplepass.ServicePassword_;
import com.nakedferret.simplepass.Utils;
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
	private FragPassInput fragPasswordInput;
	private boolean vaultChecked;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Utils.log(this, "created");
	}

	@AfterViews
	void initializeInterface() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragListVault());
		t.commit();

		receiver = new BroadcastVaultReceiver(this, this);
		vaultChecked = false;
	}

	@Override
	public void onVaultCreated(Uri uri) {

	}

	@Override
	public void onVaultSelected(Uri uri) {
		Intent i = new Intent(this, ServicePassword_.class);
		i.setAction(ServicePassword.UNLOCK_VAULT);
		i.putExtra(ServicePassword.EXTRA_VAULT_URI, uri.toString());
		startService(i);

	}

	@Override
	// The user entered the correct password
	public void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {
		FragmentManager m = getSupportFragmentManager();

		if (!vaultChecked)
			m.popBackStack(); // Remove the password input fragment

		FragmentTransaction t = m.beginTransaction();

		t.replace(R.id.fragmentContainer, FragListAccount.newInstance(vault));
		t.addToBackStack(null);
		t.commit();
		vaultChecked = true;
	}

	@Override
	public void onVaultLocked(Uri vault) {
		if (!vaultChecked) {
			FragmentManager m = getSupportFragmentManager();
			FragmentTransaction t = m.beginTransaction();

			t.replace(R.id.fragmentContainer, FragPassInput.newInstance(vault));
			t.addToBackStack(null);
			t.commit();
		}

		if (fragPasswordInput != null)
			fragPasswordInput.onVaultLocked(vault);

		vaultChecked = true;
	}

	@Override
	public void onAccountCreated(Uri uri) {
	}

	@Override
	public void onAccountSelected(Uri uri) {

	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		Utils.log(this, "saved instance state");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Utils.log(this, " destroyed");
	}

}
