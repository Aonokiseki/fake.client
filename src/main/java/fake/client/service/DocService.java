package fake.client.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hwpf.HWPFDocument;

public class DocService {
	private DocService() {}
	
	public static String parse(InputStream in) throws IOException {
		HWPFDocument doc = new HWPFDocument(in);
		String documentText = doc.getDocumentText();
		doc.close();
		return documentText;
	}
}
