package com.nakedferret.simplepass;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table
public class Vault extends Model {
	
	@Column
	String name;
	
	@Column
	byte[] salt;
	
	@Column
	byte[] iv;
	
	@Column
	int iterations;
	
	@Column
	byte[] hash;
}
