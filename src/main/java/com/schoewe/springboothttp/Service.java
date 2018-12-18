package com.schoewe.springboothttp;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Set<String> cities;
	public Set<String> getCities() {
		return cities;
	}



	public void setCities(Set<String> cities) {
		this.cities = cities;
	}

	public boolean doesCityExist(String city) {
		logger.info("cities members: "+ getCities().size());
		return getCities().contains(city);		
	}



	public boolean addCity(String city) {
		logger.info("cities members: "+ getCities().size());
		return getCities().add(city);
	}
}
