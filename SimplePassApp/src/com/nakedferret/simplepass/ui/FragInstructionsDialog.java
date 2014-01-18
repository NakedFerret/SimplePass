package com.nakedferret.simplepass.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;

@EFragment
public class FragInstructionsDialog extends DialogFragment {

	@FragmentArg
	int messageId;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setMessage(messageId);
		b.setPositiveButton(android.R.string.ok, null);
		return b.create();
	}

}
