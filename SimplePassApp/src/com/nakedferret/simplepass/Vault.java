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
}
