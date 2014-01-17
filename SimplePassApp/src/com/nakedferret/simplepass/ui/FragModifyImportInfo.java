package com.nakedferret.simplepass.ui;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.CSVImporter.MockAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.Utils;
import com.nakedferret.simplepass.utils.ViewHolder;

@EFragment
public class FragModifyImportInfo extends ListFragment implements
		OnItemClickListener {

	@FragmentArg
	int nameColumn, usernameColumn, passwordColumn, categoryColumn;

	@FragmentArg
	Uri fileUri;

	@Bean
	CSVImporter importer;

	private Dialog dialog;

	private HashMap<Integer, MockAccount> selectedAccounts = new HashMap<Integer, MockAccount>();

	public FragModifyImportInfo() {
		// Required empty public constructor
	}

	@AfterViews
	void init() {
		processFile();
		showInstructions();
	}

	private void showInstructions() {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setMessage(R.string.pickVaultForImport);
		b.setPositiveButton(android.R.string.ok, null);
		dialog = b.create();
		dialog.show();
	}

	@Background
	void processFile() {
		importer.setFile(new File(fileUri.getPath()));
		importer.process();

		List<MockAccount> accounts = importer.getAccounts(nameColumn,
				usernameColumn, passwordColumn, categoryColumn);

		populateUI(accounts);
	}

	@UiThread
	void populateUI(List<MockAccount> accounts) {
		setListAdapter(new ModifyDetailAdapter(getActivity(), accounts));
		getListView().setOnItemClickListener(this);
	}

	class ModifyDetailAdapter extends BaseAdapter implements OnTouchListener,
			OnCheckedChangeListener {

		private Context context;
		private List<MockAccount> accounts;

		public ModifyDetailAdapter(Context c, List<MockAccount> mockAccounts) {
			context = c;
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
			CheckBox cb = ViewHolder.get(r, android.R.id.checkbox);
			MockAccount a = getItem(position);

			text1.setText(a.name + "\n" + a.category);
			text2.setText(a.username + "\n" + a.password);
			showInfoButton.setOnTouchListener(this);
			cb.setOnCheckedChangeListener(this);
			cb.setTag(position);
			cb.setChecked(selectedAccounts.get(position) != null ? true : false);

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

		@Override
		public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
			int position = (Integer) cb.getTag();
			MockAccount a = accounts.get(position);

			if (isChecked) {
				selectedAccounts.put(position, a);
			} else {
				selectedAccounts.remove(position);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Utils.log(this, "list clicked");
	}

	@Override
	public void onStop() {
		super.onStop();
		dialog.dismiss();
	}

}
