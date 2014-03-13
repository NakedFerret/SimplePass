package com.nakedferret.simplepass;


public interface IFragListener {

	void onVaultSelected(Long vaultId);

	void onAccountSelected(Long accountId);

	void cancel();

	void requestCreateVault();

	void requestCreateAccount(Long vaultId);
	
	void onKeyboardChanged();

	void unlockVault(Long vaultId, String password);
}
