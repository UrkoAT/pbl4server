package pbl4server.api.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GlobalUtils {

	public static Properties loadPropertiesFile(String filePath) {
		Properties props = new Properties();
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(filePath);
			props.load(iStream);
			iStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
}
