package org.example.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.config.ParseRuleConfig;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.BlockingQueue;

/**
 * traversal指定資料夾
 * 將檔案路徑放入queue中
 * @author liyanting
 */
@Slf4j
public class FileScan extends SimpleFileVisitor<Path>{

    private ParseRuleConfig rule;
    private BlockingQueue<String> queue;
    @Getter
    private int fileCount = 0;

    @Getter
    private boolean isDone = false;

    public FileScan(ParseRuleConfig rule, BlockingQueue<String> queue) {
        this.rule = rule;
        this.queue = queue;
    }

    public void parse() {
        try {
            Path path = Paths.get(rule.getScanFolder());
            log.info("掃瞄路徑: {} 掃檔開始...", path);
            Files.walkFileTree(path, this);
            log.info("掃檔結束 共{}筆檔案", fileCount);
            isDone = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        String fileName = dir.getFileName().toString();
        if (rule.getIgnoreFolder().contains(fileName)) {
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String fileName = file.getFileName().toString();
        if (isMatch(fileName)) {
            try {
                queue.put(file.toFile().getAbsolutePath());
                fileCount++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 檢驗檔名是否有包含在過濾名單中
     * @param fileName
     * @return 若在過濾名單中回傳false 反之true
     */
    private boolean isMatch(String fileName) {
        return !rule.getIgnoreSuffix().contains(getSuffix(fileName));
    }

    /**
     * 取得檔名後綴
     * 若無後綴則回傳原檔名
     * @param fileName
     * @return
     */
    public String getSuffix(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        return lastIndexOf != -1 ? fileName.substring(lastIndexOf + 1) : fileName;
    }
}
