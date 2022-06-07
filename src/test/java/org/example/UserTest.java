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
        String scanFolder = "/Volumes/work/project/line-bot-demo";
        ParseRuleConfig.ParseRuleConfigBuilder builder = new ParseRuleConfig.ParseRuleConfigBuilder();
        ParseRuleConfig rule = builder.setScanFolder(scanFolder)
                .addKeyword(keyword)
                .build();
        // when
        user.start(rule);
        // then
    }
}