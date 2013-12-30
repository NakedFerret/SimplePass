package com.nakedferret.simplepass;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
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

	@Column
	public Vault vault;
}
