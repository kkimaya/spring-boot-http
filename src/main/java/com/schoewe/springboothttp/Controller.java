package com.schoewe.springboothttp;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.msci.mis.login.captcha.CaptchaService;
import com.msci.mis.login.captcha.ICaptchaService;

@RestController
@RequestMapping("/home")
public class Controller {

	@Value("${spring.cloud.consul.discovery.instanceId}")
	private String appInstance;
	
	@Autowired
	private Service service;
	private static final String cookieName = "APPSERVERID";

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ICaptchaService captchaService;
	private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";
	
	@RequestMapping(path = {"", "/"})
	public ModelAndView index(ModelAndView modelAndView) {
		modelAndView.addObject("test", "Hellow World");
		return setView(modelAndView, "home");
	}
	
	@RequestMapping("echo")
	public String echo(@RequestParam("msg") String msg, HttpServletResponse response) {
		if(StringUtils.isEmpty(msg)) {
			return "";
		}
		
		//Mimic what happens when the application is responsible for setting a cookie used for sticky-session
		//HAProxy will be responsible for looking at the 'APPSERVERID' cookie and routing accordingly
		response.addCookie(new Cookie(cookieName, appInstance));
		
		String responseStr = appInstance + " echo: " + msg + "\n";
		return responseStr;
	}
	
	@RequestMapping("upper")
	public String upper(@RequestParam("msg") String msg) {
		if(StringUtils.isEmpty(msg)) {
			return "";
		}
		return msg.toUpperCase();
	}
	
	@RequestMapping(value="/city", method = RequestMethod.GET)
	public ModelAndView getCity(ModelAndView modelAndView) {
		
		modelAndView.addObject("cityList", service.getCities());
		
		CityForm cityForm = new CityForm();
		modelAndView.addObject("cityForm", cityForm);
		return setView(modelAndView, "city");
	}
	
	@RequestMapping(value="/city", method = RequestMethod.POST)
	public ModelAndView addCity(@Valid @ModelAttribute("cityForm")CityForm cityform,ModelAndView modelAndView, BindingResult bindingResult,HttpServletRequest request) {
		
		if(bindingResult.hasErrors()) {
			return setView(modelAndView,"city");
		}
		String response = request.getParameter("g-recaptcha-response");

		if(!captchaService.processResponse(response))
		{
			modelAndView.addObject("reCaptchaError","captcha is invalid");
			return setView(modelAndView,"city");
		}
		String str=""; 
        boolean city = service.addCity(cityform.getCity());
		if(city)
			str= "Added " + city;
		else
			str= "couldn't add "+city;
		
		modelAndView.addObject("test", str);
		modelAndView.addObject("cityList", service.getCities());
		//return setView(modelAndView, "city");
		return new ModelAndView(redirectToUrl("/home/city"));
	}
	
	@RequestMapping("blocked")
	public String blocked() {
		return "In blocked area";
	}
	protected ModelAndView setView(ModelAndView modelAndView, String viewName) {
		modelAndView.addObject(DEFAULT_VIEW_ATTRIBUTE_NAME, viewName); //specific name of view
		modelAndView.setViewName("default"); //name of default page layout
		return modelAndView;
	}
	protected View redirectToUrl(String redirectUrl) {
		RedirectView redirectView = new RedirectView(redirectUrl, true, true, false);
		return redirectView;
	}

	public ICaptchaService getCaptchaService() {
		return captchaService;
	}

	public void setCaptchaService(ICaptchaService captchaService) {
		this.captchaService = captchaService;
	}
}

