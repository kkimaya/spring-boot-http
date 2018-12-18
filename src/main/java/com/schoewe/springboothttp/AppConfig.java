package com.schoewe.springboothttp;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.msci.mis.login.captcha.CaptchaService;
import com.msci.mis.login.captcha.CaptchaSettings;
import com.msci.mis.login.captcha.ICaptchaService;

@Import(SessionConfig.class)
@Order(2)
@Configuration 
public class AppConfig {
	@Autowired
	private SessionConfig sessionConfig;
	
	@Bean Service service() {
		Service service = new Service();
		service.setCities(sessionConfig.cities(sessionConfig.hazelcastInstance()));
		return service;
	}
	@Bean
	public ICaptchaService captchaService(){
		ICaptchaService captchaService = new CaptchaService();
		captchaService.setRestTemplate(restTemplateWithProxy());
		captchaService.setCaptchaSettings(captchaSettings());
		return captchaService;
	}
	@Bean
	public RestTemplate restTemplateWithProxy(){
		RestTemplate restTemplate = new RestTemplate();
		SimpleClientHttpRequestFactory requestFactory = new  SimpleClientHttpRequestFactory();
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("geoproxy.geo.msci.org", 8080));
		requestFactory.setProxy(proxy);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}
	@Bean 
	public CaptchaSettings captchaSettings(){
		CaptchaSettings captchaSettings = new CaptchaSettings();
		captchaSettings.setSecret("6LdeVw4UAAAAAMBhgtdWv7_00bv8V1U1lPdhBYee");
		captchaSettings.setSite("6LdeVw4UAAAAAEuhipOHVfsuDI3qaFiwfSjNHrAP");
		return captchaSettings;
	}
}
