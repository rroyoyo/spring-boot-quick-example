package com.royoyo.bootiful;

//import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
// reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@SpringBootApplication
public class BootifulApplication {

    @Bean
    MapReactiveUserDetailsService users() {
        return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder().username("user").password("pass").roles("USER").build());
    }

    @Bean
    HealthIndicator healthIndicator (){
        return () -> Health.status("I <3 OYO?").build();

    }

    @Bean
    RouterFunction<ServerResponse> routes(CustomerRepository cr) {

        return RouterFunctions.route(GET("/customers"), serverRequest ->  ok().body(cr.findAll(), Customer.class));
        }
	public static void main(String[] args) {
		SpringApplication.run(BootifulApplication.class, args);
	}
}

@Component
class DataWriter implements ApplicationRunner{

    private final CustomerRepository customerRepository;

    DataWriter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String id = null;
        Flux.just("Mia", "Madhura", "Dave", "Onsi")
                .flatMap( name -> customerRepository.save(new Customer(id, name)))
                .subscribe(System.out::println);
    }
}

interface CustomerRepository extends ReactiveMongoRepository <Customer, String>{}

@Document
@NoArgsConstructor
@Data
class Customer {
    private String id, name;

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }
}