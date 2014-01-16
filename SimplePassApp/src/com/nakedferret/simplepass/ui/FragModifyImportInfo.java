package com.nakedferret.simplepass.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.R.id;
import com.nakedferret.simplepass.R.layout;

@EFragment
public class FragModifyImportInfo extends ListFragment implements
		OnItemClickListener {

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
		getListView().setOnItemClickListener(this);
	}

	class ModifyDetailAdapter extends BaseAdapter implements OnTouchListener {

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
		public View getView(int position, View r, ViewGroup parent) {

			if (r == null) {
				r = LayoutInflater.from(context).inflate(
						R.layout.importer_detail_listview, parent, false);
			}

			TextView text1 = ViewHolder.get(r, R.id.text1);
			TextView text2 = ViewHolder.get(r, R.id.text2);
			ImageButton showInfoButton = ViewHolder.get(r, R.id.showInfoButton);
			MockAccount a = getItem(position);

			text1.setText(a.name + "\n" + a.category);
			text2.setText(a.username + "\n" + a.password);

			showInfoButton.setOnTouchListener(this);
			return r;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			View p;
			TextView text2;

			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				p = (View) v.getParent();
				text2 = ViewHolder.get(p, R.id.text2);
				text2.setVisibility(View.VISIBLE);
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				p = (View) v.getParent();
				text2 = ViewHolder.get(p, R.id.text2);
				text2.setVisibility(View.INVISIBLE);
				return true;
			default:
				return false;
			}
		}
	}

	// http://www.piwai.info/android-adapter-good-practices/
	static class ViewHolder {
		// I added a generic return type to reduce the casting noise in
		// client code
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}

	class MockAccount {
		String name, username, password, category;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Utils.log(this, "list clicked");
	}

}
