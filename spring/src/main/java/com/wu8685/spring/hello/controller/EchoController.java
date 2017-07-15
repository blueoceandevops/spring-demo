package com.wu8685.spring.hello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/hello")
public class EchoController {

	@RequestMapping(path = "/echo", method = RequestMethod.GET)
	public String echo(@RequestParam(defaultValue = "hello") String echo) {
		return "echo: " + echo; 
	}
	
}
