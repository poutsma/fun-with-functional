package org.springframework.demo.funwithfunctional.pet;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class Pet {

	@Id
	private String id;

	private String name;

	private LocalDate birthDay;

	public Pet(String name, LocalDate birthDay) {
		this.name = name;
		this.birthDay = birthDay;
	}
}
