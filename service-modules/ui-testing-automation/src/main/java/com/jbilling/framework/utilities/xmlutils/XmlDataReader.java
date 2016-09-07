package com.jbilling.framework.utilities.xmlutils;

import java.util.Map;

import com.jbilling.framework.globals.GlobalConsts;
import com.jbilling.framework.utilities.textutilities.TextUtilities;

public class XmlDataReader {
	DomParser dp;

	public XmlDataReader(final String xmlFileName) {
		this._InitializeDomParser(GlobalConsts.DirectoryPathTestData, xmlFileName);
	}

	protected XmlDataReader(final String xmlDir, final String xmlFileName) {
		this._InitializeDomParser(xmlDir, xmlFileName);
	}

	private void _InitializeDomParser(final String xmlDir, final String xmlFileName) {
		this.dp = new DomParser(xmlDir, xmlFileName);
	}

	public String readData(String dataSetName, String keyName, String category) {
		Map<String, String> ln = this.readData(dataSetName, category);

		String value = "";
		for (Map.Entry<String, String> entry : ln.entrySet()) {
			String key = entry.getKey();
			if (TextUtilities.equalsIgnoreCase(key, keyName)) {
				value = entry.getValue();
				if (value.contains("{RANDOM}")) {
					value = value.replace("{RANDOM}", "") + TextUtilities.getRandomString(3);
				}
				if (value.contains("{RANDOMNUM}")) {
					value = value.replace("{RANDOMNUM}", "") + TextUtilities.getRandomNumber(3);
				}
			}
		}

		return value.trim();
	}

	protected Map<String, String> readData(String dataSetName, String category) {
		Map<String, String> ln = this.dp.getTestDataSetNodesWithValues(GlobalConsts.TestEnvironment, dataSetName, category);
		return ln;
	}
}