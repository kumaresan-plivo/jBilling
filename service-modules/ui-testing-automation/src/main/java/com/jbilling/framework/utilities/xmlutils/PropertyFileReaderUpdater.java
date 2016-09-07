package com.jbilling.framework.utilities.xmlutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.jbilling.framework.globals.GlobalConsts;
import com.jbilling.framework.globals.Logger;

public class PropertyFileReaderUpdater {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	public String readPropertyFromFile(final String key, String fileName) {
		String value = "";
		try {
			final Properties prop = new Properties();
			final File f = new File(GlobalConsts.getProjectDir() + "/test/automation/resources/" + fileName);
			PropertyFileReaderUpdater.logger.info("Read File :::::::::::" + f);
			if (f.exists()) {
				prop.load(new FileInputStream(f));
				value = prop.getProperty(key);
			}
		} catch (final Exception e) {
			PropertyFileReaderUpdater.logger.error("Failed to read from application.properties file.");
		}
		return value;
	}

	public void updatePropertyInFile(final String key, final String value, String fileName) {
		final Properties props = new Properties();
		final String propsFileName = GlobalConsts.getProjectDir() + "/test/automation/resources/" + fileName + ".properties";
		PropertyFileReaderUpdater.logger.info("Update File :::::::::::::" + propsFileName + "key: "+ key + "value: " + value);
		try {
			// first load old property file:
			final FileInputStream configStream = new FileInputStream(propsFileName);
			props.load(configStream);
			configStream.close();

			// modifies new property
			props.setProperty(key, value);

			// save modified property file
			final FileOutputStream output = new FileOutputStream(propsFileName);
			props.store(output, "");
			output.close();

		} catch (final IOException ex) {
			PropertyFileReaderUpdater.logger.info(ex);
		}
	}
}
