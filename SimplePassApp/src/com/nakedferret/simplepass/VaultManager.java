package com.nakedferret.simplepass;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class VaultManager implements IWorker {

	private Map<Long, Vault> unlockedVaults = new HashMap<Long, Vault>();

	@Override
	public boolean unlockVault(Long vaultId, String pass) {
		Vault v = Vault.load(Vault.class, vaultId);

		if (v.unlock(pass)) {
			unlockedVaults.put(vaultId, v);
			return true;
		}

		return false;
	}

	@Override
	public Long createVault(String name, String password, int iterations) {
		Vault v = Vault.createVault(name, password, iterations);
		v.save();
		return v.getId();
	}

	@Override
	public Long createAccount(Long vaultId, Long categoryId, String name,
			String username, String password) {
		Vault v = unlockedVaults.get(vaultId);
		Category c = Category.load(Category.class, categoryId);
		Account a = v.createAccount(name, username, password, c);
		a.save();
		return a.getId();
	}

	@Override
	public void lockVault(Long vaultId) {
		Vault v = unlockedVaults.get(vaultId);
		v.lock();
		unlockedVaults.remove(vaultId);
	}

	@Override
	public boolean isVaultUnlocked(Long vaultId) {
		return unlockedVaults.get(vaultId) != null;
	}

	@Override
	public Account getDecryptedAccount(Long accountId) {
		Account a = Account.load(Account.class, accountId);
		Vault v = unlockedVaults.get(a.vault.getId());

		if (v == null)
			return null;

		v.decryptAccount(a);
		return a;
	}
}
