package org.springframework.demo.funwithfunctional.owner;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;

@Data
public class Owner {

	@Id
	private String id;

	private String name;

	private String address;

	private String city;

	public Owner(String name, String address, String city) {
		this.name = name;
		this.address = address;
		this.city = city;
	}
}
