package com.nakedferret.simplepass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.UiThread;

@EFragment
public class FragModifyImportInfo extends ListFragment {

	@FragmentArg
	int nameColumn, usernameColumn, passwordColumn, categoryColumn;

	@FragmentArg
	Uri fileUri;

	@Bean
	CSVImporter importer;

	public FragModifyImportInfo() {
		// Required empty public constructor
	}

	@AfterViews
	void init() {
		processFile();
	}

	@Background
	void processFile() {
		importer.setFile(new File(fileUri.getPath()));
		importer.process();
		List<MockAccount> accounts = new ArrayList<MockAccount>();

		for (String[] line : importer.readAll()) {
			MockAccount a = new MockAccount();
			a.name = line[nameColumn];
			a.username = line[usernameColumn];
			a.password = line[passwordColumn];
			a.category = line[categoryColumn];
			accounts.add(a);
		}

		populateUI(accounts);
	}

	@UiThread
	void populateUI(List<MockAccount> accounts) {
		setListAdapter(new ModifyDetailAdapter(getActivity(), accounts));
	}

	class ModifyDetailAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		private List<MockAccount> accounts;

		public ModifyDetailAdapter(Context c, List<MockAccount> mockAccounts) {
			context = c;
			inflater = LayoutInflater.from(c);
			accounts = mockAccounts;
		}

		@Override
		public int getCount() {
			return accounts.size();
		}

		@Override
		public MockAccount getItem(int position) {
			return accounts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.importer_detail_listview, parent, false);
			}

			ViewFlipper textFlipper = ViewHolder.get(convertView,
					R.id.textFlipper);
			ImageButton showInfoButton = ViewHolder.get(convertView,
					R.id.showInfoButton);

			MockAccount a = getItem(position);
			TextView text = (TextView) textFlipper.getCurrentView();
			text.setText(a.name);

			return convertView;
		}
	}

	class MockAccount {
		String name, username, password, category;
	}

}
