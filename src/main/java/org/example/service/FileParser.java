package org.example.service;

import org.example.config.ParseRuleConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 負責解析並回傳結果
 * @author liyanting
 */
public class FileParser implements Callable<List<String>> {

    private String filePath;
    private String outputFormat = "行數:%d, 關鍵字: %s, 內文: %s";
    private ParseRuleConfig rule;

    public FileParser(String filePath, ParseRuleConfig rule) {
        this.filePath = filePath;
        this.rule = rule;
    }


    @Override
    public List<String> call() throws Exception {
        String line;
        List<String> res = new ArrayList<>();
        int count = 1;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))))) {
            while ((line = br.readLine()) != null) {
                for(String keyword : rule.getKeywordList()) {
                    if (isMatch(line, keyword)) {
                        String output = String.format(outputFormat, count, keyword, line);
                        res.add(output);
                    }
                }
                count++;
            }
        }
        return res;
    }

    private boolean isMatch(String line, String keyword) {
        if (rule.isCaseSensitive()) {
            return line.contains(keyword);
        } else {
            return line.toUpperCase().contains(keyword.toUpperCase());
        }

    }




}
