package org.example.service;

import org.example.config.ParseRuleConfig;
import org.example.model.Info;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 負責解析並回傳結果
 * @author liyanting
 */
public class FileParser implements Callable<List<Info>> {

    private String filePath;
    private ParseRuleConfig rule;

    public FileParser(String filePath, ParseRuleConfig rule) {
        this.filePath = filePath;
        this.rule = rule;
    }


    @Override
    public List<Info> call() throws Exception {
        String line;
        List<Info> res = new ArrayList<>();
        int count = 1;
        Path path = Paths.get(filePath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
            while ((line = br.readLine()) != null) {
                for(String keyword : rule.getKeywordList()) {
                    if (isMatch(line, keyword)) {
                        Info info = new Info(path.toString(), path.getFileName().toString(), count, keyword, line);
                        res.add(info);
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
