package com.nakedferret.simplepass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nakedferret.simplepass.PasswordStorageContract.Account_;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;
import com.nakedferret.simplepass.PasswordStorageContract.Group_;
import com.nakedferret.simplepass.PasswordStorageContract.Vault_;

public class PasswordStorageDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "vault_room";

	//@formatter:off //Eclipse formatting
	private static final String CREATE_VAULTS_TABLE = "CREATE TABLE "
			+ Vault_.TABLE_NAME + " ( " 
			+ Vault_._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ Vault_.COL_SALT + " BLOB, " 
			+ Vault_.COL_IV + " BLOB, " 
			+ Vault_.COL_NAME + " TEXT, "
			+ Vault_.COL_ITERATIONS + " INTEGER, " 
			+ Vault_.COL_HASH + " BLOB " 
			+ ")";
	//@formatter:on

	//@formatter:off //Eclipse formatting
	private static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE "
			+ Account_.TABLE_NAME + " ( "
			+ Account_._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Account_.COL_NAME + " TEXT, "
			+ Account_.COL_PASSWORD + " BLOB, "
			+ Account_.COL_USERNAME + " BLOB, "
			+ Account_.COL_VAULT_ID + " INTEGER, " 
			+ Account_.COL_GROUP_ID + " INTEGER, "
			+ "FOREIGN KEY(" + Account_.COL_VAULT_ID + ") REFERENCES "
			+ Vault_.TABLE_NAME + "(" + Vault_._ID + "), "
			+ "FOREIGN KEY(" + Account_.COL_GROUP_ID + ") REFERENCES "
			+ Group_.TABLE_NAME + "(" + Group_._ID + ")"
			+ ")";
	//@formatter:on

	//@formatter:off //Eclipse formatting
	private static final String CREATE_GROUPS_TABLE = "CREATE TABLE " 
			+ Group_.TABLE_NAME + " ( "
			+ Group_._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Group_.COL_NAME + " TEXT"
			+ ")";
	//@formatter:on

	// CREATE VIEW account_w_group AS SELECT account._id, account_name,
	// account_username, account_password, category_name, category_id, vault_id
	// FROM account INNER JOIN categoryON category._id = category_id;

	//@formatter:off //Eclipse formatting
	private static final String CREATE_ACCOUNTS_W_GROUPS = "CREATE VIEW "
			+ AccountWGroup.TABLE_NAME + " AS " + "SELECT "
			+ Account_.TABLE_NAME + "." + Account_._ID + ", "
			+ AccountWGroup.COL_NAME + ", "
			+ AccountWGroup.COL_USERNAME + ", "
			+ AccountWGroup.COL_PASSWORD + ", "
			+ AccountWGroup.COL_GROUP_NAME + ", "
			+ AccountWGroup.COL_GROUP_ID + ", "
			+ AccountWGroup.COL_VAULT_ID +  " "
			+ "FROM " + Account_.TABLE_NAME 
			+ " INNER JOIN " + Group_.TABLE_NAME + " ON " 
			+ Group_.TABLE_NAME + "." + Group_._ID + " = " + Account_.COL_GROUP_ID
			;
	//@formatter:on //Eclipse formatting

	private Context c;

	public PasswordStorageDbHelper(Context c) {
		super(c, DB_NAME, null, 2);
		this.c = c;
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

		db.execSQL("drop table " + Vault_.TABLE_NAME);
		db.execSQL("drop table " + Account_.TABLE_NAME);
		db.execSQL("drop table " + Group_.TABLE_NAME);
		db.execSQL("drop view " + AccountWGroup.TABLE_NAME);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(CREATE_VAULTS_TABLE);
		db.execSQL(CREATE_ACCOUNTS_TABLE);
		db.execSQL(CREATE_GROUPS_TABLE);
		db.execSQL(CREATE_ACCOUNTS_W_GROUPS);
		// Add the default group to groups table
		ContentValues values = new ContentValues();
		values.put(Group_.COL_NAME, c.getString(R.string.defaultGroup));
		db.insert(Group_.TABLE_NAME, null, values);
	}
}
