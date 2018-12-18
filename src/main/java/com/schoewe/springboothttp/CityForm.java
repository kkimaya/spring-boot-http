package com.schoewe.springboothttp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CityForm {
	@NotBlank
	@Size(max=256)
	private String city;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
