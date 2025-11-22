package com.pradheep.event.EventManageTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

@Log4j
public class EventDataMapper {

	private static final int FIRST_NAME_POSITION = 1;

	private static final int CONTACT_POSITION = 2;

	private static final int EMAIL_POSITION = 3;

	private static final int ACCOMPANYING_ADULT_POSITION = 6;

	private static final int ACCOMPANYING_CHILD_POSITION = 14;
	
	private static final int CHILD_LESS_THAN5YRS_POS = 20;

	private static final int WHO_INVITED_POSITION = 5;

	public Map<String, Integer> getFieldsMap() {
		HashMap<String, Integer> fieldsMap = new HashMap<>();
		fieldsMap.put("setName", FIRST_NAME_POSITION);
		fieldsMap.put("setEmail", EMAIL_POSITION);
		fieldsMap.put("setMobileNumber", CONTACT_POSITION);
		fieldsMap.put("setAccompanyingAdults", ACCOMPANYING_ADULT_POSITION);
		fieldsMap.put("setAccompanyingChild", ACCOMPANYING_CHILD_POSITION);
		fieldsMap.put("setWhoInvited", WHO_INVITED_POSITION);
		return fieldsMap;
	}

	private void addFoodCountInfo(List<String> listData, ParticipantInfo obj) {
		int nonVegCount = 0;
		int vegCount = 0;
		int foodNotRequired = 0;
		for (int i = ACCOMPANYING_ADULT_POSITION + 1; i < ACCOMPANYING_CHILD_POSITION; i++) {
			String foodPreference = listData.get(i);
			if(foodPreference.trim().equalsIgnoreCase("Non-Vegetarian - Hallal")) {
				nonVegCount++;
			} else if(foodPreference.trim().equalsIgnoreCase("Vegetarian")) {
				vegCount++;
			} else if(foodPreference.trim().equalsIgnoreCase("Food not required")) {
				foodNotRequired++;
			}
		}
		for (int i = ACCOMPANYING_CHILD_POSITION + 1; i < CHILD_LESS_THAN5YRS_POS; i++) {
			String foodPreference = listData.get(i);
			if(foodPreference.trim().equalsIgnoreCase("Non-Vegetarian - Hallal")) {
				nonVegCount++;
			} else if(foodPreference.trim().equalsIgnoreCase("Vegetarian")) {
				vegCount++;
			} else if(foodPreference.trim().equalsIgnoreCase("Food not required")) {
				foodNotRequired++;
			}
		}
		obj.setFoodNotRequiredCount(String.valueOf(foodNotRequired));
		obj.setNonVegCount(String.valueOf(nonVegCount));
		obj.setVegCount(String.valueOf(vegCount));
	}

	public List<ParticipantInfo> loadParticipantData(List<List<String>> rawInformation) {
		log.info("Loading the participant info:" + rawInformation.size());
		Class participantInfoClass = ParticipantInfo.class;
		Map<String, Integer> fieldsMap = getFieldsMap();
		List collection = new ArrayList();
		rawInformation.forEach(listData -> {
			ParticipantInfo participantInfo = new ParticipantInfo();
			fieldsMap.keySet().forEach(fieldName -> {
				try {
					Method fieldMethod = participantInfoClass.getDeclaredMethod(fieldName.toString(), String.class);
					String value = listData.get(fieldsMap.get(fieldName.toString()));
					fieldMethod.invoke(participantInfo, value);
					addFoodCountInfo(listData, participantInfo);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					log.error(e.getMessage(), e);
				}
			});
			log.info(participantInfo.toString());
			log.info("-------------------------------------------");
			collection.add(participantInfo);
		});
		return collection;
	}
}
