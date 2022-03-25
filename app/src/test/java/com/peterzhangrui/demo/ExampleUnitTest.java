package com.peterzhangrui.demo;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testListAdd() {
        ArrayList<String> list = new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
        }};

        list.add(0, "7");
        list.add(0, "8");

        System.out.println(list.toString());
        assertTrue(list.toString(), true);
    }
}