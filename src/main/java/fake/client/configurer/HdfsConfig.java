package fake.client.configurer;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class HdfsConfig {
	
	@Bean(name = "fileSystem")
	@RefreshScope
	public FileSystem hadoopFileSystem(
			@Value("${hadoop.enabled}") Boolean hadoopEnabled,
			@Value("${hadoop.default-fs}") String hadoopDefaultFs,
			@Value("${hadoop.user-name}") String hadoopUserName) throws IOException {
		if(hadoopEnabled != null && !hadoopEnabled.booleanValue())
			return null;
		System.setProperty("HADOOP_USER_NAME", hadoopUserName);
		System.setProperty("HADOOP_HOME", "/");
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		FileSystem fs = FileSystem.get(URI.create(hadoopDefaultFs), configuration);
		return fs;
	}
}
