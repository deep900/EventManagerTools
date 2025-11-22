package com.pradheep.event.EventManageTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class CSVFileReader {

	public List<List<String>> getExcelData(String filePath, String delimiter) throws FileNotFoundException {
		File inputFile = new File(filePath);
		if (!inputFile.exists()) {
			throw new FileNotFoundException("File not found :" + filePath);
		}
		FileReader fileReader = new FileReader(inputFile);
		List rawData = new ArrayList();
		try (BufferedReader reader = new BufferedReader(fileReader)) {
			String line;
			while ((line = reader.readLine()) != null) {
				List<String> data = Arrays.asList(line.split(delimiter));
				log.info(line);
				rawData.add(data);				
			}
		} catch (Exception err) {
			log.error(err.getMessage(), err);
		}
		return rawData;
	}
}
