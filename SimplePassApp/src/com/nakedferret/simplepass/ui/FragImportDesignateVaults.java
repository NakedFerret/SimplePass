package com.nakedferret.simplepass.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.MockAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.ViewHolder;

@EFragment
public class FragImportDesignateVaults extends ListFragment implements
		OnItemClickListener, Callback {

	@Bean
	ImportManager importManager;

	private ActionMode mode;
	private SelectableMockAccountAdapter adapter;
	private ActImport activity;
	private boolean proceeding = false; // True if proceeding with import

	public FragImportDesignateVaults() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity attachedActivity) {
		super.onAttach(attachedActivity);
		activity = (ActImport) attachedActivity;
	}

	@AfterViews
	void populateUI() {
		adapter = new SelectableMockAccountAdapter(getActivity(),
				importManager.getAccounts());
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	public void onStart() {
		super.onStart();
		// If the user backs out of later import stages. This fragment will be
		// shown again and this method will be called. Therefore this must be
		// reset
		proceeding = false;
	}

	public class SelectableMockAccountAdapter extends MockAccountAdapter
			implements OnCheckedChangeListener {

		public SelectableMockAccountAdapter(Context context,
				List<MockAccount> accounts) {
			super(context, accounts);
			layout = R.layout.listitem_mockaccount_selectable;
		}

		@Override
		public View getView(int position, View r, ViewGroup parent) {
			MockAccount a = getItem(position);

			r = super.getView(position, r, parent);
			CheckBox cb = ViewHolder.get(r, android.R.id.checkbox);
			cb.setOnCheckedChangeListener(this);
			cb.setTag(position);
			cb.setChecked(importManager.isSelected(a));

			return r;
		}

		@Override
		public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
			int position = (Integer) cb.getTag();

			MockAccount a = adapter.getItem(position);
			importManager.setAccountSelection(a, isChecked);

			ListView listView = getListView();
			if (listView.isItemChecked(position) != isChecked)
				listView.setItemChecked(position, isChecked);

			handleActionMode();
		}

	}

	public void handleActionMode() {
		int numOfSelectedAccounts = importManager.getSelectedAccounts().size();

		if (numOfSelectedAccounts >= 1 && mode == null)
			mode = getActivity().startActionMode(this);

		if (numOfSelectedAccounts == 0 && mode != null)
			mode.finish();

	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		if (!proceeding)
			importManager.deselectAllAccounts();
		adapter.notifyDataSetChanged();
		this.mode = null;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.ac_frag_import_designate_vaults,
				menu);
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_ignore_accounts:
			importManager.deleteSelectedAccounts();
			adapter.notifyDataSetChanged();
			return true;
		case R.id.action_import_accounts:
			proceeding = true;
			activity.onAccountsSelected();
			mode.finish();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CheckBox cb = ViewHolder.get(view, android.R.id.checkbox);
		cb.setChecked(!cb.isChecked());
	}

}
