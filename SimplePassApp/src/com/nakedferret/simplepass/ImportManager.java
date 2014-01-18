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
import com.nakedferret.simplepass.CSVImporter.CSVMapping;

@SuppressLint("UseSparseArrays")
@EBean
public class ImportManager {

	public static final class FILE_TYPE {
		public static final int CSV = 0;
	}

	public interface Importer {
		public List<MockAccount> getAccounts();
	}

	public Map<Integer, MockAccount> selectedAccounts = new HashMap<Integer, MockAccount>();

	@Bean
	CSVImporter csvImporter;

	private Importer currentImporter;

	public List<MockAccount> getAccounts() {
		return currentImporter.getAccounts();
	}

	public void setAccountSelection(MockAccount a, boolean selected) {

		// Add to selectedAccounts
		if (selected) {
			selectedAccounts.put(a.id, a);
		} else {
			selectedAccounts.remove(a.id);
		}
	}

	public Collection<MockAccount> getSelectedAccounts() {
		return selectedAccounts.values();
	}

	public void removeSelectedAccounts() {
		selectedAccounts.clear();
	}

	public void processCSV(Uri fileUri, CSVMapping mapping) {
		currentImporter = csvImporter;
		csvImporter.prepare(new File(fileUri.getPath()), mapping);
		csvImporter.process();
	}

	// Used to keep info for future accounts to import
	public static class MockAccount {
		private String name, username, password, category;
		private int id;

		public MockAccount(String name, String username, String password,
				String category, int id) {
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

		public int getId() {
			return id;
		}

	}

	public boolean isSelected(MockAccount a) {
		return selectedAccounts.get(a.id) != null;
	}

}
