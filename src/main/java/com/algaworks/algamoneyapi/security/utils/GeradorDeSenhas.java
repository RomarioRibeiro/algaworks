package com.algaworks.algamoneyapi.security.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorDeSenhas {
	
	public static void main(String[] args) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("m0b1l30"));
		
	}
}
