package com.nakedferret.simplepass.ui;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ui.Importer.MockAccount;
import com.nakedferret.simplepass.utils.ViewHolder;

@EFragment
public class FragImportDesignateVaults extends ListFragment implements
		OnItemClickListener, Callback {

	@FragmentArg
	int nameColumn, usernameColumn, passwordColumn, categoryColumn;

	@FragmentArg
	Uri fileUri;

	@Bean
	CSVImporter importer;

	private HashMap<Integer, MockAccount> selectedAccounts = new HashMap<Integer, MockAccount>();
	private ActionMode mode;

	public FragImportDesignateVaults() {
		// Required empty public constructor
	}

	@AfterViews
	void init() {
		processFile();
	}

	@Background
	void processFile() {
		int[] mapping = new int[] { nameColumn, usernameColumn, passwordColumn,
				categoryColumn };

		importer.prepare(new File(fileUri.getPath()), mapping);
		importer.process();

		populateUI();
	}

	@UiThread
	void populateUI() {
		setListAdapter(new ModifyDetailAdapter(getActivity(),
				importer.getAccounts()));
		getListView().setOnItemClickListener(this);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
						R.layout.listitem_importer_detail, parent, false);
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

			ListView listView = getListView();
			if (listView.isItemChecked(position) != isChecked)
				listView.setItemChecked(position, isChecked);

			handleActionMode();
		}

	}

	public void handleActionMode() {

		if (selectedAccounts.size() == 1 && mode == null)
			mode = getActivity().startActionMode(this);

		if (selectedAccounts.size() == 0 && mode != null) {
			mode.finish();
			mode = null;
		}

	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CheckBox cb = ViewHolder.get(view, android.R.id.checkbox);
		cb.setChecked(!cb.isChecked());
	}

}
