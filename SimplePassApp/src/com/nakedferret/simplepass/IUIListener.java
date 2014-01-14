package com.nakedferret.simplepass;


public interface IUIListener {

	// Vault created
	void onVaultCreated(Long vaultId);

	// Alert a vault was unlocked
	void onVaultUnlocked(Long vaultId, byte[] key, byte[] iv);

	// Alert wrong password used to unlock
	void onVaultUnlockedFailed(Long vaultId);

	// Alert a vault was locked
	void onVaultLocked(Long vaultId);

	// Account created
	void onAccountCreated(Long accountId);

}