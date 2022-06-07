package org.example;

import org.example.config.ParseRuleConfig;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {



    @Test
    void startService() {
        // given
        User user = new User();
        String keyword = "Redis";
        String scanFolder = System.getProperty("user.dir");
        ParseRuleConfig.ParseRuleConfigBuilder builder = new ParseRuleConfig.ParseRuleConfigBuilder();
        ParseRuleConfig rule = builder.setScanFolder(scanFolder)
                .addKeyword(keyword)
                .build();
        // when
        user.start(rule);
        // then
    }

    @Test
    void integerTest() {
        // given
        String keyword = "FileScan";
        String scanFolder = System.getProperty("user.dir");
        ParseRuleConfig parseRule = ParseRuleConfig.builder()
                .setScanFolder(scanFolder)
                .addKeyword(keyword)
                .build();
        User user = new User();

        // when
        user.start(parseRule);
//        user.showAll();
//        user.onlyShowFileName();
        user.filterSpecificPackage("org.example.service");
    }
}