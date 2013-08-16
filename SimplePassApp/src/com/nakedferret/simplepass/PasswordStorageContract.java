package com.nakedferret.simplepass;

import android.provider.BaseColumns;

public final class PasswordStorageContract {

	public PasswordStorageContract() {
	}

	public static abstract class Account implements BaseColumns {
		public static final String TABLE_NAME = "account";
		public static final String COL_NAME = TABLE_NAME + "_name";
		public static final String COL_GROUP_ID = Group.TABLE_NAME + _ID;
		public static final String COL_USERNAME = TABLE_NAME + "_username";
		public static final String COL_PASSWORD = TABLE_NAME + "_password";
		public static final String COL_VAULT_ID = Vault.TABLE_NAME + _ID;
	}

	public static abstract class Vault implements BaseColumns {
		public static final String TABLE_NAME = "vault";
		public static final String COL_NAME = TABLE_NAME + "_name";
		public static final String COL_SALT = TABLE_NAME + "_salt";
		public static final String COL_IV = TABLE_NAME + "_iv";
		public static final String COL_ITERATIONS = TABLE_NAME + "_iterations";
		public static final String COL_HASH = TABLE_NAME + "_hash";
	}

	public static abstract class Group implements BaseColumns {
		// Group is a special word in SQL
		public static final String TABLE_NAME = "category";
		public static final String COL_NAME = TABLE_NAME + "_name";
	}

	public static abstract class AccountWGroup extends Account {
		public static final String TABLE_NAME = "account_w_group";
		public static final String COL_GROUP_NAME = Group.COL_NAME;
	}

}
