package com.nakedferret.simplepass;

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class VaultManager {

	private Map<Long, Vault> unlockedVaults = new HashMap<Long, Vault>();
	private Handler handler = new Handler();

	@Background
	public void unlockVault(Long vaultId, String pass,
			ResultListener<Boolean> listener) {

		Vault v = Vault.load(Vault.class, vaultId);
		boolean result = false;

		if (v.unlock(pass)) {
			unlockedVaults.put(vaultId, v);
			result = true;
		}

		postResult(listener, result);
	}

	@Background
	public void createVault(String name, String password, int iterations,
			ResultListener<Long> listener) {

		Vault v = Vault.createVault(name, password, iterations);
		v.save();
		postResult(listener, v.getId());
	}

	@Background
	public void createAccount(Long vaultId, Long categoryId, String name,
			String username, String password, ResultListener<Long> listener) {
		Vault v = unlockedVaults.get(vaultId);
		Category c = Category.load(Category.class, categoryId);
		Account a = v.createAccount(name, username, password, c);
		a.save();
		postResult(listener, a.getId());
	}

	@Background
	public void lockVault(Long vaultId) {
		Vault v = unlockedVaults.get(vaultId);
		v.lock();
		unlockedVaults.remove(vaultId);
	}

	public boolean isVaultUnlocked(Long vaultId) {
		return unlockedVaults.get(vaultId) != null;
	}

	@Background
	public void getDecryptedAccount(Long accountId,
			ResultListener<Account> listener) {
		Account a = Account.load(Account.class, accountId);
		Vault v = unlockedVaults.get(a.vault.getId());

		if (v == null) {
			postResult(listener, null);
			return;
		}

		v.decryptAccount(a);
		postResult(listener, a);
	}

	public <T> void postResult(final ResultListener<T> listener, final T result) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				listener.onResult(result);
			}
		});
	}

	public interface ResultListener<T> {
		void onResult(T result);
	}

	public interface VaultLockListener {
		void onVaultLocked(Long vaultId);
	}

}
