package com.nakedferret.simplepass;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.FragCreateVault.OnVaultCreatedListener;
import com.nakedferret.simplepass.FragDeviceSpeed.OnIterationsCalculatedListener;
import com.nakedferret.simplepass.FragListCursor.OnItemSelected;
import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

@EActivity(R.layout.act_frag_test)
public class ActFragTest extends ActFloating implements OnItemSelected,
		OnVaultCreatedListener, OnIterationsCalculatedListener {

	@Click(R.id.vaultButton)
	void showVaultsFragment() {
		int layout = android.R.layout.simple_list_item_1;
		Uri uri = Utils.buildContentUri(Vault.TABLE_NAME);
		String[] projection = { Vault.COL_NAME };
		int[] views = { android.R.id.text1 };
		String searchCol = Vault.COL_NAME;

		FragListCursor vaultsList = FragListCursor.newInstance(layout, uri,
				projection, views, searchCol);

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, vaultsList);
		t.commit();
	}

	@Click(R.id.accountsButton)
	void showAccountsFragment() {
		int layout = android.R.layout.simple_list_item_1;
		Uri uri = Utils.buildContentUri(Account.TABLE_NAME);
		String[] projection = { Account.COL_NAME };
		int[] views = { android.R.id.text1 };
		String searchCol = Account.COL_NAME;

		FragListCursor accountList = FragListCursor.newInstance(layout, uri,
				projection, views, searchCol);

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, accountList);
		t.commit();
	}

	@Click(R.id.createVault)
	void onCreateVault() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		Fragment f = FragCreateVault.newInstance(1000);
		t.replace(R.id.fragmentContainer, f);
		t.commit();
	}

	@Click(R.id.speedButton)
	void testSpeed() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		Fragment f = new FragDeviceSpeed_();
		t.replace(R.id.fragmentContainer, f);
		t.commit();
	}

	@Override
	public void onFragmentInteraction(Uri uri) {

	}

	@Override
	public void onVaultSelected(Uri uri) {

	}

	@Override
	public void onIterationsCalculated(int iters) {
		// TODO Auto-generated method stub
		
	}
}
