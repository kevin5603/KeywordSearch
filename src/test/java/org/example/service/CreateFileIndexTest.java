package org.example.service;

import org.apache.lucene.search.ScoreDoc;
import org.example.User;
import org.example.config.ParseRuleConfig;
import org.example.model.Info;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateFileIndexTest {

    void t() {
        String keyword = "FileScan";
        String indexPath = "/Volumes/work/tmp";
        String scanFolder = "/Volumes/work/project/line-bot-demo";
        ParseRuleConfig parseRule = ParseRuleConfig.builder()
                .setScanFolder(scanFolder)
                .addKeyword(keyword)
                .build();
        CreateFileIndex createFileIndex = new CreateFileIndex(parseRule, indexPath);
        createFileIndex.deleteIndex();
    }

    void start() {
        // given
        String keyword = "FileScan";
        String indexPath = "/Volumes/work/tmp";
        String scanFolder = "/Volumes/work/project/line-bot-demo";
        ParseRuleConfig parseRule = ParseRuleConfig.builder()
                .setScanFolder(scanFolder)
                .addKeyword(keyword)
                .build();
        CreateFileIndex createFileIndex = new CreateFileIndex(parseRule, indexPath);
        // 清除之前索引 避免同樣索引重複建立
        createFileIndex.deleteIndex();
        // when
        createFileIndex.start();
        // then
    }

    @Test
    void test() {
        start();
        String keyword = "param";
        String indexPath = "/Volumes/work/tmp";
        String scanFolder = "/Volumes/work/project/line-bot-demo";
        ParseRuleConfig parseRule = ParseRuleConfig.builder()
                .setScanFolder(scanFolder)
                .addKeyword(keyword)
                .isCaseSensitive(false)
                .build();

        User user = new User();
        user.start(parseRule);
        user.onlyShowFileName();

        List<String> collect1 = user.getResult().stream().map(Info::getFileName).distinct().sorted().collect(Collectors.toList());

        CreateFileIndex createFileIndex = new CreateFileIndex(parseRule, indexPath);
        List<String> collect2 = createFileIndex.search("contents", "*" + keyword + "*").stream().sorted().collect(Collectors.toList());


        assertThat(collect1).usingRecursiveComparison().isEqualTo(collect2);
        Assertions.assertEquals(collect1.size(), collect2.size());


    }
}