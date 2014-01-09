package com.nakedferret.simplepass;

import android.net.Uri;

public interface IFragListener {

	void onVaultSelected(Uri vault);

	void onAccountSelected(Uri account);

	void onCancel();

	void requestCreateVault();

	void requestCreateAccount(Uri vaultUri);
	
	void onKeyboardChanged();
}
