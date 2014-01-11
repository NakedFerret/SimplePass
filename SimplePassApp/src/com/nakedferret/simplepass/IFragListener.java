package com.nakedferret.simplepass;

import android.net.Uri;

public interface IFragListener {

	void onVaultSelected(Uri vault);

	void onAccountSelected(Uri account);

	void cancel();

	void requestCreateVault();

	void requestCreateAccount(Uri vaultUri);
	
	void onKeyboardChanged();
}
