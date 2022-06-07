package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ParseRuleConfig;
import org.example.service.FileParser;
import org.example.service.FileScan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * @author liyanting
 */
@Slf4j
public class User {

    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private ParseRuleConfig config;
    private FileScan fileScan;


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
        List<Future<List<String>>> futureList = new ArrayList<>();
        while (!fileScan.isDone() || !queue.isEmpty()) {
            Future<List<String>> future;
            try {
                future = service.submit(new FileParser(queue.take(), config));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            futureList.add(future);
        }

        futureList.forEach(future -> {
            try {
                future.get().forEach(log::info);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        service.shutdown();
    }



    public static void main(String[] args) {
        String keyword = "Redis";
        String scanFolder = "/Volumes/work/project/line-bot-demo";
        ParseRuleConfig.ParseRuleConfigBuilder builder = new ParseRuleConfig.ParseRuleConfigBuilder();
        ParseRuleConfig rule = builder.setScanFolder(scanFolder)
                .addKeyword(keyword)
                .addKeyword("Kevin")
                .build();

        User user = new User();
        user.start(rule);
    }
}
