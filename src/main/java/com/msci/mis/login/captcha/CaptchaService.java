package com.msci.mis.login.captcha;

import java.net.URI;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class CaptchaService implements ICaptchaService {


	private CaptchaSettings captchaSettings;
	
    private RestTemplate restTemplate;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    
    @Value("${google.verification.url}")
    private String googleVerificationUrl;
    
    @Override
    public boolean processResponse(String response)  {
    	
        if(!responseSanityCheck(response)) {
            //throw new InvalidReCaptchaException("Verify you're human!");
        	return false;
        }
 
        URI verifyUri = URI.create(String.format(
          googleVerificationUrl+"?secret=%s&response=%s",
          getCaptchaSettings().getSecret(), response));
 
        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
		return googleResponse.isSuccess();
    }
 
	private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public void setCaptchaSettings(CaptchaSettings captchaSettings) {
		this.captchaSettings= captchaSettings;
		
	}

	public CaptchaSettings getCaptchaSettings() {
		return captchaSettings;
	}

	
}
