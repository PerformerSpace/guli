package com.guli.ucenter;

import org.junit.Test;
import sun.security.krb5.internal.Ticket;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test1 {
    @Test
    public void test1(){
        ArrayList<Object> list = new ArrayList<>();
        int a  = 1;
        int b  = 2;
        int c  = 3;


        list.add(a);
        list.add(b);
        list.add(c);
        list.forEach(System.out::println);

    }



}

