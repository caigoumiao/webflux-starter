package com.miao.webfluxstarter.fluxuser;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserRepository extends ReactiveCrudRepository<User, String>
{

}
