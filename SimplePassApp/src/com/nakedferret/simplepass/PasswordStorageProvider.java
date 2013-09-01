package com.nakedferret.simplepass;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;
import com.nakedferret.simplepass.PasswordStorageContract.Group;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

public class PasswordStorageProvider extends ContentProvider {

	public static final String AUTHORITY = "com.nakedferret.simplepass.provider";

	private static final int ACCOUNT = 1;
	private static final int ACCOUNT_ID = 10;
	private static final int VAULT = 2;
	private static final int VAULT_ID = 20;
	private static final int GROUP = 3;
	private static final int ACCOUNT_W_GROUP = 4;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, Account.TABLE_NAME, ACCOUNT);
		sURIMatcher.addURI(AUTHORITY, Account.TABLE_NAME + "/#", ACCOUNT_ID);
		sURIMatcher.addURI(AUTHORITY, Vault.TABLE_NAME, VAULT);
		sURIMatcher.addURI(AUTHORITY, Vault.TABLE_NAME + "/#", VAULT_ID);
		sURIMatcher.addURI(AUTHORITY, Group.TABLE_NAME, GROUP);
		sURIMatcher
				.addURI(AUTHORITY, AccountWGroup.TABLE_NAME, ACCOUNT_W_GROUP);
	}

	private PasswordStorageDbHelper dbHelper;

	public PasswordStorageProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
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
			case GROUP:
				table = Group.TABLE_NAME;
				break;
			default:
				table = null;
		}
		if (table == null)
			return null;

		long id = db.insert(table, null, values);
		Uri rowUri = Utils.buildContentUri(table, id);
		getContext().getContentResolver().notifyChange(rowUri, null);
		return rowUri;
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

		String table = null;
		long id = 0;

		switch (sURIMatcher.match(uri)) {
			case ACCOUNT:
				table = Account.TABLE_NAME;
				break;
			case VAULT_ID:
				id = Long.parseLong(uri.getLastPathSegment());
			case VAULT:
				table = Vault.TABLE_NAME;
				break;
			case GROUP:
				table = Group.TABLE_NAME;
				break;
			case ACCOUNT_W_GROUP:
				table = AccountWGroup.TABLE_NAME;
				break;
			default:
				table = null;
		}

		Utils.log(this, "table: " + table + "\nuri: " + uri.toString());

		if (table == null)
			return null;

		// Add the _ID to the list of columns as a courtesy :D
		if (projection != null) {
			String[] projectionWithID = new String[projection.length + 1];
			System.arraycopy(projection, 0, projectionWithID, 0,
					projection.length);
			projectionWithID[projection.length] = BaseColumns._ID;
			projection = projectionWithID;
		}

		if (id != 0) {
			selection = Vault._ID + " = ?";
			selectionArgs = new String[] { Long.toString(id) };
		}

		Cursor c = db.query(table, projection, selection, selectionArgs, null,
				null, null);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
