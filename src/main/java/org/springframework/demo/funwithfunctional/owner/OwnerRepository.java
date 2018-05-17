package org.springframework.demo.funwithfunctional.owner;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.demo.funwithfunctional.pet.Pet;

public interface OwnerRepository extends ReactiveMongoRepository<Owner, String> {

}
