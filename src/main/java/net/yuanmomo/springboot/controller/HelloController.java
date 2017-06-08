package net.yuanmomo.springboot.controller;

/**
 * Created by Hongbin.Yuan on 2017-04-03 20:01.
 */

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
