package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.TableInfo;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.Account;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Vault;

@EActivity(R.layout.act_main)
public class ActMain extends Activity {

	@ViewById
	Button insertButton;

	@ViewById
	Button testDataButton;

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

		Vault vault = Vault.createVault("Personal", "secret", 2500);
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
