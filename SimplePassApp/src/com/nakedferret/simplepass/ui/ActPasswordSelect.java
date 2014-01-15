package com.nakedferret.simplepass.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.IUIListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;

@EActivity(R.layout.fragment_container)
public class ActPasswordSelect extends ActFloating implements IUIListener,
		IFragListener {

	@App
	SimplePass app;

	private FragPassInput fragPasswordInput;
	private Long selectedAccountId;

	@Override
	protected void onStart() {
		super.onStart();
		app.attachUIListener(this);
	}

	@AfterViews
	void initializeInterface() {
		showFragListVault();
	}

	@Override
	public void onVaultSelected(Long vaultId) {
		if (app.isVaultUnlocked(vaultId))
			showFragListAccount(vaultId);
		else
			showFragPassInput(vaultId);
	}

	@Override
	public void requestCreateVault() {
		showFragCreateVault();
	}

	@Override
	public void onVaultCreated(Long vaultId) {
		cancel();
		showFragPassInput(vaultId);
	}

	@Override
	// The user entered the correct password
	public void onVaultUnlocked(Long vaultId, byte[] key, byte[] iv) {
		showFragListAccount(vaultId);
	}

	@Override
	public void onVaultUnlockedFailed(Long vaultId) {
		fragPasswordInput.onPasswordIncorrect();
	}

	@Override
	public void onVaultLocked(Long vaultId) {
		cancel();
		showFragListVault();
	}

	@Override
	public void requestCreateAccount(Long vaultId) {
		showFragCreateAccount(vaultId);
	}

	@Override
	public void onAccountCreated(Long vaultId) {
		// FragCreateAccount is currently showing, so pop it out to show
		// FragListAccount
		cancel();
	}

	@Override
	public void onAccountSelected(Long accountId) {
		selectedAccountId = accountId;
		showFragChangeKeyboard();
	}

	private void showFragChangeKeyboard() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragChangeKeyboard_());
		t.addToBackStack(null);
		t.commit();
	}

	@Override
	public void cancel() {
		getSupportFragmentManager().popBackStack();
	}

	private void showFragListVault() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragListVault_());
		t.commit();
	}

	private void showFragCreateVault() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, new FragCreateVault_());
		t.addToBackStack(null);
		t.commit();

	}

	private void showFragPassInput(Long vaultId) {
		fragPasswordInput = FragPassInput_.builder().vaultId(vaultId).build();

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, fragPasswordInput);
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragListAccount(Long vaultId) {

		Fragment f = FragListAccount_.builder().vaultId(vaultId).build();

		FragmentManager m = getSupportFragmentManager();
		m.popBackStack(); // Remove the password input fragment
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragCreateAccount(Long vaultId) {
		Fragment f = FragCreateAccount_.builder().vaultId(vaultId).build();

		FragmentManager m = getSupportFragmentManager();
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

	@Override
	public void onKeyboardChanged() {
		app.onAccountSelected(selectedAccountId);
		finish();
	}

}
