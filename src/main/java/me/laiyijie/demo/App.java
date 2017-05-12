package me.laiyijie.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import me.laiyijie.demo.service.HelloInterface;

public class App {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("root-context.xml");

        HelloInterface userService = context.getBean(HelloInterface.class);

        userService.sayHello();

        context.close();
    }
}
