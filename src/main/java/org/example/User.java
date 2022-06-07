package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ParseRuleConfig;
import org.example.model.Info;
import org.example.service.FileParser;
import org.example.service.FileScan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 *
 * @author liyanting
 */
@Slf4j
public class User {

    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private ParseRuleConfig config;
    private FileScan fileScan;
    Queue<Info> result = new ConcurrentLinkedQueue<>();


    public void start(String scanFolder, String keyword) {
        config = new ParseRuleConfig(scanFolder, keyword);

        fileScan = new FileScan(config, queue);
        fileScan.parse();

        startService();
    }

    public void start(ParseRuleConfig config) {
        System.out.println("start...");
        this.config = config;
        fileScan = new FileScan(config, queue);
        fileScan.parse();

        startService();
        System.out.println("end...");
    }

    public void startService() {
        ExecutorService service = Executors.newCachedThreadPool();
        
        List<Future<List<Info>>> futureList = new ArrayList<>();
        while (!fileScan.isDone() || !queue.isEmpty()) {
            Future<List<Info>> future;
            try {
                future = service.submit(new FileParser(queue.take(), config));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            futureList.add(future);
        }

        futureList.forEach(future -> {
            try {
                result.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        service.shutdown();
    }

    public void onlyShowFileName() {
        List<String> distinctFileName = result.stream()
                .map(i -> i.getFileName())
                .distinct()
                .collect(Collectors.toList());
        log.info("========== only show filename ==========");
        distinctFileName.forEach(log::info);
        log.info("符合條件檔案數量: {}筆", distinctFileName.size());
        log.info("========== end ==========");
    }

    public void showAll() {
        log.info("========== show all message ==========");
        result.forEach(i -> log.info("{}", i));
        log.info("符合條件行數: {}筆", result.size());
        log.info("========== end ==========");
    }

    public void filterSpecificPackage(String packageName) {
        result.stream().forEach(info -> {
            info.setFullPath(info.getFullPath().replaceAll(File.separator, "."));
        });
        result.stream()
                .filter(info -> info.getFullPath().contains(packageName))
                .forEach(info -> log.info("{}", info));
    }

    /**
     * 直接取得結果
     * 可依自己需求做客製化處理
     * @return
     */
    public Queue<Info> getResult() {
        return result;
    }
}
