package org.springframework.demo.funwithfunctional;

import java.time.LocalDate;

import reactor.core.publisher.Flux;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.demo.funwithfunctional.owner.Owner;
import org.springframework.demo.funwithfunctional.owner.OwnerRepository;
import org.springframework.demo.funwithfunctional.pet.Pet;
import org.springframework.demo.funwithfunctional.pet.PetRepository;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Configuration
@EnableAutoConfiguration
@Import(Demo3Configuration.class)
public class FunWithFunctionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunWithFunctionalApplication.class, args);
	}

	@Component
	private static class DataInserter implements CommandLineRunner {

		private final PetRepository petRepository;

		private final OwnerRepository ownerRepository;

		public DataInserter(PetRepository petRepository,
				OwnerRepository ownerRepository) {
			this.petRepository = petRepository;
			this.ownerRepository = ownerRepository;
		}

		@Override
		public void run(String... args) throws Exception {
			Pet leo = new Pet("Leo", LocalDate.of(2010, 9, 7));
			Pet basil = new Pet("Basil", LocalDate.of(2012, 8, 6));
			this.petRepository.insert(asList(leo, basil)).subscribe();

			Owner georgeFranklin = new Owner("George Franklin", "110 W. Liberty St.", "Madison'");
			Owner bettyDavis = new Owner("Betty Davis", "638 Cardinal Ave.", "Sun Prairie");
			this.ownerRepository.insert(asList(georgeFranklin, bettyDavis)).subscribe();
		}
	}
}
