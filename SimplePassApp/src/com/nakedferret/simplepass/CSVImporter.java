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
import com.nakedferret.simplepass.ui.Importer.MockAccount;

@EBean
public class CSVImporter {

	public static final class MAPPING {
		public static final int LASTPASS = 0;
		public static final int OTHER = 1;
	}

	public static final class COLUMN_MAPPING {
		public static final int NAME = 0;
		public static final int USERNAME = 1;
		public static final int PASSWORD = 2;
		public static final int CATEGORY = 3;
	}

	private File file;
	private SafeCSVReader reader;

	private boolean isValid = false;
	private int width = -1;
	private String[] firstRow;
	private int[] mapping;
	private List<MockAccount> accounts = new ArrayList<MockAccount>();

	public void prepare(File file, int[] mapping) {
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
		boolean isCSV = false;
		List<MockAccount> parsedAccounts = new ArrayList<MockAccount>();

		if ((line = reader.readNext()) != null) {
			isCSV = true;
			maxColumns = line.length;
			firstRow = line;
			if (mapping != null)
				parsedAccounts.add(getAccount(line));
		}

		while ((line = reader.readNext()) != null) {
			if (maxColumns != line.length) {
				isCSV = false;
				break;
			}
			if (mapping != null)
				parsedAccounts.add(getAccount(line));
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

	public List<MockAccount> getAccounts() {
		return accounts;
	}

	public String getFirstRow() {
		StringBuilder b = new StringBuilder();

		for (String c : firstRow) {
			b.append(c + ", ");
		}

		return b.substring(0, b.length() - 2);
	}

	private MockAccount getAccount(String[] line) {
		MockAccount a = new MockAccount();
		a.name = line[mapping[COLUMN_MAPPING.NAME]];
		a.username = line[mapping[COLUMN_MAPPING.USERNAME]];
		a.password = line[mapping[COLUMN_MAPPING.PASSWORD]];
		a.category = line[mapping[COLUMN_MAPPING.CATEGORY]];
		return a;
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
