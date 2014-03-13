package com.nakedferret.simplepass.ui;

import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;
import com.nakedferret.simplepass.VaultManager.ResultListener;
import com.nakedferret.simplepass.VaultManager.VaultLockListener;
import com.nakedferret.simplepass.utils.Utils;

@EActivity(R.layout.fragment_container)
public class ActPasswordSelect extends ActFloating implements IFragListener,
		VaultLockListener {

	@App
	SimplePass app;

	@NonConfigurationInstance
	boolean configurationChanged = false;

	private FragPassInput fragPasswordInput;
	private Long selectedAccountId;

	@Override
	protected void onStart() {
		super.onStart();
	}

	@AfterViews
	void initializeInterface() {
		if (!configurationChanged)
			showFragListVault();
	}

	@Override
	public void onVaultSelected(Long vaultId) {
		if (app.vaultManager.isVaultUnlocked(vaultId))
			showFragListAccount(vaultId);
		else
			showFragPassInput(vaultId);
	}

	@Override
	public void requestCreateVault() {
		showFragCreateVault();
	}

	@Override
	public void createVault(String name, String password, int iterations) {
		ResultListener<Long> l = new ResultListener<Long>() {
			@Override
			public void onResult(Long vaultId) {
				cancel();
				onVaultSelected(vaultId);
			}
		};

		app.vaultManager.createVault(name, password, iterations, l);
	}

	@Override
	public void unlockVault(final Long vaultId, String password) {
		final ResultListener<Boolean> l = new ResultListener<Boolean>() {

			@Override
			public void onResult(Boolean unlocked) {
				Utils.log(this, "result for vault unlock");
				if (unlocked)
					showFragListAccount(vaultId);
				else
					fragPasswordInput.onPasswordIncorrect();
			}
		};

		app.vaultManager.unlockVault(vaultId, password, l);
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
	public void createAccount(Long vaultId, Long categoryId, String name,
			String username, String password) {

		ResultListener<Long> l = new ResultListener<Long>() {

			@Override
			public void onResult(Long result) {
				cancel();
			}
		};

		app.vaultManager.createAccount(vaultId, categoryId, name, username,
				password, l);
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
	public Object onRetainCustomNonConfigurationInstance() {
		configurationChanged = true;
		return super.onRetainCustomNonConfigurationInstance();
	}

	@Override
	public void onKeyboardChanged() {
		app.onAccountSelected(selectedAccountId);
		finish();
	}

	public ResultListener<Long> getVaultCreateListener() {
		return new ResultListener<Long>() {

			@Override
			public void onResult(Long vaultId) {
				cancel();
				showFragPassInput(vaultId);
			}
		};
	}

}
