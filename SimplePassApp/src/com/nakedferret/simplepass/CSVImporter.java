package com.nakedferret.simplepass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.googlecode.androidannotations.annotations.EBean;
import com.nakedferret.simplepass.ImportManager.ImportAccount;
import com.nakedferret.simplepass.ImportManager.Importer;

@EBean
public class CSVImporter implements Importer {

	public static class CSVMapping {
		final int name, username, password, category;

		public CSVMapping(int name, int username, int password, int category) {
			this.name = name;
			this.username = username;
			this.password = password;
			this.category = category;
		}

		public static final CSVMapping LASTPASS = new CSVMapping(4, 1, 2, 5);

		public static List<CSVMapping> mappings = new ArrayList<CSVMapping>();

		static {
			mappings.add(LASTPASS);
		}

		public static final CSVMapping getMapping(int type) {
			try {
				return mappings.get(type);
			} catch (Exception e) {
				return null;
			}
		}

		public static int addMapping(CSVMapping csvMapping) {
			int id = mappings.size();
			mappings.add(csvMapping);
			return id;
		}

	}

	private File file;
	private SafeCSVReader reader;

	private boolean isValid = false;
	private int width = -1;
	private String[] firstRow;
	private CSVMapping mapping;
	private List<ImportAccount> accounts = new ArrayList<ImportAccount>();

	public void prepare(File file, CSVMapping mapping) {
		this.file = file;
		this.mapping = mapping;
	}

	private void resetReader() {
		closeReader();

		try {
			reader = new SafeCSVReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void closeReader() {
		if (reader != null)
			reader.close();
	}

	// Checks to see if CSV file is valid. If it is, it scans to see how many
	// columns it contains
	public void process() {
		resetReader();

		int maxColumns = -1;
		String[] line;
		int position = 0;
		boolean isCSV = false;
		List<ImportAccount> parsedAccounts = new ArrayList<ImportAccount>();

		if ((line = reader.readNext()) != null) {
			isCSV = true;
			maxColumns = line.length;
			firstRow = line;
			if (mapping != null)
				parsedAccounts.add(getAccount(line, position++));
		}

		while ((line = reader.readNext()) != null) {
			if (maxColumns != line.length) {
				isCSV = false;
				break;
			}
			if (mapping != null)
				parsedAccounts.add(getAccount(line, position++));
		}

		if (isCSV) {
			isValid = true;
			width = maxColumns;
			accounts = parsedAccounts;
		}

		closeReader();
	}

	// CSV is not valid if the number of columns per line fluctuates
	public boolean isValid() {
		return isValid;
	}

	// Returns the number of columns in the CSV file
	public int getWidth() {
		return width;
	}

	public List<ImportAccount> getAccounts() {
		return accounts;
	}

	public String getFirstRow() {
		StringBuilder b = new StringBuilder();

		for (String c : firstRow) {
			b.append(c + ", ");
		}

		return b.substring(0, b.length() - 2);
	}

	private ImportAccount getAccount(String[] line, int id) {
		String name = line[mapping.name];
		String username = line[mapping.username];
		String password = line[mapping.password];
		String category = line[mapping.category];
		return new ImportAccount(name, username, password, category, id);
	}

	class SafeCSVReader extends CSVReader {

		public SafeCSVReader(Reader arg0) {
			super(arg0);
		}

		@Override
		public void close() {
			try {
				super.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String[] readNext() {
			// TODO Auto-generated method stub
			try {
				return super.readNext();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}
}
