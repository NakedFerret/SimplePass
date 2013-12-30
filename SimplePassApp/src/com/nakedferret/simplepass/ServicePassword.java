package com.nakedferret.simplepass;

import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.EService;
import com.nakedferret.simplepass.PasswordStorageContract.Vault_;

@EService
public class ServicePassword extends Service implements IWorkerListener {

	private Map<Uri, UnlockedVault> unlockedVaults = new HashMap<Uri, UnlockedVault>();

	public class LocalBinder extends Binder {
		ServicePassword getService() {
			return ServicePassword.this;
		}
	}

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
	public int onStartCommand(Intent i, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder();
	}

	@Override
	public boolean unlockVault(Uri uri, String pass) {

		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		ContentValues vault = Utils.getVault(cursor);
		cursor.close();

		Utils.log(this, "Got the vault: " + vault.getAsString(Vault_.COL_NAME));
		byte[] iv = vault.getAsByteArray(Vault_.COL_IV);
		byte[] salt = vault.getAsByteArray(Vault_.COL_SALT);

		byte[] storedHash = vault.getAsByteArray(Vault_.COL_HASH);
		int iterations = vault.getAsInteger(Vault_.COL_ITERATIONS);
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
		return false;
	}

	private void storeUnlockedVault(Uri uri, byte[] keyValue, byte[] iv) {
		UnlockedVault v = new UnlockedVault(uri, keyValue, iv);
		if (!unlockedVaults.containsKey(uri))
			unlockedVaults.put(uri, v);
	}

	@Override
	public Uri createVault(String name, String password, int iterations) {
		ContentValues values = Utils.createVault(name, password, iterations);
		ContentResolver r = getContentResolver();
		Uri vaultUri = r
				.insert(Utils.buildContentUri(Vault_.TABLE_NAME), values);

		return vaultUri;
	}

	@Override
	public Uri createAccount(Uri vault, Uri group, String name,
			String username, String password) {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public void lockVault(Uri vault) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVaultUnlocked(Uri vault) {
		return false;
		// TODO Auto-generated method stub

	}

}
