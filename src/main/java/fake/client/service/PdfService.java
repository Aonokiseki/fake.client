package fake.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class PdfService {
	private PdfService() {}
	
	public static List<String> extract(InputStream in) throws IOException{
		PdfReader reader = new PdfReader(in);
		List<String> pageContents = new LinkedList<String>();
		int pageCount = reader.getNumberOfPages();
		String pageContent = null;
		for(int i = 1; i <= pageCount; i++) {
			pageContent = PdfTextExtractor.getTextFromPage(reader, i);
			pageContents.add(pageContent);
		}
		reader.close();
		return pageContents;
	}
}
