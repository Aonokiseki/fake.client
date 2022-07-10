package fake.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import fake.client.pojo.data.DocumentContent;

public class DocxService {
	private DocxService() {}
	
	public static DocumentContent extract(InputStream in) throws IOException{
		XWPFDocument document = new XWPFDocument(in);
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		List<XWPFTable> tables = document.getTables();
		DocumentContent result = new DocumentContent();
		List<String> paragraphContents = new ArrayList<String>(paragraphs.size());
		for(int i = 0, size = paragraphs.size(); i < size; i++)
			paragraphContents.add(paragraphs.get(i).getText());
		List<String> tableContents = new ArrayList<String>(tables.size());
		for(int i = 0, size = tables.size(); i < size; i++)
			tableContents.add(tables.get(i).getText());
		result.setParagraphContents(paragraphContents);
		result.setTableContents(tableContents);
		document.close();
		return result;
	}
}
