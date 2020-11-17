package ioHandler;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

//import utility.TimeStamp;

import org.apache.commons.collections4.*;

public class InputHandler {
	public static ArrayList<String>readFileHeader(File file) throws IOException{
		ArrayList<HashMap<String,String>> out = new ArrayList<HashMap<String,String>>();
		FileInputStream f = new FileInputStream(file);
		XSSFWorkbook myWorkBook = new XSSFWorkbook (f);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet.iterator();
		Row row = rowIterator.next();
		Iterator<Cell> i = row.cellIterator();
		ArrayList<String> header = new ArrayList<String>();
		while(i.hasNext())
			header.add(i.next().getStringCellValue());
		return header;
	}
	
	public static ArrayList<HashMap<String,String>>readFileContent(File file) throws IOException{
		ArrayList<HashMap<String,String>> out = new ArrayList<HashMap<String,String>>();
		FileInputStream f = new FileInputStream(file);
		XSSFWorkbook myWorkBook = new XSSFWorkbook (f);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet.iterator();
		Row row = rowIterator.next();
		Iterator<Cell> i = row.cellIterator();
		ArrayList<String> header = new ArrayList<String>();
		while(i.hasNext())
			header.add(i.next().getStringCellValue());
		while(rowIterator.hasNext()) {
			row = rowIterator.next();
			HashMap<String,String> h = new HashMap<String,String>();
			i = row.cellIterator();
			while(i.hasNext()) {
				Cell c = i.next();
				if(c.getCellType() == CellType.STRING)
					h.put(header.get(c.getColumnIndex()), c.getStringCellValue());
				else if (c.getCellType() == CellType.NUMERIC)
					h.put(header.get(c.getColumnIndex()), ""+c.getNumericCellValue());
				else
					h.put(header.get(c.getColumnIndex()), "");
			}
			out.add(h);
		}
		return out;
	}
	
}