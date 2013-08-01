package com.nakedferret.simplepass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

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
			+ Vault.COL_HASH + " TEXT )";
	//@formatter:on

	public PasswordStorageDbHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
