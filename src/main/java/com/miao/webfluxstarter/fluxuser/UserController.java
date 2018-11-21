package com.miao.webfluxstarter.fluxuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;

@RestController
@RequestMapping(path = "/api/rest/fluxuser")
public class UserController
{
    private final IUserRepository userRepo;

    public UserController(IUserRepository userRepo)
    {
        this.userRepo = userRepo;
    }

    @PostMapping("/initData")
    public Mono<Void> add5000(){
        List<User> users=new ArrayList<>();
        for (int i = 0; i < 1000; i++)
        {
            User u =new User();
            u.setId(i);
            u.setName("miaoqiao");
            u.setAge(21);
            users.add(u);
        }
        return userRepo.saveAll(users)
                .then();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Mono<ResponseEntity<?>> addOne(@RequestBody User u){
        return Mono.justOrEmpty(u.getId())
                .flatMap(id -> userRepo.existsById(String.valueOf(id)))
                .defaultIfEmpty(false)
                .flatMap(exist -> {
                   if(exist)
                       throw new IllegalArgumentException("ID existed!");
                   else
                       return userRepo.save(u).map(ResponseEntity::ok);
                });
    }

    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<?>> deleteOne(@PathVariable String id){
        final Mono<ResponseEntity<?>> noContent = Mono.just(noContent().build());

        return userRepo.existsById(id)
                .filter(e -> e)
                .flatMap(exist -> userRepo.deleteById(id).then(noContent))
                .switchIfEmpty(noContent);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Mono<ResponseEntity<?>> updateOne(@RequestBody User u){
        return userRepo.existsById(String.valueOf(u.getId()))
                .flatMap(exist -> {
                   if(!exist)
                       throw new IllegalArgumentException();
                   return userRepo.save(u).then(Mono.just(noContent().build()));
                });
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<User>> findOne(@PathVariable String id){
        return userRepo.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(noContent().build()));
    }

    @GetMapping("/list")
    public Mono<ResponseEntity<List<User>>> findAll(){
        return userRepo.findAll()
                .collectList()
                .filter(users -> users.size()>0)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
