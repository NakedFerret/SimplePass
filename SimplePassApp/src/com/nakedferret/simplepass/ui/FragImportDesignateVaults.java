package com.nakedferret.simplepass.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.MockAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.ViewHolder;

@EFragment
public class FragImportDesignateVaults extends ListFragment implements
		OnItemClickListener, Callback {

	private ActionMode mode;
	private ActImport activity;
	private ImportManager importManager;
	private ModifyDetailAdapter adapter;

	public FragImportDesignateVaults() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity attachingActivity) {
		super.onAttach(attachingActivity);
		activity = (ActImport) attachingActivity;
		importManager = activity.getImportManager();
	}

	@AfterViews
	void populateUI() {
		adapter = new ModifyDetailAdapter(getActivity(),
				importManager.getAccounts());
		setListAdapter(adapter);
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

			text1.setText(a.getName() + "\n" + a.getCategory());
			text2.setText(a.getUsername() + "\n" + a.getPassword());
			showInfoButton.setOnTouchListener(this);
			cb.setOnCheckedChangeListener(this);
			cb.setTag(position);
			cb.setChecked(importManager.isSelected(a));

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

		if (numOfSelectedAccounts == 0 && mode != null) {
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
		importManager.deselectAllAccounts();
		adapter.notifyDataSetChanged();
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
