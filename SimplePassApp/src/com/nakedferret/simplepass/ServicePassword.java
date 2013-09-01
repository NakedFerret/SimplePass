package com.nakedferret.simplepass;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		handleIntent(i);
		return START_STICKY;
	}

	void handleIntent(Intent i) {
		String uriString = i.getStringExtra(EXTRA_VAULT_URI);
		Uri uri = Uri.parse(uriString);

		if (UNLOCK_VAULT.equals(i.getAction())) {
			String pass = i.getStringExtra(EXTRA_VAULT_PASSWORD);
			unlockVault(uri, pass);
		} else if (LOCK_VAULT.equals(i.getAction())) {
			lockVault(uri);
		}
	}

	// Use this method to join back to the UI thread
	@UiThread
	void dispatchIntent(Intent i) {

	}

	@Background
	void unlockVault(Uri uri, String pass) {
		Utils.log(this, "Going to attempt to unlock vault: " + uri.toString());
		Utils.log(this, "with the password: " + pass);

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

			Utils.log(this,
					"hash are the same: " + Arrays.equals(hash, storedHash));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: implement unlock vault
		// generate the key, check the hash,
		// and send an intent that the vault was unlocked if successful and
		// locked if failed

	}

	private void lockVault(Uri uri) {
		// TODO: implement lock vault
		// null the key and iv, and send an intent that the vault was locked
	}

}