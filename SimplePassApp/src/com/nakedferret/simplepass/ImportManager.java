package com.nakedferret.simplepass;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;
import com.nakedferret.simplepass.CSVImporter.CSVMapping;
import com.nakedferret.simplepass.ui.BeanAdapter.ListItem;

@SuppressLint("UseSparseArrays")
@EBean(scope = Scope.Singleton)
public class ImportManager {

	public static final class FILE_TYPE {
		public static final int CSV = 0;
	}

	public interface Importer {
		public List<ImportAccount> getAccounts();
	}

	public Map<Long, ImportAccount> selectedAccounts = new HashMap<Long, ImportAccount>();

	@Bean
	CSVImporter csvImporter;

	private Importer currentImporter;

	public List<ImportAccount> getAccounts() {
		return currentImporter.getAccounts();
	}

	public void setAccountSelection(ImportAccount a, boolean selected) {
		// Add to selectedAccounts
		if (selected) {
			selectedAccounts.put(a.id, a);
		} else {
			selectedAccounts.remove(a.id);
		}
	}

	public Collection<ImportAccount> getSelectedAccounts() {
		return selectedAccounts.values();
	}

	public ImportAccount getSelectedAccount(long id) {
		return selectedAccounts.get(id);
	}

	public void deselectAllAccounts() {
		selectedAccounts.clear();
	}

	public void processCSV(Uri fileUri, CSVMapping mapping) {
		currentImporter = csvImporter;
		csvImporter.prepare(new File(fileUri.getPath()), mapping);
		csvImporter.process();
	}

	public boolean isSelected(ImportAccount a) {
		return selectedAccounts.get(a.id) != null;
	}

	public void deleteSelectedAccounts() {
		getAccounts().removeAll(selectedAccounts.values());
		selectedAccounts.clear();
	}

	public static class ImportAccount implements ListItem {
		private String name, username, password, category;
		private long id;

		public ImportAccount(String name, String username, String password,
				String category, long id) {
			this.name = name;
			this.username = username;
			this.password = password;
			this.category = category;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public String getCategory() {
			return category;
		}

		public long getId() {
			return id;
		}

	}
}
