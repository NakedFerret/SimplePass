package com.nakedferret.simplepass.ui;

import android.content.Intent;
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
import com.nakedferret.simplepass.R;
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
