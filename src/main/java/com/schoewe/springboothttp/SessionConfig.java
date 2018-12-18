package com.schoewe.springboothttp;

import java.util.List;
import java.util.Set;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.session.hazelcast.HazelcastSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@EnableHazelcastHttpSession
@Configuration
@Order(1)
public class SessionConfig {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static final String SESSION_CACHE_KEY = "spring:session:sessions";
	public static final String SP_CACHE_KEY      = "msci:service-provider:sp";
	public static final String USER_REG_CACHE_KEY="msci:auth-service:user";
	public static final String SERVICE_ACCOUNT_CACHE_KEY="msci:service-account:user";
	
	
    private Integer maxInactiveIntervalInSeconds=120;
	
	@Value("#{'${session.replication.hostnameMembers}'.split(',')}")
	private List<String> sessionReplicationHostnameMembers;

	
	
    @Primary
    @Bean
    public HazelcastSessionRepository sessionRepository(HazelcastInstance instance) {
    	HazelcastSessionRepository sessionRepository = new HazelcastSessionRepository(instance.getMap(SESSION_CACHE_KEY));
    	
        sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds); //allow specifying the session idle timeout from a properties file
        return sessionRepository;
    }
    @Bean
    public Set<String> cities(HazelcastInstance instance){
    	Set<String> cities =  instance.getSet(SERVICE_ACCOUNT_CACHE_KEY);
    	return cities;
    }
    @Bean(destroyMethod = "shutdown")
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();

		// Use TCP member discovery instead of multicast
		JoinConfig joinConfig = config.getNetworkConfig().getJoin();
		joinConfig.getMulticastConfig().setEnabled(false);
		joinConfig.getTcpIpConfig().setEnabled(true).setMembers(sessionReplicationHostnameMembers);
		log.info("Replication members: " + sessionReplicationHostnameMembers);
		
		config.setProperty("hazelcast.jmx", "true");

		MapAttributeConfig attributeConfig = new MapAttributeConfig()
				.setName(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
				.setExtractor(PrincipalNameExtractor.class.getName());

		config.getMapConfig(SESSION_CACHE_KEY)
				.addMapAttributeConfig(attributeConfig)
				.addMapIndexConfig(new MapIndexConfig(
						HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false));
		
		
		config.getSetConfig(SERVICE_ACCOUNT_CACHE_KEY);

		return Hazelcast.newHazelcastInstance(config);
	}
}