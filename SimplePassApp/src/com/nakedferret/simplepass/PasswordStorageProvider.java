package com.nakedferret.simplepass;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

public class PasswordStorageProvider extends ContentProvider {

	private static final String authority = "com.nakedferret.simplepass.provider";

	private static final int ACCOUNT = 1;
	private static final int ACCOUNT_ID = 2;

	private static final int VAULT = 3;
	private static final int VAULT_ID = 4;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(authority, "account", ACCOUNT);
		sURIMatcher.addURI(authority, "account/#", ACCOUNT_ID);
		sURIMatcher.addURI(authority, "vault", VAULT);
		sURIMatcher.addURI(authority, "vault/#", VAULT_ID);
	}

	private PasswordStorageDbHelper dbHelper;

	public PasswordStorageProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.close();
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.close();
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table = null;
		switch (sURIMatcher.match(uri)) {
			case ACCOUNT:
				table = Account.TABLE_NAME;
				break;
			case VAULT:
				table = Vault.TABLE_NAME;
				break;
			default:
				table = null;
		}
		if (table == null)
			return null;

		db.insert(table, null, values);
		// TODO: notify change
		// I think this is how you do it
		// getContext().getContentResolver().notifyChange(uri, null);
		db.close();
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public boolean onCreate() {
		dbHelper = new PasswordStorageDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.close();
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.close();
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
