package com.nakedferret.simplepass;


// A background worker that deals with Vaults and Accounts needs to implement these
public interface IWorker {

	Long createVault(String name, String password, int iterations);

	Long createAccount(Long vaultId, Long categoryId, String name, String username,
			String password);

	// Returns True if Vault successfully unlocked
	boolean unlockVault(Long vaultId, String password);

	void lockVault(Long vaultId);

	// Check to see if vault is unlocked or not
	boolean isVaultUnlocked(Long vaultId);

	Account getDecryptedAccount(Long vaultId);
}
