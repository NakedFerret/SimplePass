package com.nakedferret.simplepass;

import java.util.Arrays;

import javax.crypto.Cipher;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table
public class Vault extends Model {

	@Column
	public String name;

	@Column
	public byte[] salt;

	@Column
	public byte[] iv;

	@Column
	public int iterations;

	@Column
	public byte[] hash;

	private boolean unlocked = false;
	private byte[] key;

	public Vault() {

	}

	public Vault(String name, byte[] salt, byte[] iv, int iterations,
			byte[] hash) {
		this.name = name;
		this.salt = salt;
		this.iv = iv;
		this.iterations = iterations;
		this.hash = hash;
	}

	public static Vault createVault(String name, String password, int iterations) {
		Vault v = new Vault();
		v.name = name;
		v.iterations = iterations;
		v.salt = Utils.getSalt();

		byte[] key = Utils.getKey(password, v.salt, iterations);
		Cipher cipher = Utils.getEncryptionCipher(key);
		byte[] encPass = Utils.encrypt(cipher, password);

		v.iv = cipher.getIV();
		v.hash = Utils.getHash(encPass, v.salt);
		return v;
	}

	public boolean unlock(String password) {
		byte[] key = Utils.getKey(password, salt, iterations);

		Cipher c = Utils.getEncryptionCipher(key, iv);
		byte[] encPass = Utils.encrypt(c, password);
		byte[] hash = Utils.getHash(encPass, salt);

		if (Arrays.equals(hash, this.hash)) {
			Utils.log(this, "unlock successful");
			unlocked = true;
			this.key = key;
		}
		return unlocked;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void decryptAccount(Account a) {
		if (!unlocked)
			return;

		Cipher c = Utils.getDecryptionCipher(key, iv);
		a.decryptedUsername = new String(Utils.decrypt(c, a.username));
		a.decryptedPassword = new String(Utils.decrypt(c, a.password));
	}

}
