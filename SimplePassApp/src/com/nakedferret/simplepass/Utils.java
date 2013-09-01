package com.nakedferret.simplepass;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Group;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

public class Utils {

	public static final String ENCRYPTION_CIPHER = "AES/CBC/PKCS5Padding";
	public static final String KEY_SPEC = "AES";

	public static final int SALT_SIZE = 32;
	public static final Digest DIGEST = new SHA256Digest();

	public static Uri buildContentUri(String table, long id) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(PasswordStorageProvider.AUTHORITY);
		builder.appendPath(table);
		if (id > 0)
			builder.appendPath(Long.toString(id));

		return builder.build();
	}

	public static Uri buildContentUri(String table) {
		return buildContentUri(table, 0);
	}

	public static ContentValues createVault(String name, String pass,
			int iterations) {

		try {
			ContentValues values = new ContentValues();

			String password = pass;
			byte[] salt = new byte[SALT_SIZE];
			new SecureRandom().nextBytes(salt);
			byte[] keyValue = getKey(password, salt, iterations);

			Key key = new SecretKeySpec(keyValue, KEY_SPEC);
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);

			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encPass = c.doFinal(password.getBytes("UTF-8"));
			byte[] iv = c.getIV();
			byte[] hash = getHash(encPass, salt);

			values.put(Vault.COL_NAME, name);
			values.put(Vault.COL_ITERATIONS, iterations);
			values.put(Vault.COL_IV, iv);
			values.put(Vault.COL_SALT, salt);
			values.put(Vault.COL_HASH, hash);

			return values;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ContentValues();
	}

	public static ContentValues createGroup(String name) {
		ContentValues group = new ContentValues();
		group.put(Group.COL_NAME, name);
		return group;
	}

	public static ContentValues getGroup(Cursor c) {
		ContentValues group = new ContentValues();
		group.put(Group._ID, c.getLong(c.getColumnIndex(Group._ID)));
		group.put(Group.COL_NAME, c.getString(c.getColumnIndex(Group.COL_NAME)));
		return group;
	}

	public static ContentValues createAccount(ContentValues vault, String name,
			String username, String password, Long groupId, String vaultPass) {

		try {
			ContentValues account = new ContentValues();

			byte[] salt = vault.getAsByteArray(Vault.COL_SALT);
			int iterations = vault.getAsInteger(Vault.COL_ITERATIONS);
			byte[] iv = vault.getAsByteArray(Vault.COL_IV);
			byte[] keyValue = getKey(password, salt, iterations);

			Key key = new SecretKeySpec(keyValue, KEY_SPEC);
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

			byte[] encUser = c.doFinal(username.getBytes("UTF-8"));
			byte[] encPass = c.doFinal(password.getBytes("UTF-8"));

			account.put(Account.COL_NAME, name);
			account.put(Account.COL_GROUP_ID, groupId);
			account.put(Account.COL_VAULT_ID, vault.getAsLong(Vault._ID));
			account.put(Account.COL_USERNAME, encUser);
			account.put(Account.COL_PASSWORD, encPass);

			return account;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ContentValues();
	}

	public static byte[] getKey(String pass, int iterations) {
		byte[] salt = new byte[SALT_SIZE];
		new SecureRandom().nextBytes(salt);
		return getKey(pass, salt, iterations);
	}

	public static byte[] getKey(String pass, byte[] salt, int iterations) {
		char[] passChar = pass.toCharArray();
		byte[] passBytes = PBEParametersGenerator
				.PKCS5PasswordToUTF8Bytes(passChar);
		PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(DIGEST);
		gen.init(passBytes, salt, iterations);
		KeyParameter key = (KeyParameter) gen.generateDerivedMacParameters(256);
		return key.getKey();
	}

	public static byte[] getHash(byte[] encPass, byte[] salt) {
		Digest d = DIGEST;
		d.reset();

		d.update(encPass, 0, encPass.length);
		d.update(salt, 0, salt.length);

		byte[] hash = new byte[d.getDigestSize()];
		d.doFinal(hash, 0);
		return hash;
	}

	public static ContentValues createAccount(long groupId, long vaultId,
			String name, String username, String password, byte[] keyValue,
			byte[] iv) {

		try {
			ContentValues values = new ContentValues();

			Key key = new SecretKeySpec(keyValue, KEY_SPEC);
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);

			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encUser = c.doFinal(username.getBytes("UTF-8"));
			byte[] encPass = c.doFinal(password.getBytes("UTF-8"));

			values.put(Account.COL_GROUP_ID, groupId);
			values.put(Account.COL_NAME, name);
			values.put(Account.COL_PASSWORD, encPass);
			values.put(Account.COL_USERNAME, encUser);
			values.put(Account.COL_VAULT_ID, vaultId);

			return values;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ContentValues();
	}

	public static ContentValues decryptAccount(ContentValues account,
			byte[] keyValue, byte[] iv) {
		try {

			Key key = new SecretKeySpec(keyValue, KEY_SPEC);
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);

			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encUser = Hex.decode(account
					.getAsString(Account.COL_USERNAME));
			byte[] encPass = Hex.decode(account
					.getAsString(Account.COL_PASSWORD));

			byte[] decUser = c.doFinal(encUser);
			byte[] decPass = c.doFinal(encPass);

			account.put(Account.DEC_USERNAME, new String(decUser));
			account.put(Account.DEC_USERNAME, new String(decPass));

			return account;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return account;
	}

	public static void log(Object o, String message) {
		message = o.getClass().getSimpleName() + " - " + message;
		Log.d("SimplePass", message);
	}

	public static ContentValues getVault(Cursor c) {
		c.moveToFirst();

		ContentValues vault = new ContentValues();

		vault.put(Vault._ID, c.getLong(c.getColumnIndex(Vault._ID)));
		vault.put(Vault.COL_HASH, c.getBlob(c.getColumnIndex(Vault.COL_HASH)));
		vault.put(Vault.COL_ITERATIONS,
				c.getLong(c.getColumnIndex(Vault.COL_ITERATIONS)));
		vault.put(Vault.COL_IV, c.getBlob(c.getColumnIndex(Vault.COL_IV)));
		vault.put(Vault.COL_NAME, c.getString(c.getColumnIndex(Vault.COL_NAME)));
		vault.put(Vault.COL_SALT, c.getBlob(c.getColumnIndex(Vault.COL_SALT)));
		c.close();
		return vault;
	}

}
