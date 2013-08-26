package com.nakedferret.simplepass;

import android.net.Uri;

interface IPasswordListener {
	void onPasswordEntered(Uri vault);

	void onPasswordCleared(Uri vault);
}