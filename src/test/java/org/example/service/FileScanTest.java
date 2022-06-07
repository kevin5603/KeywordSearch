package org.example.service;

import org.example.config.ParseRuleConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class FileScanTest {

    private FileScan fileScan;

    void init() {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        String keyword = "Redis";
        String scanFolder = System.getProperty("user.dir");
        ParseRuleConfig.ParseRuleConfigBuilder builder = new ParseRuleConfig.ParseRuleConfigBuilder();
        ParseRuleConfig rule = builder.setScanFolder(scanFolder)
                .addKeyword(keyword)
                .build();
        fileScan = new FileScan(rule, queue);
    }

    @Test
    void parseTest() {
        // given
        init();
        int expectFileCount = 6;
        // when
        fileScan.parse();
        // then
        Assertions.assertEquals(expectFileCount, fileScan.getFileCount());
    }

    @Test
    void getSuffixWithNoSuffixFileName() {
        // given
        init();
        // when
        String fileName = "noSuffixFileName";
        String expectSuffix = fileName;
        String actualSuffixName = fileScan.getSuffix(fileName);
        // then
        assertEquals(expectSuffix, actualSuffixName);
    }

    @Test
    void getSuffixWithSuffixFileName() {
        // given
        init();
        // when
        String fileName = "FileParse.java";
        String expectSuffix = "java";
        String actualSuffixName = fileScan.getSuffix(fileName);
        // then
        assertEquals(expectSuffix, actualSuffixName);
    }
}