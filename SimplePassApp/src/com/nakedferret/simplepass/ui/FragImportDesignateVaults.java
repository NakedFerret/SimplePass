package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
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
import com.nakedferret.simplepass.ImportManager.ImportAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ui.FragImportModifyAndSave.ImportAccountBinder;
import com.nakedferret.simplepass.utils.ViewHolder;

@EFragment
public class FragImportDesignateVaults extends ListFragment implements
		OnItemClickListener, Callback {

	@Bean
	ImportManager importManager;

	private ActionMode mode;
	private BeanAdapter<ImportAccount> adapter;
	private SelectableImportAccountBinder binder = new SelectableImportAccountBinder();
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
		adapter = new BeanAdapter<ImportAccount>(getActivity(),
				importManager.getAccounts(), binder);
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

	public class SelectableImportAccountBinder extends ImportAccountBinder
			implements OnCheckedChangeListener {

		@Override
		public View newView(Context c, View convertView, ViewGroup parent) {
			return LayoutInflater.from(c).inflate(
					R.layout.listitem_mockaccount_selectable, parent, false);
		}

		@Override
		public View bindView(Context c, View r, int position, ImportAccount bean) {
			r = super.bindView(c, r, position, bean);

			CheckBox cb = ViewHolder.get(r, android.R.id.checkbox);
			cb.setOnCheckedChangeListener(this);
			cb.setTag(position);
			cb.setChecked(importManager.isSelected(bean));

			return r;
		}

		@Override
		public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
			int position = (Integer) cb.getTag();
			importManager.setAccountSelection(adapter.getItem(position),
					isChecked);
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
