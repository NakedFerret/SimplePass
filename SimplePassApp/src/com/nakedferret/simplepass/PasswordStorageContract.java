package com.nakedferret.simplepass;

import android.provider.BaseColumns;

public final class PasswordStorageContract {

	public PasswordStorageContract() {
	}

	public static abstract class Account implements BaseColumns {
		public static final String TABLE_NAME = "accounts";
		public static final String COL_NAME = "name";
		public static final String COL_GROUP = "group";
		public static final String COL_USERNAME = "username";
		public static final String COL_PASSWORD = "password";
	}

	public static abstract class Vault implements BaseColumns {
		public static final String TABLE_NAME = "vaults";
		public static final String COL_NAME = "name";
		public static final String COL_SALT = "salt";
		public static final String COL_IV = "iv";
		public static final String COL_ITERATIONS = "iterations";
		public static final String COL_HASH = "hash";
	}

}
