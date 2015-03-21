/**
 * 
 */
package net.sf.jabb.spring.profile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import net.sf.jabb.util.bean.KeyValueBean;
import net.sf.jabb.util.bean.StringKeyValueBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;


/**
 * Choose active profiles according to the environment the code is running in.
 * @author James Hu
 *
 */
class ActiveProfilesChooser {
	private static final Logger logger = LoggerFactory.getLogger(ActiveProfilesChooser.class);
	private String hostnamePropertyName;
	private String subHostnamePropertyName;
	private String primaryConfigFileLocation;
	private String secondaryConfigFileLocation;
	
	/**
	 * Constructor.
	 * @param hostnamePropertyName		the property name to override the host name reported by the OS
	 * @param subHostnamePropertyName	the property name to provide finer control
	 * @param primaryEnvironmentsPropertiesLocation	primary location of the .properties file
	 * @param secondaryEnvironmentsPropertiesLocation	secondary location of the .properties file
	 */
	ActiveProfilesChooser(String hostnamePropertyName, String subHostnamePropertyName, 
			String primaryEnvironmentsPropertiesLocation, String secondaryEnvironmentsPropertiesLocation){
		this.hostnamePropertyName = hostnamePropertyName;
		this.subHostnamePropertyName = subHostnamePropertyName;
		this.primaryConfigFileLocation = primaryEnvironmentsPropertiesLocation;
		this.secondaryConfigFileLocation = secondaryEnvironmentsPropertiesLocation;
	}
	
	ActiveProfilesChooser(String hostnamePropertyName, String subHostnamePropertyName, 
			String environmentsPropertiesLocation){
		this(hostnamePropertyName, subHostnamePropertyName, environmentsPropertiesLocation, null);
	}
	
	/**
	 * Get the final host name that will be used to choose the active profiles.
	 * @return the (overridden) host name or the (overriden) host name + / + the (overriden) sub host name
	 */
	private String getHostname(){
		String hostname = null;
        try {
            // Use the OS hostname first.
            hostname = InetAddress.getLocalHost().getHostName().toLowerCase();
        } catch (UnknownHostException e) {
            logger.warn("Cannot find hostname.", e);
        }
        // Override by a system property second. Sometimes set when running in a container. 
        String hostnameOverride = System.getProperty(hostnamePropertyName);
        if (hostnameOverride != null && hostnameOverride.length() > 0){
        	hostname = hostnameOverride;
            logger.info("Overriden hostname '{}' defined in system property '{}' will be used to choose Spring profile", hostname, hostnamePropertyName);
        }else{
        	hostnameOverride = System.getenv(hostnamePropertyName.toUpperCase());
            if (hostnameOverride != null && hostnameOverride.length() > 0){
            	hostname = hostnameOverride;
                logger.info("Overriden hostname '{}' defined in environment variable '{}' will be used to choose Spring profile", hostname, hostnamePropertyName.toUpperCase());
            }else{
                logger.info("The hostname '{}' will be used to choose Spring profile", hostname);
            }
        }
        
        String subHostnameOverride = System.getProperty(subHostnamePropertyName);
        if (subHostnameOverride != null && subHostnameOverride.length() > 0){
        	hostname += "/" + subHostnameOverride;
            logger.info("Sub-hostname defined in system property '{}' appended. The full hostname that will be used to choose Spring profile is: {}", subHostnamePropertyName, hostname);
        }else{
        	subHostnameOverride = System.getenv(subHostnamePropertyName.toUpperCase());
            if (subHostnameOverride != null && subHostnameOverride.length() > 0){
            	hostname += "/" + subHostnameOverride;
                logger.info("Sub-hostname defined in environment variable '{}' appended. The full hostname that will be used to choose Spring profile is: {}", subHostnamePropertyName.toUpperCase(), hostname);
            }
        }

        return hostname;
	}
	
	/**
	 * Load hostname - profiles mapping configuration
	 * @return the configuration with all-matching wild card at the bottom.
	 */
	private List<StringKeyValueBean> loadConfiguration(){
		List<StringKeyValueBean> result = new LinkedList<StringKeyValueBean>();
		Resource resource = null;
		if (primaryConfigFileLocation == null){
			return result;		// fail silently because we must be in test
		}else{
			resource = new ClassPathResource(primaryConfigFileLocation);
			if (!resource.exists()){
				if (secondaryConfigFileLocation == null){
					return result;		// fail silently because we must be in test
				}
				resource = new ClassPathResource(secondaryConfigFileLocation);
			}
		}

		Properties properties = null;
		try {
			properties = PropertiesLoaderUtils.loadProperties(resource);
			
			// sort them, make the more specific ones at the top, and "*"/".*" at the bottom
			List<StringKeyValueBean> sorted = new ArrayList<StringKeyValueBean>(properties.size());
			for (Entry<Object, Object> entry: properties.entrySet()){
				sorted.add(new StringKeyValueBean(entry.getKey().toString(), entry.getValue().toString()));
			}
			Collections.sort(sorted, new Comparator<StringKeyValueBean>(){
				@Override
				public int compare(StringKeyValueBean b0,	StringKeyValueBean b1) {
					if (b0.getKey().equals(b1.getKey())){
						return 0;
					}
					if ("*".equals(b0) || ".*".equals(b0)){
						return 1;
					}else if ("*".equals(b1) || ".*".equals(b1)){
						return -1;
					}
					return b1.getKey().compareTo(b0.getKey());
				}
			});
			result.addAll(sorted);
		} catch (Exception e) {
			logger.warn("Cannot load environments configuration file from class path: " + 
					(resource == null ? "<null>" : resource.getDescription()), e);
		}
		
		return result;
	}
	
	/**
	 * Get active profiles according to all the factors.
	 * @return the active profiles
	 */
	public String[] getActiveProfiles(){
		String hostname = getHostname();
		List<StringKeyValueBean> config = loadConfiguration();
		for (StringKeyValueBean entry: config){
			String pattern = entry.getKey();
			if ("*".equals(pattern) || !(pattern.contains("\\.") || pattern.contains(".+") || pattern.contains(".*")
					|| StringUtils.containsAny(pattern, '[', '^', '$', '(', '|'))){	// it is not regular expression but with widecards
				String widecard = pattern;
				pattern = widecard.replace(".", "\\.")
						.replace("?", ".")
						.replace("*", ".*");
				if (!pattern.contains("/")){
					pattern += "/?.*";
				}
				logger.debug("Widecard '" + widecard + "' converted to regular expression: '" + pattern + "'.");
			}else{
				if (!pattern.contains("/")){
					pattern += "/?.*";
				}
				logger.debug("Regular expression: '" + pattern + "'.");
			}
			if (Pattern.matches(pattern, hostname)){
				String profiles = (String) entry.getValue();
				logger.debug("Hostname '" + hostname + "' matched by '" + pattern + "'");
				return profiles.split("[ ,\t]+");
			}
		}
		
		// matching not found
		logger.warn("No matching profiles can be found for '" + hostname + "', a profile derived from hostname will be activated.");
		String firstPart = StringUtils.substringBefore(hostname, ".");
		String profile = firstPart.replaceAll("[0-9-]+$", "");
		return new String[]{profile};
	}
}
