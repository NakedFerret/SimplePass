package com.nakedferret.simplepass;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;

@Table
public class Account extends Model {

	@Column
	public String name;

	@Column
	public Category category;

	@Column
	public byte[] username;

	@Column
	public byte[] password;

	@Column(onDelete = ForeignKeyAction.CASCADE)
	public Vault vault;

	public String decryptedUsername;
	public String decryptedPassword;

	public Account() {

	}

	public Account(String name, Category category, byte[] username,
			byte[] password, Vault vault) {
		this.name = name;
		this.category = category;
		this.username = username;
		this.password = password;
		this.vault = vault;
	}

}
