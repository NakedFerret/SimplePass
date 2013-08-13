package com.nakedferret.simplepass;

import android.provider.BaseColumns;

public final class PasswordStorageContract {

	public PasswordStorageContract() {
	}

	public static abstract class Account implements BaseColumns {
		public static final String TABLE_NAME = "account";
		public static final String COL_NAME = "name";
		public static final String COL_GROUP = "group_id";
		public static final String COL_USERNAME = "username";
		public static final String COL_PASSWORD = "password";
		public static final String COL_VAULT = "vault_id";
	}

	public static abstract class Vault implements BaseColumns {
		public static final String TABLE_NAME = "vault";
		public static final String COL_NAME = "name";
		public static final String COL_SALT = "salt";
		public static final String COL_IV = "iv";
		public static final String COL_ITERATIONS = "iterations";
		public static final String COL_HASH = "hash";
	}

	public static abstract class Group implements BaseColumns {
		// Group is a special word in SQL
		public static final String TABLE_NAME = "category";
		public static final String COL_NAME = "name";
	}

}
