package com.msci.mis.login.captcha;

import org.springframework.web.client.RestTemplate;

public interface ICaptchaService {
	
	boolean processResponse(final String response);
	void setRestTemplate(RestTemplate restTemplate);
	void setCaptchaSettings(CaptchaSettings captchaSettings);
	
}
