package com.nakedferret.simplepass;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.activeandroid.query.Select;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.api.Scope;
import com.nakedferret.simplepass.CSVImporter.CSVMapping;
import com.nakedferret.simplepass.VaultManager.ResultListener;
import com.nakedferret.simplepass.ui.BeanAdapter.ListItem;
import com.nakedferret.simplepass.utils.Utils;

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
	VaultManager importVaultManager;

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

	public void unlockVault(Long vaultId, String password,
			ResultListener<Boolean> listener) {
		importVaultManager.unlockVault(vaultId, password, listener);
	}

	@Background
	public void importSelectedAccounts(Long selectedVault) {

		for (ImportAccount a : selectedAccounts.values()) {

			Category c = new Select()
					.from(Category.class)
					.where("name = ? collate nocase and name != ?", a.category,
							a.category).executeSingle();

			// There is no Category stored...
			if (c == null) {
				c = new Category(a.category);
				c.save();
			}

			importVaultManager.createAccount(selectedVault, c.getId(), a.name,
					a.username, a.password, null);
		}

		Utils.log(this, "finished adding accounts");
		deleteSelectedAccounts();
	}

	public static class ImportAccount implements ListItem {
		public String name, username, password, category;
		public long id;

		public ImportAccount(String name, String username, String password,
				String category, long id) {
			this.name = name;
			this.username = username;
			this.password = password;
			this.category = category;
			this.id = id;
		}

		public long getId() {
			return id;
		}

	}

}
