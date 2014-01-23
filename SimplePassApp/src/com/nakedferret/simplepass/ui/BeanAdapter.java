package com.nakedferret.simplepass.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nakedferret.simplepass.ui.BeanAdapter.ListItem;

public class BeanAdapter<T extends ListItem> extends BaseAdapter {

	private ListItemBinder<T> binder;
	private List<T> beans;
	private Context c;

	public BeanAdapter(Context c, List<T> beans, ListItemBinder<T> binder) {
		this.c = c;
		if (beans == null)
			beans = new ArrayList<T>();
		this.beans = beans;
		this.binder = binder;
	}

	public BeanAdapter(Context c, ListItemBinder<T> binder) {
		this(c, null, binder);
	}

	public void addAll(Collection<T> collection) {
		for (T t : collection) {
			beans.add(t);
		}
	}

	@Override
	public int getCount() {
		return beans.size();
	}

	@Override
	public T getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		T bean = getItem(position);
		if (convertView == null)
			convertView = binder.newView(c, convertView, parent);
		return binder.bindView(c, convertView, position, bean);
	}

	public interface ListItemBinder<T> {

		View newView(Context c, View convertView, ViewGroup parent);

		View bindView(Context c, View convertView, int position, T bean);
	}

	public interface ListItem {
		long getId();
	}

}
