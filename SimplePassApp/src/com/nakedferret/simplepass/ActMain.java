package com.nakedferret.simplepass;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Group;
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
	void initialize() {
		Log.d("SimplePass", "Strict mode disabled...");
		Log.d("SimplePass", "Thread Policy: " + StrictMode.getThreadPolicy());
		Log.d("SimplePass", "VM Policy: " + StrictMode.getVmPolicy());

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().build());

		Log.d("SimplePass", "Strict mode enabled...");
		Log.d("SimplePass", "Thread Policy: " + StrictMode.getThreadPolicy());
		Log.d("SimplePass", "VM Policy: " + StrictMode.getVmPolicy());
		// Creates the database if the app is opened for the first time
		initDB();
	}

	@Background
	void initDB() {
		// Upgrades or creates the database if needed
		new PasswordStorageDbHelper(this).getWritableDatabase().close();
	}

	@Click(R.id.testDataButton)
	void onCreateTestData() {
		clearAndInsertTestData();
	}

	@Background
	void clearAndInsertTestData() {
		PasswordStorageDbHelper helper = new PasswordStorageDbHelper(this);
		helper.clearData();
		helper.close();

		ContentResolver r = getContentResolver();

		ContentValues vault = Utils.createVault("Personal", "secret", 2500);
		Uri rowUri = r.insert(Utils.buildContentUri(Vault.TABLE_NAME), vault);
		int vaultId = Integer.parseInt(rowUri.getLastPathSegment());

		insertAccount("Reddit", "Entertainment", vaultId);
		insertAccount("Xda-Developers", "Development", vaultId);
		insertAccount("Twitter", "Social", vaultId);
		insertAccount("Bank", "Financial", vaultId);
		insertAccount("Amazon", "Retail", vaultId);
	}

	private void insertAccount(String name, String group, int vaultId) {
		ContentResolver r = getContentResolver();
		ContentValues values = new ContentValues();

		values.put(Group.COL_NAME, group);
		Uri rowUri = r.insert(Utils.buildContentUri(Group.TABLE_NAME), values);

		values.clear();

		final String username = "naked_ferret";
		final String pass = "super_secret";
		final int groupID = Integer.parseInt(rowUri.getLastPathSegment());

		values.put(Account.COL_GROUP, groupID);
		values.put(Account.COL_NAME, name);
		values.put(Account.COL_PASSWORD, pass);
		values.put(Account.COL_USERNAME, username);
		values.put(Account.COL_VAULT, vaultId);

		r.insert(Utils.buildContentUri(Account.TABLE_NAME), values);
	}

	@Click(R.id.testQuery)
	void onTestQuery() {
		startActivity(new Intent(this, ActSelectAccount_.class));
	}

	@Click(R.id.testFrags)
	void testGenericFrag() {
		startActivity(new Intent(this, ActFragTest_.class));
	}

}
