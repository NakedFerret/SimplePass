package com.nakedferret.simplepass;

import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

@EService
public class ServicePassword extends Service {

	public static final String VAULT_UNLOCKED = "vault_unlocked";
	public static final String VAULT_LOCKED = "vault_locked";

	public static final String EXTRA_VAULT_URI = "vault_uri";
	public static final String EXTRA_VAULT_KEY = "vault_key";
	public static final String EXTRA_VAULT_IV = "vault_iv";

	public static final String UNLOCK_VAULT = "unlock_vault";
	public static final String LOCK_VAULT = "lock_vault";

	public static final String EXTRA_VAULT_PASSWORD = "vault_pass";

	private Map<Uri, UnlockedVault> unlockedVaults = new HashMap<Uri, UnlockedVault>();

	private class UnlockedVault {
		Uri vault;
		byte[] key;
		byte[] iv;

		public UnlockedVault(Uri vault, byte[] key, byte[] iv) {
			super();
			this.vault = vault;
			this.key = key;
			this.iv = iv;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		// Sometimes android sends an empty intent to restart the service
		if (i != null)
			handleIntent(i);
		return START_STICKY;
	}

	void handleIntent(Intent i) {
		String uriString = i.getStringExtra(EXTRA_VAULT_URI);
		Uri uri = Uri.parse(uriString);

		if (UNLOCK_VAULT.equals(i.getAction())) {
			String pass = i.getStringExtra(EXTRA_VAULT_PASSWORD);
			handleUnlockVault(uri, pass);
		} else if (LOCK_VAULT.equals(i.getAction())) {
			lockVault(uri);
		}
	}

	// Use this method to join back to the UI thread
	@UiThread
	void dispatchIntent(Intent i) {
		LocalBroadcastManager m = LocalBroadcastManager.getInstance(this);
		m.sendBroadcast(i);
		Utils.log(this, "sent the intent");
	}

	@Background
	void handleUnlockVault(Uri uri, String pass) {
		Intent i; // intent to broadcast VAULT_UNLOCKED or VAULT_LOCKED
		if (vaultAlreadyUnlocked(uri) || unlockVault(uri, pass)) {
			byte[] iv = unlockedVaults.get(uri).iv;
			byte[] key = unlockedVaults.get(uri).key;

			i = new Intent(VAULT_UNLOCKED);
			i.putExtra(EXTRA_VAULT_URI, uri.toString());
			i.putExtra(EXTRA_VAULT_IV, iv);
			i.putExtra(EXTRA_VAULT_KEY, key);
			Utils.log(this, "created the unlock intent");
		} else {
			i = new Intent(VAULT_LOCKED);
			i.putExtra(EXTRA_VAULT_URI, uri.toString());
			Utils.log(this, "created the lock intent");
		}

		dispatchIntent(i);
		// TODO: implement unlock vault
		// generate the key, check the hash,
		// and send an intent that the vault was unlocked if successful and
		// locked if failed

	}

	private boolean vaultAlreadyUnlocked(Uri uri) {
		return unlockedVaults.containsKey(uri);
	}

	// Attempts to unlock vault. Returns true if vault unlocks, otherwise
	// returns false
	private boolean unlockVault(Uri uri, String pass) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		ContentValues vault = Utils.getVault(cursor);
		cursor.close();

		Utils.log(this, "Got the vault: " + vault.getAsString(Vault.COL_NAME));
		byte[] iv = vault.getAsByteArray(Vault.COL_IV);
		byte[] salt = vault.getAsByteArray(Vault.COL_SALT);

		byte[] storedHash = vault.getAsByteArray(Vault.COL_HASH);
		int iterations = vault.getAsInteger(Vault.COL_ITERATIONS);
		byte[] keyValue = Utils.getKey(pass, salt, iterations);

		try {
			Key key = new SecretKeySpec(keyValue, Utils.KEY_SPEC);
			Cipher c = Cipher.getInstance(Utils.ENCRYPTION_CIPHER);

			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encPass = c.doFinal(pass.getBytes("UTF-8"));
			byte[] hash = Utils.getHash(encPass, salt);

			if (Arrays.equals(hash, storedHash)) {
				Utils.log(this, "unlock successful");
				storeUnlockedVault(uri, keyValue, iv);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Utils.log(this, "unlock unsuccessful");
		// We couldn't unlock
		return false;
	}

	private void storeUnlockedVault(Uri uri, byte[] keyValue, byte[] iv) {
		UnlockedVault v = new UnlockedVault(uri, keyValue, iv);
		if (!unlockedVaults.containsKey(uri))
			unlockedVaults.put(uri, v);

		Utils.log(this, "stored the locked vault");
	}

	private void lockVault(Uri uri) {
		// TODO: implement lock vault
		// null the key and iv, and send an intent that the vault was locked
	}

}
