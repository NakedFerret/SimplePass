package com.nakedferret.simplepass;

import android.provider.BaseColumns;

public final class PasswordStorageContract {

	public PasswordStorageContract() {
	}

	public static abstract class Account implements BaseColumns {
		public static final String TABLE_NAME = "accounts";
		public static final String COL_NAME = "_name";
		public static final String COL_GROUP = "_group";
		public static final String COL_USERNAME = "_username";
		public static final String COL_PASSWORD = "_password";
		public static final String COL_VAULT = "_vault_id";
	}

	public static abstract class Vault implements BaseColumns {
		public static final String TABLE_NAME = "vaults";
		public static final String COL_NAME = "_name";
		public static final String COL_SALT = "_salt";
		public static final String COL_IV = "_iv";
		public static final String COL_ITERATIONS = "_iterations";
		public static final String COL_HASH = "_hash";
	}

}
