package com.dam.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("juan@example.com → " + encoder.encode("1234"));
        System.out.println("admin@example.com → " + encoder.encode("adminpass"));
        System.out.println("ana@example.com → " + encoder.encode("ana1234"));
        System.out.println("antonioperezbermejo@hotmail.com → " + encoder.encode("7777"));
        System.out.println("silviuusm@gmail.com → " + encoder.encode("100799tequieroS"));
        System.out.println("javilukt@gmail.com → " + encoder.encode("1111"));
        System.out.println("Javi@gmail.com → " + encoder.encode("1111"));
    }
}
