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

import com.nakedferret.simplepass.PasswordStorageContract.Account_;
import com.nakedferret.simplepass.PasswordStorageContract.Group_;
import com.nakedferret.simplepass.PasswordStorageContract.Vault_;

public class Utils {

	public static final String ENCRYPTION_CIPHER = "AES/CBC/PKCS5Padding";
	public static final String KEY_SPEC = "AES";

	public static final int SALT_SIZE = 32;
	public static final Digest DIGEST = new SHA256Digest();

	public static byte[] getSalt() {
		byte[] salt = new byte[SALT_SIZE];
		new SecureRandom().nextBytes(salt);
		return salt;
	}

	public static ContentValues createGroup(String name) {
		ContentValues group = new ContentValues();
		group.put(Group_.COL_NAME, name);
		return group;
	}

	public static ContentValues getGroup(Cursor c) {
		ContentValues group = new ContentValues();
		group.put(Group_._ID, c.getLong(c.getColumnIndex(Group_._ID)));
		group.put(Group_.COL_NAME,
				c.getString(c.getColumnIndex(Group_.COL_NAME)));
		return group;
	}

	public static ContentValues createAccount(ContentValues vault, String name,
			String username, String password, Long groupId, String vaultPass) {

		try {
			ContentValues account = new ContentValues();

			byte[] salt = vault.getAsByteArray(Vault_.COL_SALT);
			int iterations = vault.getAsInteger(Vault_.COL_ITERATIONS);
			byte[] iv = vault.getAsByteArray(Vault_.COL_IV);
			byte[] keyValue = getKey(password, salt, iterations);

			Key key = new SecretKeySpec(keyValue, KEY_SPEC);
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

			byte[] encUser = c.doFinal(username.getBytes("UTF-8"));
			byte[] encPass = c.doFinal(password.getBytes("UTF-8"));

			account.put(Account_.COL_NAME, name);
			account.put(Account_.COL_GROUP_ID, groupId);
			account.put(Account_.COL_VAULT_ID, vault.getAsLong(Vault_._ID));
			account.put(Account_.COL_USERNAME, encUser);
			account.put(Account_.COL_PASSWORD, encPass);

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

			values.put(Account_.COL_GROUP_ID, groupId);
			values.put(Account_.COL_NAME, name);
			values.put(Account_.COL_PASSWORD, encPass);
			values.put(Account_.COL_USERNAME, encUser);
			values.put(Account_.COL_VAULT_ID, vaultId);

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
					.getAsString(Account_.COL_USERNAME));
			byte[] encPass = Hex.decode(account
					.getAsString(Account_.COL_PASSWORD));

			byte[] decUser = c.doFinal(encUser);
			byte[] decPass = c.doFinal(encPass);

			account.put(Account_.DEC_USERNAME, new String(decUser));
			account.put(Account_.DEC_USERNAME, new String(decPass));

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

	public static Cipher getEncryptionCipher(byte[] keyValue) {
		Key key = new SecretKeySpec(keyValue, KEY_SPEC);
		try {
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);
			c.init(Cipher.ENCRYPT_MODE, key);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Cipher getEncryptionCipher(byte[] keyValue, byte[] iv) {
		Key key = new SecretKeySpec(keyValue, KEY_SPEC);
		try {
			Cipher c = Cipher.getInstance(ENCRYPTION_CIPHER);
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encrypt(Cipher c, String password) {
		try {
			return c.doFinal(password.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
