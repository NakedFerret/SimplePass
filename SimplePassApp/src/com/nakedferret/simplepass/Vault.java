package com.nakedferret.simplepass;

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

}
