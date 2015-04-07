/**
 * 
 */
package net.sf.jabb.quartz;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;

/**
 * Utility class to integrate myschedule
 * @author James Hu
 *
 */
public class SchedulerUtility {

	/**
	 * Get the settings names and full names of all schedulers defined in myschedule.
	 * @return Map of <settings name, full name>
	 */
	public static Map<String, String> getSchedulerNames(){
		Map<String, String> schedulerNames = new HashMap<String, String>();
		MySchedule mySchedule = MySchedule.getInstance();
		for(String settingsName: mySchedule.getSchedulerSettingsNames()){
			SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
			schedulerNames.put(settingsName, settings.getSchedulerFullName());
		}
		return schedulerNames;
	}

	/**
	 * Get the settings names and corresponding schedulers of all schedulers defined in myschedule.
	 * @return Map of <settings name, scheduler>
	 */
	public static Map<String, SchedulerTemplate> getSchedulers(){
		Map<String, SchedulerTemplate> schedulers = new HashMap<String, SchedulerTemplate>();
		MySchedule mySchedule = MySchedule.getInstance();
		for(String settingsName: mySchedule.getSchedulerSettingsNames()){
			SchedulerTemplate schedulerTemplate = mySchedule.getScheduler(settingsName);
			schedulers.put(settingsName, schedulerTemplate);
		}
		return schedulers;
	}

	/**
	 * Convert a text in properties file format to DataMap that can be used by Quartz
	 * @param text	the text in properties file format 
	 * @return a Map that can be used as JobDataMap by Quartz
	 * @throws IOException if the text is not in proper properties file format
	 */
	public static Map<String, Object> convertTextToDataMap(String text) throws IOException{
		Map<String, Object> dataMap = null; 
		Properties p = new Properties();
		p.load(new StringReader(text));
			
		dataMap = new HashMap<String, Object>();
		for (Entry<Object, Object> entry: p.entrySet()){
			dataMap.put((String)entry.getKey(), entry.getValue());
		}
		return dataMap;
	}
	
	/**
	 * Convert JobDataMap into text in properties file format
	 * @param dataMap the JobDataMap
	 * @return a text in properties file format
	 */
	public static String convertDataMapToText(Map<String, Object> dataMap){
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry: dataMap.entrySet()){
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append('\n');
		}
		return sb.toString();
	}

}
