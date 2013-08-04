package com.nakedferret.simplepass;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

@EActivity(R.layout.act_main)
public class ActMain extends SherlockActivity {

	@ViewById
	Button insertButton;

	@ViewById
	Button testDataButton;

	@ViewById
	Button testQuery;

	@AfterViews
	void initDatabase() {
		// Creates the database if the app is opened for the first time
		new PasswordStorageDbHelper(this).getWritableDatabase();
	}

	@Click(R.id.insertButton)
	void onInsert() {
		doBackgroundInsert();
	}

	@Background
	void doBackgroundInsert() {
		ContentResolver r = getContentResolver();

		ContentValues values = new ContentValues();
		values.put(Vault.COL_NAME, "testVault");

		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(PasswordStorageProvider.authority);
		builder.appendPath(Vault.TABLE_NAME);

		r.insert(builder.build(), values);
	}

	@Click(R.id.testDataButton)
	void onCreateTestData() {
		clearAndInsertTestData();
	}

	@Background
	void clearAndInsertTestData() {
		PasswordStorageDbHelper helper = new PasswordStorageDbHelper(this);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();

		helper.clearData();

		values.put(Vault.COL_ITERATIONS, 5000);
		values.put(Vault.COL_NAME, "Personal");
		long vaultId = db.insert(Vault.TABLE_NAME, null, values);
		values.clear();

		values.put(Account.COL_NAME, "reddit");
		values.put(Account.COL_VAULT, vaultId);
		values.put(Account.COL_GROUP, "entertainment");
		values.put(Account.COL_USERNAME, "naked_ferret");
		values.put(Account.COL_PASSWORD, "secret");
		db.insert(Account.TABLE_NAME, null, values);
		values.clear();

		values.put(Account.COL_NAME, "xda-developers");
		values.put(Account.COL_VAULT, vaultId);
		values.put(Account.COL_GROUP, "development");
		values.put(Account.COL_USERNAME, "naked_ferret");
		values.put(Account.COL_PASSWORD, "secret2");
		db.insert(Account.TABLE_NAME, null, values);
		values.clear();

		values.put(Account.COL_NAME, "twitter");
		values.put(Account.COL_VAULT, vaultId);
		values.put(Account.COL_GROUP, "social");
		values.put(Account.COL_USERNAME, "naked_ferret");
		values.put(Account.COL_PASSWORD, "secret3");
		db.insert(Account.TABLE_NAME, null, values);
		values.clear();

		values.put(Account.COL_NAME, "bank");
		values.put(Account.COL_VAULT, vaultId);
		values.put(Account.COL_GROUP, "financial");
		values.put(Account.COL_USERNAME, "naked_ferret");
		values.put(Account.COL_PASSWORD, "secret4");
		db.insert(Account.TABLE_NAME, null, values);
		values.clear();

		values.put(Account.COL_NAME, "amazon");
		values.put(Account.COL_VAULT, vaultId);
		values.put(Account.COL_GROUP, "retail");
		values.put(Account.COL_USERNAME, "naked_ferret");
		values.put(Account.COL_PASSWORD, "secret5");
		db.insert(Account.TABLE_NAME, null, values);
		values.clear();
	}

	@Click(R.id.testQuery)
	void onTestQuery() {
		startActivity(new Intent(this, ActSelectAccount_.class));
	}
}
