package fake.client.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.poi.util.IOUtils;


public class FileUtils {
	private FileUtils() {}
	
	public static List<File> traversal(String entrance, String suffix, boolean containsDirectory){
		File root = new File(entrance);
		if(!root.exists())
			throw new IllegalArgumentException(String.format("[%s] not exist!", entrance));
		List<File> files = new LinkedList<File>();
		List<File> queue = new LinkedList<File>();
		File current = null; 
		File[] childs = null;
		queue.add(root);
		while(!queue.isEmpty()) {
			current = queue.remove(0);
			if(current.isFile()) {
				if(suffix == null || suffix.trim().isEmpty()|| current.getName().endsWith(suffix))
					files.add(current);
				continue;
			}
			if(containsDirectory)
				files.add(current);
			childs = current.listFiles();
			for(File child : childs)
				queue.add(child);
		}
		return files;
	}
	
	public static List<String> readAsList(InputStream in, String charsetName) throws IOException{
		List<String> list = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charsetName)));
		String line = null;
		while((line = reader.readLine())!= null) {
			if(line.trim().isEmpty())
				continue;
			list.add(line);
		}
		reader.close();
		return list;
	}
	
	/**
	 * 	解压缩文件(p.s. 目前中文有bug, 一定要英文目录)
	 * @param zipPath
	 * @param destDirPath
	 * @throws ArchiveException
	 * @throws IOException
	 */
	public static void unzip(InputStream zipInputStream, String destDirPath) throws ArchiveException, IOException {
		ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(
						ArchiveStreamFactory.ZIP,
						zipInputStream);
		ArchiveEntry entry;
		File file = null, parent = null;
		OutputStream out = null;
		while((entry = in.getNextEntry()) != null) {
			if(!in.canReadEntryData(entry))
				continue;
			file = new File(destDirPath, entry.getName());
			if(entry.isDirectory()) {
				if(!file.isDirectory() && !file.mkdirs())
					throw new IOException(String.format("[%s] failed to create directory", file.getAbsolutePath()));
				continue;
			}
			parent = file.getParentFile();
			if(!parent.isDirectory() && !parent.mkdirs())
				throw new IOException(String.format("[%s] failed to create directory", parent.getAbsolutePath()));
			out = Files.newOutputStream(file.toPath());
			IOUtils.copy(in, out);
		}
		in.close();
		out.close();
	}
}
