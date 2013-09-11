package com.nakedferret.simplepass;

import android.net.Uri;

public interface IUIListener {

	// Vault created
	void onVaultCreated(Uri vault);

	// Alert a vault was unlocked
	void onVaultUnlocked(Uri vault, byte[] key, byte[] iv);

	// Alert wrong password used to unlock
	void onVaultUnlockedFailed(Uri vault);

	// Alert a vault was locked
	void onVaultLocked(Uri vault);

	// Account created
	void onAccountCreated(Uri account);

}