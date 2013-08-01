package com.nakedferret.simplepass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

public class PasswordStorageDbHelper extends SQLiteOpenHelper {

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
			+ "FOREIGN KEY(" + Account.COL_VAULT + ") REFERENCES "
			+ Vault.TABLE_NAME + "(" + Vault._ID + ")" 
			+ ")";
	//@formatter:on

	public PasswordStorageDbHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_VAULTS_TABLE);
		db.execSQL(CREATE_ACCOUNTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
