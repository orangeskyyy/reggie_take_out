package com.ntc.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UploadFile {
    @Test
    void testSuffix() {
        String fileName = "hahaha.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);// .jpg
    }
}
