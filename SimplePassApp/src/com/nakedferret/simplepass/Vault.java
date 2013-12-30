package com.nakedferret.simplepass;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

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

}
