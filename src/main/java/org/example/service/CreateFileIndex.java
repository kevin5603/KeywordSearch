package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.config.ParseRuleConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * !!一次性執行程式
 * 會將指定資料夾下的檔案建立索引
 * 方便後續快速查詢
 * 若重複執行結果也會重複出現
 * 若有異動須將原先索引資料夾刪除再重新跑一遍
 */
@Slf4j
public class CreateFileIndex {

    private final String indexPath;
    private Analyzer analyzer;
    private Directory indexDirectory;
    private BlockingQueue<String> blockingQueue;
    private ParseRuleConfig rule;


    public CreateFileIndex(ParseRuleConfig rule, String indexPath) {
        try {
            this.indexPath = indexPath;
            this.rule = rule;
            this.analyzer = new StandardAnalyzer();
            this.indexDirectory = FSDirectory
                    .open(Paths.get(indexPath));
            blockingQueue = new LinkedBlockingQueue<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIndex() {
        try {
            IndexWriterConfig indexWriterConfig
                    = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(
                    indexDirectory, indexWriterConfig);
            indexWriter.deleteAll();
            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        log.info("start...");
        FileScan scan = new FileScan(rule, blockingQueue);
        scan.parse();
        int i = 0;
        while (!scan.isDone() || !blockingQueue.isEmpty()) {
            try {
                String filePath = blockingQueue.take();
                System.out.println(++i);
                addFileToIndex(filePath);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("end...");
    }

    public void addFileToIndex(String filepath) throws IOException {

        Path path = Paths.get(filepath);
        File file = path.toFile();
        IndexWriterConfig indexWriterConfig
                = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(
                indexDirectory, indexWriterConfig);
        Document document = new Document();

        FileReader fileReader = new FileReader(file);

        document.add(
                new TextField("contents", fileReader));
        document.add(
                new StringField("path", file.getPath(), Field.Store.YES));

        document.add(
                new StringField("name", file.getName(), Field.Store.YES));

        indexWriter.addDocument(document);
        indexWriter.close();
    }

    public List<String> search(String field, String queryString) {
        // 参数：输入的lucene的查询语句
        try {
            QueryParser queryParser = new QueryParser(field, analyzer);
            queryParser.setAllowLeadingWildcard(true);
            Query query = queryParser.parse(queryString);


            File file=new File(indexPath);
            Directory directory=FSDirectory.open(file.toPath());

            IndexReader reader= DirectoryReader.open(directory);
            IndexSearcher searcher=new IndexSearcher(reader);
            TopDocs search = searcher.search(query, 10000);
            ScoreDoc[] scoreDocs = search.scoreDocs;
            List<String> res = new ArrayList<>();
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 获取文档的ID
                int docId = scoreDoc.doc;

                // 通过ID获取文档
                Document doc = searcher.doc(docId);
                System.out.println(doc.get("name"));
                res.add(doc.get("name"));
            }
            System.out.println("共" + scoreDocs.length + "筆");
            return res;

        } catch (IOException | org.apache.lucene.queryparser.classic.ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
