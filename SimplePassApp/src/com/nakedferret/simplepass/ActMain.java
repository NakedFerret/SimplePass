package com.nakedferret.simplepass;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity(R.layout.act_main)
public class ActMain extends SherlockActivity {

	@AfterViews
	void initDatabase() {
		new PasswordStorageDbHelper(this).getWritableDatabase();
	}

}
