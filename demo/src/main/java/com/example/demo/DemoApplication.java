package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.airegistry.model.Agent;
import com.example.demo.airegistry.repository.AgentRepository;

@SpringBootApplication
public class DemoApplication {

	// @Autowired
	// AgentRepository agentRepository;
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// Just for testing purpose
	// @Bean
	// public CommandLineRunner insertAgent(ApplicationContext ctx){
	// 	return args->{
	// 		Agent agent = new Agent();
	// 		agent.setName("Testing Agent"+System.currentTimeMillis());
	// 		agentRepository.save(agent);
	// 	};
	// }
	

}

@RestController
class HelloController{
	@GetMapping("/hello")
	public String sayHello(){
		return "Hello World";
	}
}

