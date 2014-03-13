package com.nakedferret.simplepass;

public interface IFragListener {

	void onVaultSelected(Long vaultId);

	void onAccountSelected(Long accountId);

	void cancel();

	void requestCreateVault();

	void createVault(String name, String password, int iterations);

	void requestCreateAccount(Long vaultId);

	void createAccount(Long vaultId, Long categoryId, String name,
			String username, String password);

	void onKeyboardChanged();

	void unlockVault(Long vaultId, String password);
}
