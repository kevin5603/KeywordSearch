package org.example.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParseRuleConfigTest {


    @Test
    public void buildTest() {
        ParseRuleConfig.ParseRuleConfigBuilder builder = new ParseRuleConfig.ParseRuleConfigBuilder();
        String scanFolder = System.getProperty("user.dir");
        HashSet<String> ignoreFolder = new HashSet<String>() {{
            add(".git");
            add("target");
        }};

        HashSet<String> ignoreSuffix = new HashSet<String>() {{
            add(".class");
            add(".xml");
        }};

        List<String> keywordList = new ArrayList<String>(){{add("hello"); add("yoyo");}};

        ParseRuleConfig config = builder
                .isCaseSensitive(true)
                .isEnableRegex(true)
                .addKeyword("hello")
                .addKeyword("yoyo")
                .addIgnoreFolder(ignoreFolder)
                .addIgnoreSuffix(ignoreSuffix)
                .setScanFolder(scanFolder)
                .build();

        Assertions.assertEquals(scanFolder, config.getScanFolder());
        assertTrue(config.isCaseSensitive());
        assertTrue(config.isEnableRegex());
        assertThat(config.getIgnoreSuffix()).usingRecursiveComparison().isEqualTo(ignoreSuffix);
        assertThat(config.getIgnoreFolder()).usingRecursiveComparison().isEqualTo(ignoreFolder);
        assertThat(config.getKeywordList()).usingRecursiveComparison().isEqualTo(keywordList);

    }

}