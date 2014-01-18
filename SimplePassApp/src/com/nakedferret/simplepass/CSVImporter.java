package com.nakedferret.simplepass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.googlecode.androidannotations.annotations.EBean;

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

	public void setFile(File file) {
		this.file = file;
	}

	// Checks to see if CSV file is valid. If it is, it scans to see how many
	// columns it contains
	public void process() {
		resetReader();

		int maxColumns = -1;
		String[] line;
		boolean isCSV = false;

		while ((line = reader.readNext()) != null) {
			if (maxColumns == -1)
				maxColumns = line.length;

			isCSV = true;

			if (maxColumns != line.length) {
				isCSV = false;
				break;
			}
		}

		if (isCSV) {
			isValid = true;
			width = maxColumns;
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

	private void resetReader() {
		closeReader();

		try {
			reader = new SafeCSVReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeReader() {
		if (reader != null)
			reader.close();
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

	public String getFirstRow() {
		final Charset charSet = Charset.forName("UTF-8");

		String row = null;
		BufferedReader br = null;

		try {
			InputStream fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, charSet));
			row = br.readLine();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return row;
	}

	// Used to keep info for future accounts to import
	public class MockAccount {
		public String name, username, password, category;
	}

	public List<MockAccount> getAccounts(int[] mappings) {

		resetReader();
		List<MockAccount> accounts = new ArrayList<MockAccount>();
		String[] line;

		while ((line = reader.readNext()) != null) {
			MockAccount a = new MockAccount();
			a.name = line[mappings[COLUMN_MAPPING.NAME]];
			a.username = line[COLUMN_MAPPING.USERNAME];
			a.password = line[COLUMN_MAPPING.PASSWORD];
			a.category = line[COLUMN_MAPPING.CATEGORY];
			accounts.add(a);
		}
		reader.close();
		return accounts;
	}
}
