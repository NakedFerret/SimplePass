package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.nakedferret.simplepass.ServicePassword;

public class FragPasswordListener extends Fragment {

	public FragPasswordListener() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		LocalBroadcastManager.getInstance(activity).registerReceiver(
				passwordReceiver, ServicePassword.FILTER);
	}

	private BroadcastReceiver passwordReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	};

	void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {

	}

	void onVaultLocked(Uri vault) {

	}

}
