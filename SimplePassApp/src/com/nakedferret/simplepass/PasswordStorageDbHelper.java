package com.nakedferret.simplepass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

public class PasswordStorageDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "storage_room";

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
			+ Account.COL_GROUP + " TEXT, "
			+ Account.COL_PASSWORD + " TEXT, "
			+ Account.COL_USERNAME + " TEXT, "
			+ Account.COL_VAULT + " INTEGER, " 
			+ "FOREIGN KEY(" + Account.COL_VAULT + ") REFERENCES "
			+ Vault.TABLE_NAME + "(" + Vault._ID + ")" 
			+ ")";
	//@formatter:on

	public PasswordStorageDbHelper(Context c) {
		super(c, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void clearData() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("drop table " + Vault.TABLE_NAME);
		db.execSQL("drop table " + Account.TABLE_NAME);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(CREATE_VAULTS_TABLE);
		db.execSQL(CREATE_ACCOUNTS_TABLE);
	}
}
