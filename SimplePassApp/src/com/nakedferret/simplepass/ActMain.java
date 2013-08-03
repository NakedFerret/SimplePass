package com.nakedferret.simplepass;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

@EActivity(R.layout.act_main)
public class ActMain extends SherlockActivity {

	@ViewById
	Button insertButton;

	@ViewById
	Button testDataButton;

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
		clearData();
		insertTestData();
	}

	@Background
	void clearData() {
		PasswordStorageDbHelper helper = new PasswordStorageDbHelper(this);
		helper.clearData();
	}

	@Background
	void insertTestData() {

	}

}
