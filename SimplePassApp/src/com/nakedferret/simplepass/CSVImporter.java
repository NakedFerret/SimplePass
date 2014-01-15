package com.nakedferret.simplepass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;

import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class CSVImporter {

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

}
