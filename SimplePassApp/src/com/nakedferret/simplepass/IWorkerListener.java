package com.nakedferret.simplepass;

import android.net.Uri;

// A background worker that deals with Vaults and Accounts needs to implement these
public interface IWorkerListener {

	Uri createVault(String name, String password, int iterations);

	Uri createAccount(Uri vault, Uri group, String name, String username,
			String password);

	// Returns True if Vault successfully unlocked
	boolean unlockVault(Uri vault, String password);

	void lockVault(Uri vault);

	// Check to see if vault is unlocked or not
	boolean isVaultUnlocked(Uri vault);
	
	Account getDecryptedAccount(Uri accountUri);
}
