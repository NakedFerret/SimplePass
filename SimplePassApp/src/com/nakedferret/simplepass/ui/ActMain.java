package com.nakedferret.simplepass.ui;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.TableInfo;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.Account;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.PasswordStorageContract.Account_;
import com.nakedferret.simplepass.PasswordStorageContract.Group_;
import com.nakedferret.simplepass.PasswordStorageContract.Vault_;
import com.nakedferret.simplepass.PasswordStorageDbHelper;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.Vault;

@EActivity(R.layout.act_main)
public class ActMain extends SherlockActivity {

	@ViewById
	Button insertButton;

	@ViewById
	Button testDataButton;

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
		testActiveAndroid();
	}

	@Background
	void testActiveAndroid() {
		Vault workVault = new Vault();
		workVault.name = "Work";
		workVault.iterations = 1000;
		workVault.save();

		Vault personalVault = new Vault();
		personalVault.name = "Personal";
		personalVault.iterations = 500;
		personalVault.save();

		Category socialCategory = new Category();
		socialCategory.name = "Social";
		socialCategory.save();

		Account a1 = new Account();
		a1.name = "Facebook";
		a1.vault = workVault;
		a1.category = socialCategory;
		a1.save();

		Account a2 = new Account();
		a2.name = "Facebook";
		a2.vault = personalVault;
		a2.category = socialCategory;
		a2.save();
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
		// Clear all the info in the tables
		for (TableInfo table : Cache.getTableInfos()) {
			ActiveAndroid.execSQL("delete from " + table.getTableName());
		}

		Vault vault = new Vault("Personal", null, null, 2500, null);
		vault.save();

		insertAccount("Reddit", "Entertainment", vault);
		insertAccount("Xda-Developers", "Development", vault);
		insertAccount("Twitter", "Social", vault);
		insertAccount("Bank", "Financial", vault);
		insertAccount("Amazon", "Retail", vault);
	}

	private void insertAccount(String name, String categoryName, Vault vault) {
		Category c = new Category(categoryName);
		c.save();

		Account a = new Account(name, c, null, null, vault);
		a.save();
	}

	@Click(R.id.testFrags)
	void testGenericFrag() {
		startActivity(new Intent(this, ActFragTest_.class));
	}

}
