package com.pradheep.event.EventManageTools;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import lombok.extern.log4j.Log4j;

@Log4j
public class EventSummaryGenerator {

	private CSVFileReader fileReader = new CSVFileReader();

	private EventDataMapper dataMapper = new EventDataMapper();

	private int duplicateCount = 0;

	private int adultCount = 0;

	private int childCount = 0;

	private int nonVegCount = 0;

	private int vegCount = 0;

	private int foodNotRequiredCount = 0;

	private Map<String, ParticipantInfo> cleanParticipantInfoMap = new HashMap<String, ParticipantInfo>();

	public List<ParticipantInfo> parseData(String fileName, String delimiter) {
		try {
			List<List<String>> rawData = fileReader.getExcelData(fileName, delimiter);
			List<ParticipantInfo> participantDataList = dataMapper.loadParticipantData(rawData);
			//log.info(rawData.toString());
			printSeperator();
			//log.info(participantDataList.toString());
			return participantDataList;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		return Collections.emptyList();
	}

	private void computeDuplicateRecords(List<ParticipantInfo> parsedData) {
		parsedData.stream().forEach(data -> {
			String userInfoKey = data.getName() + "_" + data.getMobileNumber() + "_" + data.getEmail();
			if (cleanParticipantInfoMap.containsKey(userInfoKey)) {
				duplicateCount++;
			}
			cleanParticipantInfoMap.put(userInfoKey, data);
		});
		log.info("Clean Records Size:" + cleanParticipantInfoMap.size());
		printSeperatorByName("Registration Summary");
		log.info("Duplicate records:" + duplicateCount);
	}

	private void countPeople(List<ParticipantInfo> parsedData) {
		adultCount = parsedData.stream().mapToInt(data -> Integer.parseInt(data.getAccompanyingAdults())).sum();
		log.info("Adult Count:" + adultCount);
		childCount = parsedData.stream().mapToInt(data -> Integer.parseInt(data.getAccompanyingChild())).sum();
		log.info("Child Count:" + childCount);
	}
	
	private void tallyCount() {
		int totalPeople = adultCount + childCount;
		log.info("Total count by people: " + totalPeople);
		int totalPeopleByFood = nonVegCount + vegCount + foodNotRequiredCount;
		log.info("Total count by food: " + totalPeopleByFood);
	}

	private void countBasedOnFoodType(List<ParticipantInfo> parsedData) {
		nonVegCount = parsedData.stream().mapToInt(data -> Integer.parseInt(data.getNonVegCount())).sum();
		log.info("Non Vegetarian Count:" + nonVegCount);
		vegCount = parsedData.stream().mapToInt(data -> Integer.parseInt(data.getVegCount())).sum();
		log.info("Vegeterian Count:" + vegCount);
		foodNotRequiredCount = parsedData.stream().mapToInt(data -> Integer.parseInt(data.getFoodNotRequiredCount()))
				.sum();
		log.info("Food Not Required Count:" + foodNotRequiredCount);
	}

	private void printSeperator() {
		log.info("--------------------------");
	}
	

	private void printSeperatorByName(String name) {
		log.info("-----" + name + "-----");
	}
	
	private List<ParticipantInfo> getCleanData() {
		List<ParticipantInfo> cleanList = new ArrayList();
		cleanParticipantInfoMap.keySet().forEach(key -> {
			cleanList.add(cleanParticipantInfoMap.get(key));
		});
		return cleanList;
	}

	public static void main(String args[]) {
		try (Scanner scanner = new Scanner(System.in)) {
			log.info("Enter the filename: ");
			String fileName = scanner.nextLine();
			log.info("Enter the delimiter: ");
			String delimiter = scanner.nextLine();
			EventSummaryGenerator generator = new EventSummaryGenerator();
			List<ParticipantInfo> participantDataList = generator.parseData(fileName, delimiter);
			generator.computeDuplicateRecords(participantDataList);
			generator.printSeperatorByName("By people");
			List<ParticipantInfo> cleanRecords = generator.getCleanData(); 
			generator.countPeople(cleanRecords);
			generator.printSeperatorByName("By food type");
			generator.countBasedOnFoodType(cleanRecords);			
			generator.printSeperatorByName("Tally count");
			generator.tallyCount();
			generator.printSeperator();
			log.info("Reports for: " + fileName);
		}	
	}
}
