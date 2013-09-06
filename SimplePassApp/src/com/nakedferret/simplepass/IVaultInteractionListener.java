package com.nakedferret.simplepass;

import android.net.Uri;

interface IVaultInteractionListener {
	// User selects a vault in the UI
	void onVaultSelected(Uri vault);

	// User request to create a vault
	void requestCreateVault();

	// Vault created
	void onVaultCreated(Uri vault);

	// Alert a vault was unlocked
	void onVaultUnlocked(Uri vault);

	// Alert a vault was locked
	void onVaultLocked(Uri vault);

	// Alert an incorrect password attempt on a Vault
	void onVaultIncorrectPassword(Uri vault);

	// Return the state of the vault: open or closed
	void onTryVaultResult(Uri vault, boolean open);
}