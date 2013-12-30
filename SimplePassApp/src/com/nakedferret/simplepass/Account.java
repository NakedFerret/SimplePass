package com.nakedferret.simplepass;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table
public class Account extends Model {

	@Column
	String name;
	
	@Column
	Category category;
	
	@Column
	byte[] username;
	
	@Column
	byte[] password;
	
	@Column
	Vault vault;
}
