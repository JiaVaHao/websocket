package com.jwh.yunchat;

import com.jwh.yunchat.util.DMD5Encrypt;

import org.junit.Test;

import static org.junit.Assert.*;

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
    public void testMD5(){
        System.out.println(DMD5Encrypt.MD5Encrypt("33333"));
    }
}