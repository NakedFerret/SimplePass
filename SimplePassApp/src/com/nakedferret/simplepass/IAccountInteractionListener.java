package com.nakedferret.simplepass;

import android.net.Uri;

public interface IAccountInteractionListener {

	// User selects a vault in the UI
	void onAccountSelected(Uri account);

	// Account created 
	void onAccountCreated(Uri account);

	// User request to create an Account
	void requestCreateAccount(Uri vault);

}
