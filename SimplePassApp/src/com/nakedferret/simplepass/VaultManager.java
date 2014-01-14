package com.nakedferret.simplepass;

import java.util.HashMap;
import java.util.Map;

import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.EBean;

import android.content.ContentUris;
import android.net.Uri;

@EBean
public class VaultManager implements IWorkerListener {

	private Map<Uri, Vault> unlockedVaults = new HashMap<Uri, Vault>();

	@Override
	public boolean unlockVault(Uri uri, String pass) {

		Vault v = Vault.load(Vault.class, ContentUris.parseId(uri));

		Utils.log(this, "Got the vault: " + v.name);
		if (v.unlock(pass)) {
			Utils.log(this, "vault unlock successful");
			unlockedVaults.put(uri, v);
			return true;
		}

		Utils.log(this, "vault unlock failed");
		return false;
	}

	@Override
	public Uri createVault(String name, String password, int iterations) {
		Vault v = Vault.createVault(name, password, iterations);
		v.save();
		return ContentProvider.createUri(Vault.class, v.getId());
	}

	@Override
	public Uri createAccount(Uri vaultUri, Uri categoryUri, String name,
			String username, String password) {
		Vault v = unlockedVaults.get(vaultUri);
		long categoryId = ContentUris.parseId(categoryUri);
		Category c = Category.load(Category.class, categoryId);
		Account a = v.createAccount(name, username, password, c);
		a.save();
		return ContentProvider.createUri(Account.class, a.getId());
	}

	@Override
	public void lockVault(Uri vaultUri) {
		Vault v = unlockedVaults.get(vaultUri);
		v.lock();
		unlockedVaults.remove(vaultUri);
	}

	@Override
	public boolean isVaultUnlocked(Uri vaultUri) {
		return unlockedVaults.get(vaultUri) != null;
	}

	@Override
	public Account getDecryptedAccount(Uri accountUri) {
		long accountId = ContentUris.parseId(accountUri);
		Account a = Account.load(Account.class, accountId);
		Uri vaultUri = ContentProvider.createUri(Vault.class, a.vault.getId());

		Vault v = unlockedVaults.get(vaultUri);
		if (v == null)
			return null;

		v.decryptAccount(a);
		return a;
	}
}
