package org.example.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.ConfigException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 抓取檔案規則
 * @author liyanting
 */
@Getter
public class ParseRuleConfig {
    /** 欲掃描資料夾 */
    private String scanFolder;
    /** 欲略過資料夾 */
    private Set<String> ignoreFolder;
    /** 欲略過副檔名 */
    private Set<String> ignoreSuffix;
    /** 欲查詢關鍵字組 */
    private List<String> keywordList;
    // TODO 尚未實作
    private boolean enableRegex = false;
    /** 是否區分大小寫 */
    private boolean caseSensitive = true;
    // TODO 看看是否能能導入output format  問題點：參數要怎麼導入？

    public ParseRuleConfig(String scanFolder, String keyword) {
        this.scanFolder = scanFolder;
        this.keywordList = new ArrayList<String>() {{add(keyword);}};
        this.ignoreFolder = defaultIgnoreFolder();
        this.ignoreSuffix = defaultIgnoreSuffix();
    }

    public ParseRuleConfig(ParseRuleConfigBuilder builder) {
        if (builder.keywordList == null) {
            throw new ConfigException("缺少欲查詢關鍵字");
        }
        if (StringUtils.isEmpty(builder.scanFolder)) {
            throw new ConfigException("缺少掃描檔案路徑");
        }

        this.scanFolder = builder.scanFolder;
        this.keywordList = builder.keywordList;
        this.ignoreFolder = builder.ignoreFolder == null ? defaultIgnoreFolder() : builder.ignoreFolder;
        this.ignoreSuffix = builder.ignoreSuffix == null ? defaultIgnoreSuffix() : builder.ignoreSuffix;
        this.enableRegex = builder.enableRegex;
        this.caseSensitive = builder.caseSensitive;
    }

    public static ParseRuleConfigBuilder builder() {
        return new ParseRuleConfigBuilder();
    };

    /**
     * 預設不掃描副檔名
     * @return
     */
    private HashSet<String> defaultIgnoreSuffix() {
        return new HashSet<String>() {{
            add("xml");
            add("json");
            add("DS_Store");
            add("yml");
            add("sql");
            add("gitignore");
            add("iml");
            add("md");
            add("http");
            add("crt");
            add("Jenkinsfile");
            add("conf");
            add("cmd");
            add("jar");
            add("mvnw");
            add("meta");
            add("txt");
            add("cookies");
            add("png");
            add("html");
            add("properties");
            add("log");
        }};
    }

    /**
     * 預設不掃描資料夾
     * @return
     */
    private HashSet<String> defaultIgnoreFolder() {
        return new HashSet<String>() {{
            add(".git");
            add("target");
            add("test");
            add(".mvn");
            add(".idea");
        }};
    }

    public static class ParseRuleConfigBuilder {
        private String scanFolder;
        private Set<String> ignoreFolder;
        private Set<String> ignoreSuffix;
        private List<String> keywordList;
        private boolean enableRegex = false;
        private boolean caseSensitive = true;

        public ParseRuleConfigBuilder addIgnoreFolder(Set<String> ignoreFolder) {
            this.ignoreFolder = ignoreFolder;
            return this;
        }

        public ParseRuleConfigBuilder addIgnoreSuffix(Set<String> ignoreSuffix) {
            this.ignoreSuffix = ignoreSuffix;
            return this;
        }

        public ParseRuleConfigBuilder addKeywordList(List<String> keywordList) {
            if (this.keywordList != null) {
                this.keywordList.addAll(keywordList);
            } else {
                this.keywordList = keywordList;
            }
            return this;
        }

        public ParseRuleConfigBuilder addKeyword(String keyword) {
            if (keywordList == null) {
                keywordList = new ArrayList<>();
            }
            keywordList.add(keyword);
            return this;
        }

        public ParseRuleConfigBuilder setScanFolder(String scanFolder) {
            this.scanFolder = scanFolder;
            return this;
        }

        public ParseRuleConfigBuilder isEnableRegex(boolean enableRegex) {
            this.enableRegex = enableRegex;
            return this;
        }

        public ParseRuleConfigBuilder isCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public ParseRuleConfig build() {
            return new ParseRuleConfig(this);
        }
    }


}
