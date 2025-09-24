package com.disseration.coupon_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CouponEngineApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		SpringApplication.run(CouponEngineApplication.class, args);
		System.out.println("Default TimeZone: " + TimeZone.getDefault().getID());
	}

}
