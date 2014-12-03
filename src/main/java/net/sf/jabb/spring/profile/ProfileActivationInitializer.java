/**
 * 
 */
package net.sf.jabb.spring.profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * @author James Hu
 *
 */
public class ProfileActivationInitializer implements Ordered, 
	ApplicationContextInitializer<ConfigurableApplicationContext> {

	public static final String PROPERTY_SOURCE_NAME = ProfileActivationInitializer.class.getSimpleName();
	public static final String PLACEHOLDER_NAME = "ini.mainProfile";
	public static final String HOSTNAME_PROPERTY_NAME = "profile.activation.hostname";
	public static final String SUBHOSTNAME_PROPERTY_NAME = "profile.activation.subhostname";
	public static final String PRIMARY_CONFIG_RESOURCE = "/hostname-profiles.properties";
	public static final String SECONDARY_CONFIG_RESOURCE = "/environments.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(ProfileActivationInitializer.class);
	
	protected ActiveProfilesChooser chooser = new ActiveProfilesChooser(HOSTNAME_PROPERTY_NAME, 
			SUBHOSTNAME_PROPERTY_NAME, PRIMARY_CONFIG_RESOURCE, SECONDARY_CONFIG_RESOURCE);

	/* (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;		// the first
	}
	
	/**
	 * This method does two things during the initialization stage of Spring context: 
	 * 1) If there is no active or default profile, activate one or more according to the hostname of the computer it is running on;
	 * 2) Makes the first active profile name available as property "env.mainProfile"
	 */
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		ConfigurableEnvironment env = context.getEnvironment();
		String[] activeProfiles = env.getActiveProfiles();
		if (activeProfiles == null || activeProfiles.length == 0){	// don't override settings in configuration files and command line argument
			activeProfiles = chooser.getActiveProfiles();
			if (activeProfiles != null && activeProfiles.length > 0){
				env.setActiveProfiles(activeProfiles);
				logger.info("Active profiles have been choosen as: " + Arrays.toString(activeProfiles));
			}else{
				logger.warn("No active profile has been choosen.");
			}
		}else{
			logger.info("Active profiles had already been specified as: " + Arrays.toString(activeProfiles));
		}
		
		// set additional properties
		activeProfiles = env.getActiveProfiles();
		if (activeProfiles != null && activeProfiles.length > 0){
			String mainProfile = activeProfiles[0];				// the first one is the "main" one
			MutablePropertySources propertySources = env.getPropertySources();
			Map<String, Object> additionalProperties = new HashMap<String, Object>();
			additionalProperties.put(PLACEHOLDER_NAME, mainProfile);
			propertySources.addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, additionalProperties));
			logger.info("Property (can be used as placeholder) '" + PLACEHOLDER_NAME + "' has been set to: " + mainProfile);
		}
	}
	

}
