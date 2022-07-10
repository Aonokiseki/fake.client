package fake.client.configurer;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectoryConfigurer {
	
	@Bean(name = "temp.directory")
	public File tempDirectory(@Value("${temp.dir.path}")String tempDirPath) throws IOException {
		File file = new File(tempDirPath);
		if(file == null || !file.isDirectory())
			return null;
		return file;
	}
	
	@Bean(name = "python.scripts.directory")
	public File pythonScriptsDirectory(
			@Value("${python.enabled}") Boolean enabled,
			@Value("${python.script.path}") String pythonScriptPath) throws IOException {
		boolean pathIsNullOrEmpty = pythonScriptPath == null || pythonScriptPath.isEmpty();
		if(pathIsNullOrEmpty && enabled)
			throw new IOException("enabled=%s, but scripts' path not exist.");
		File pythonScriptsDirectory = new File(pythonScriptPath);
		boolean scriptsDirectoryDisabled = !pythonScriptsDirectory.exists() || pythonScriptsDirectory.isFile();
		if(scriptsDirectoryDisabled && enabled)
			throw new IOException("enabled=%s, but scripts directory not exist or is a file.");
		return pythonScriptsDirectory;
	}
	
	@Bean(name = "python.interpreter")
	public File pythonInterpreter(
			@Value("${python.enabled}") Boolean enabled,
			@Value("${python.interpreter.path}") String pythonInterpreterPath) throws IOException {
		boolean pathIsNullOrEmpty = pythonInterpreterPath == null || pythonInterpreterPath.isEmpty();
		if(pathIsNullOrEmpty && enabled)
			throw new IOException("enabled=%s, but interpreter's path not exist.");
		File pythonInterpreter = new File(pythonInterpreterPath);
		boolean interpreterDisabled = !pythonInterpreter.exists() || pythonInterpreter.isDirectory();
		if(interpreterDisabled && enabled)
			throw new IOException("enabled=%s, but interpreter not exist or is a directory.");
		return pythonInterpreter;
	}
}