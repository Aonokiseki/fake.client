package fake.client.service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import fake.client.util.FileUtils;
/**
 * 数据文件的文本排重<br>
 * <br>
 * 假设文件words.txt有众多行,如下:<br>
 * <br>
 * <table>
 *     <th>检索词</th>
 *     <tr><td>1.文化</td></tr>
 *     <tr><td>2.军事</td></tr>
 *     <tr><td>3.经济</td></tr>
 *     <tr><td>4.政治</td></tr>
 *     <tr><td>5.军事</td></tr>
 *     <tr><td>...</td></tr>
 * </table>
 * 
 * 直接做内排重可能会触发内存溢出;即使不触发内存溢出,这种全局排重也会拖累性能。
 * 这里的做法是取每一行的字符串的hashCode, 对相同质数取模, 模不同的字符串散落到不同文件,
 * hashCode相同(文本不一定一样)的字符串放到相同文件中，每个文件局部内排重,然后全局归并。<br>
 * <br>
 */
@Component
public class DuplicateCleanService {
    private final static int DEFAULT_DIVIDE = 7;
    private final static String DEFAULT_ENCODING = "UTF-8";
    private final static String DEFAULT_TEMP_FILE_BEFORE_CLEAN = "./temp/before";
    private final static String DEFAULT_TEMP_FILE_AFTER_CLEAN = "./temp/after";
    private List<BufferedWriter> writers;
    private InputStream input;
    private OutputStream output;
    private String encoding;
    private int divideNumber;
    
    public DuplicateCleanService() {
    	 this.encoding = DEFAULT_ENCODING;
         this.divideNumber = DEFAULT_DIVIDE;
    }
    
    public DuplicateCleanService(InputStream input, OutputStream output) {
    	this();
    	this.input = input;
        this.output = output;
    }
    public DuplicateCleanService(InputStream input, OutputStream output, String encoding) {
    	 this(input, output);
         this.encoding = encoding;
    }
    public DuplicateCleanService(InputStream input, OutputStream output, String encoding, int divideNumber){
    	this(input, output, encoding);
        this.divideNumber = divideNumber;
    }
    
    public List<BufferedWriter> getWriters() {
		return writers;
	}
	public void setWriters(List<BufferedWriter> writers) {
		this.writers = writers;
	}
	public InputStream getInput() {
		return input;
	}
	public void setInput(InputStream input) {
		this.input = input;
	}
	public OutputStream getOutput() {
		return output;
	}
	public void setOutput(OutputStream output) {
		this.output = output;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public int getDivideNumber() {
		return divideNumber;
	}
	public void setDivideNumber(int divideNumber) {
		this.divideNumber = divideNumber;
	}

	public void execute() throws IOException {
        initializeWriters();
        divideToDifferentFiles();
        redirectWriters();
        checkAllFilesDuplication();
        buildFinalFile();
        clearTempFiles();
    }

    private void initializeWriters() throws IOException{
        this.writers = new ArrayList<BufferedWriter>(this.divideNumber);
        for(int i=0; i<divideNumber; i++)
            writers.add(i, new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(DEFAULT_TEMP_FILE_BEFORE_CLEAN + "/" + i + ".txt"), Charset.forName(encoding))));
    }

    private void divideToDifferentFiles() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(encoding)));
        String current = null;
        int hashCode = -1; int mod = 0;BufferedWriter writer = null;
        while((current = reader.readLine()) != null){
            if(current.isEmpty())
                continue;
            hashCode = current.hashCode();
            /* 注意, floorMod 才是取模, 百分号是取余 */
            mod = Math.floorMod(hashCode, divideNumber);
            writer = writers.get(mod);
            writer.append(current).append(System.lineSeparator());
            writer.flush();
        }
        reader.close();
    }

    private void redirectWriters() throws IOException {
        for(int i=0; i<writers.size(); i++)
        	writers.get(i).close();
        this.writers.clear();
        for(int i=0; i<divideNumber; i++) 
        	 writers.add(new BufferedWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(DEFAULT_TEMP_FILE_AFTER_CLEAN + "/" + i + ".txt"), Charset.forName(encoding))));      
    }

    private void checkAllFilesDuplication() throws IOException {
       List<File> files = FileUtils.traversal(DEFAULT_TEMP_FILE_BEFORE_CLEAN, null, false);
       for(int i=0, size=Math.min(divideNumber, files.size()); i<size; i++) {
    	   removeDuplication(i, files.get(i));
       }
    }

    private void removeDuplication(int fileId, File file) throws IOException {
    	Set<String> contents = new HashSet<String>();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(encoding)));
        String line; BufferedWriter writer = null;
        while((line = reader.readLine())!=null){
            if(contents.contains(line))
            	continue;
            contents.add(line);
            writer = writers.get(fileId);
            writer.append(line).append(System.lineSeparator());
            writer.flush();
        }
        contents.clear();
        reader.close();
    }

    private void buildFinalFile() throws IOException {
        List<File> files = FileUtils.traversal(DEFAULT_TEMP_FILE_AFTER_CLEAN, null, false);
        BufferedWriter finalWriter = new BufferedWriter(new OutputStreamWriter(output, Charset.forName(encoding)));
        BufferedReader finalReader = null;
        String line;
        for(int i=0, size=Math.min(files.size(), divideNumber); i<size; i++){
            finalReader = new BufferedReader(
            		new InputStreamReader(new FileInputStream(files.get(i).getAbsolutePath()), Charset.forName(encoding)));
            while((line = finalReader.readLine())!=null) {
            	finalWriter.append(line).append(System.lineSeparator());
            }
            finalWriter.flush();
            /* 这个地方一定要关掉文件流, 不然无法删除临时文件*/
            finalReader.close();
        }
        finalWriter.close();
    }

    private void clearTempFiles() throws IOException {
        for(int i=0; i<writers.size(); i++)
        	writers.get(i).close();
        this.writers.clear();
        List<File> files = FileUtils.traversal(DEFAULT_TEMP_FILE_BEFORE_CLEAN, null, false);
        files.addAll(FileUtils.traversal(DEFAULT_TEMP_FILE_AFTER_CLEAN, null, false));
        for(int i=0,size=files.size(); i<size; i++)
            files.get(i).delete();
    }
}