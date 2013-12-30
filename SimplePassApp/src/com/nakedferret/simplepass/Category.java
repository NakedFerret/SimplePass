package com.nakedferret.simplepass;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table
public class Category extends Model {
	
	@Column
	public String name;
}
