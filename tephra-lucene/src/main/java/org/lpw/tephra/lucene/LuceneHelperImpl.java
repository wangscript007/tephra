package org.lpw.tephra.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.lucene.helper")
public class LuceneHelperImpl implements LuceneHelper {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${tephra.lucene.root:/lucene}")
    private String root;
    @Value("${tephra.lucene.analyzer:}")
    private String analyzer;
    private Map<String, Directory> map;

    @Override
    public void clear(String key) {
        io.delete(Paths.get(context.getAbsoluteRoot(), root, key, "source").toFile());
        try (IndexWriter indexWriter = new IndexWriter(get(key), new IndexWriterConfig(newAnalyzer()))) {
            indexWriter.deleteAll();
            indexWriter.flush();
        } catch (Throwable throwable) {
            logger.warn(throwable, "删除Lucene索引[{}]时发生异常！", key);
        }
    }

    @Override
    public void source(String key, String id, String data) {
        Path path = Paths.get(context.getAbsoluteRoot(), root, key, "source", id);
        io.mkdirs(path.toFile().getParentFile());
        io.write(path.toString(), data.getBytes());
    }

    @Override
    public void index(String key) {
        File[] files = Paths.get(context.getAbsoluteRoot(), root, key, "source").toFile().listFiles();
        if (files == null || files.length == 0)
            return;

        Directory directory = get(key);
        if (directory == null)
            return;

        for (File file : files)
            index(directory, file.getName(), io.readAsString(file.getAbsolutePath()));
    }

    @Override
    public void index(String key, String id, String data) {
        Directory directory = get(key);
        if (directory == null)
            return;

        index(directory, id, data);
    }

    private void index(Directory directory, String id, String data) {
        try (IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(newAnalyzer()))) {
            Document document = new Document();
            document.add(new StoredField("id", id));
            document.add(new TextField("data", data, Field.Store.YES));
            indexWriter.addDocument(document);
            indexWriter.flush();
        } catch (Throwable throwable) {
            logger.warn(throwable, "创建Lucene索引时发生异常！");
        }
    }

    @Override
    public List<String> query(String key, String[] words, boolean and, int size) {
        if (validator.isEmpty(words))
            return null;

        StringBuilder query = new StringBuilder();
        for (String word : words)
            if (!validator.isEmpty(word))
                query.append(' ').append(word);
        if (query.length() == 0)
            return null;

        return query(key, query.substring(1), and, size);
    }

    @Override
    public List<String> query(String key, List<String> words, boolean and, int size) {
        if (validator.isEmpty(words))
            return null;

        StringBuilder query = new StringBuilder();
        words.forEach(word -> {
            if (!validator.isEmpty(word))
                query.append(' ').append(word);
        });
        if (query.length() == 0)
            return null;

        return query(key, query.substring(1), and, size);
    }

    @Override
    public List<String> query(String key, String string, boolean and, int size) {
        if (validator.isEmpty(string))
            return null;

        List<String> list = new ArrayList<>();
        if (size <= 0)
            return list;

        try (IndexReader indexReader = DirectoryReader.open(get(key))) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            QueryParser queryParser = new QueryParser("data", newAnalyzer());
            if (and)
                queryParser.setDefaultOperator(QueryParser.Operator.AND);
            TopDocs topDocs = indexSearcher.search(queryParser.parse(string), size);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs)
                list.add(indexSearcher.doc(scoreDoc.doc).get("id"));
        } catch (Throwable throwable) {
            logger.warn(throwable, "检索Lucene数据[{}:{}]时发生异常！", key, string);
        }

        return list;
    }

    private synchronized Directory get(String key) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
            BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        }

        return map.computeIfAbsent(key, k -> {
            Path path = Paths.get(context.getAbsoluteRoot(), root, k, "index");
            io.mkdirs(path.toFile());
            if (logger.isInfoEnable())
                logger.info("设置Lucene索引根目录[{}:{}]。", k, path);
            try {
                return FSDirectory.open(path);
            } catch (IOException e) {
                logger.warn(e, "打开Lucene索引目录[{}:{}]时发生异常！", k, path);

                return null;
            }
        });
    }

    private Analyzer newAnalyzer() {
        switch (analyzer) {
            case "char":
                return new CharAnalyzer();
            case "simple":
                return new SimpleAnalyzer();
            case "standard":
                return new StandardAnalyzer();
            default:
                return new CJKAnalyzer();
        }
    }
}
