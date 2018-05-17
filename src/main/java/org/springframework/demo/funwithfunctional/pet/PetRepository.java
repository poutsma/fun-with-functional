package org.springframework.demo.funwithfunctional.pet;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PetRepository extends ReactiveMongoRepository<Pet, String> {

}
