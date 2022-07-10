package fake.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

public class XlsService {
	private XlsService() {}
	
	public static List<List<Map<String,String>>> extract(InputStream in, boolean firstRowAsTitle) throws IOException{
		List<List<Map<String,String>>> result = new LinkedList<List<Map<String,String>>>();
		HSSFWorkbook workbook = new HSSFWorkbook(in);
		HSSFSheet sheet = null;
		List<Map<String, String>> sheetContent = null;
		for(int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {
			sheet = workbook.getSheetAt(sheetNo);
			if(sheet.getPhysicalNumberOfRows() == 0)
				continue;
			sheetContent = firstRowAsTitle? extractSheet(sheet) : extractSheetWithoutTitle(sheet);
			result.add(sheetContent);
		}
		workbook.close();
		return result;
	}
	
	private static List<Map<String,String>> extractSheet(HSSFSheet sheet){
		List<Map<String,String>> result = new LinkedList<Map<String,String>>();
		List<String> columnNames = new LinkedList<String>();
		Map<String,String> rowContent = null; HSSFRow row = null; HSSFCell cell = null; CellType type = null;
		HSSFRow title = sheet.getRow(0);
		if(title == null || title.getPhysicalNumberOfCells() == 0)
			return result;
		for(int cellNo = 0; cellNo < title.getLastCellNum(); cellNo++) {
			cell = title.getCell(cellNo);
			if(cell == null) {
				columnNames.add("Column_" + cellNo);
				continue;
			}
			type = cell.getCellType();
			switch(type) {
				case NUMERIC: columnNames.add(String.valueOf(cell.getNumericCellValue())); break;
				case BOOLEAN: columnNames.add(String.valueOf(cell.getBooleanCellValue())); break;
				case BLANK: columnNames.add("Column_" + cellNo); break;
				case FORMULA: columnNames.add(cell.getCellFormula()); break;
				default: columnNames.add(cell.getStringCellValue());
			}
		}
		for(int rowNo = 1; rowNo < sheet.getPhysicalNumberOfRows(); rowNo++) {
			row = sheet.getRow(rowNo);
			rowContent = extractRow(row, columnNames);
			result.add(rowContent);
		}
		return result;
	}
	
	private static List<Map<String,String>> extractSheetWithoutTitle(HSSFSheet sheet){
		List<Map<String,String>> result = new LinkedList<Map<String,String>>();
		List<String> columnNames = new LinkedList<String>();
		Map<String,String> rowContent = null; HSSFRow row = null;
		int lastestCellNum = 0;
		for(int rowNo = 0; rowNo < sheet.getPhysicalNumberOfRows(); rowNo++)
			lastestCellNum = Math.max(lastestCellNum, sheet.getRow(rowNo).getLastCellNum());
		for(int i = 0; i < lastestCellNum; i++)
			columnNames.add("Column_" + i);
		for(int rowNo = 0; rowNo < sheet.getPhysicalNumberOfRows(); rowNo++) {
			row = sheet.getRow(rowNo);
			rowContent = extractRow(row, columnNames);
			result.add(rowContent);
		}
		return result;
	}
	
	private static Map<String,String> extractRow(HSSFRow row, List<String> columnNames){
		Map<String,String> result = new LinkedHashMap<String,String>();
		HSSFCell cell = null; CellType type = null;
		for(int cellNo = 0; cellNo < columnNames.size(); cellNo++) {
			if(row == null || cellNo >= row.getLastCellNum()) {
				result.put(columnNames.get(cellNo), "");
				continue;
			}
			cell = row.getCell(cellNo);
			if(cell == null) {
				result.put(columnNames.get(cellNo), "");
				continue;
			}
			type = cell.getCellType();
			if(type == CellType.NUMERIC) {
				result.put(columnNames.get(cellNo), String.valueOf((int)(cell.getNumericCellValue())));
				continue;
			}
			if(type == CellType.STRING) {
				result.put(columnNames.get(cellNo), cell.getStringCellValue());
				continue;
			}
		}
		return result;
	}
}
