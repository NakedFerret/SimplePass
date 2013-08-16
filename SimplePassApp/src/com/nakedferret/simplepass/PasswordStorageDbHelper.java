package com.nakedferret.simplepass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;
import com.nakedferret.simplepass.PasswordStorageContract.Group;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

public class PasswordStorageDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "vault_room";

	//@formatter:off //Eclipse formatting
	private static final String CREATE_VAULTS_TABLE = "CREATE TABLE "
			+ Vault.TABLE_NAME + " ( " 
			+ Vault._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ Vault.COL_SALT + " TEXT, " 
			+ Vault.COL_IV + " TEXT, " 
			+ Vault.COL_NAME + " TEXT, "
			+ Vault.COL_ITERATIONS + " INTEGER, " 
			+ Vault.COL_HASH + " TEXT " 
			+ ")";
	//@formatter:on

	//@formatter:off //Eclipse formatting
	private static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE "
			+ Account.TABLE_NAME + " ( "
			+ Account._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Account.COL_NAME + " TEXT, "
			+ Account.COL_PASSWORD + " TEXT, "
			+ Account.COL_USERNAME + " TEXT, "
			+ Account.COL_VAULT_ID + " INTEGER, " 
			+ Account.COL_GROUP_ID + " INTEGER, "
			+ "FOREIGN KEY(" + Account.COL_VAULT_ID + ") REFERENCES "
			+ Vault.TABLE_NAME + "(" + Vault._ID + "), "
			+ "FOREIGN KEY(" + Account.COL_GROUP_ID + ") REFERENCES "
			+ Group.TABLE_NAME + "(" + Group._ID + ")"
			+ ")";
	//@formatter:on

	//@formatter:off //Eclipse formatting
	private static final String CREATE_GROUPS_TABLE = "CREATE TABLE " 
			+ Group.TABLE_NAME + " ( "
			+ Group._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Group.COL_NAME + " TEXT"
			+ ")";
	//@formatter:on

	// CREATE VIEW account_w_group AS SELECT account._id, account_name,
	// account_username, account_password, category_name, category_id, vault_id
	// FROM account INNER JOIN categoryON category._id = category_id;

	//@formatter:off //Eclipse formatting
	private static final String CREATE_ACCOUNTS_W_GROUPS = "CREATE VIEW "
			+ AccountWGroup.TABLE_NAME + " AS " + "SELECT "
			+ Account.TABLE_NAME + "." + Account._ID + ", "
			+ AccountWGroup.COL_NAME + ", "
			+ AccountWGroup.COL_USERNAME + ", "
			+ AccountWGroup.COL_PASSWORD + ", "
			+ AccountWGroup.COL_GROUP_NAME + ", "
			+ AccountWGroup.COL_GROUP_ID + ", "
			+ AccountWGroup.COL_VAULT_ID +  " "
			+ "FROM " + Account.TABLE_NAME 
			+ " INNER JOIN " + Group.TABLE_NAME + " ON " 
			+ Group.TABLE_NAME + "." + Group._ID + " = " + Account.COL_GROUP_ID
			;
	//@formatter:on //Eclipse formatting

	public PasswordStorageDbHelper(Context c) {
		super(c, DB_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		clearData(db);
	}

	public void clearData(SQLiteDatabase db) {
		if (db == null)
			db = getWritableDatabase();

		db.execSQL("drop table " + Vault.TABLE_NAME);
		db.execSQL("drop table " + Account.TABLE_NAME);
		db.execSQL("drop table " + Group.TABLE_NAME);
		db.execSQL("drop view " + AccountWGroup.TABLE_NAME);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(CREATE_VAULTS_TABLE);
		db.execSQL(CREATE_ACCOUNTS_TABLE);
		db.execSQL(CREATE_GROUPS_TABLE);
		db.execSQL(CREATE_ACCOUNTS_W_GROUPS);
	}
}
