package org.lpw.tephra.lucene;

import com.hankcs.lucene.HanLPAnalyzer;
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
    private Map<String, Directory> map = new ConcurrentHashMap<>();

    @Override
    public void clear(String key) {
        io.delete(Paths.get(context.getAbsoluteRoot(), root, key, "source").toFile());
        try (IndexWriter indexWriter = new IndexWriter(get(key), new IndexWriterConfig(new StandardAnalyzer()))) {
            indexWriter.deleteAll();
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
    public int index(String key) {
        File[] files = Paths.get(context.getAbsoluteRoot(), root, key, "source").toFile().listFiles();
        if (files == null || files.length == 0)
            return 0;

        try (IndexWriter indexWriter = new IndexWriter(get(key), new IndexWriterConfig(new HanLPAnalyzer()))) {
            for (File file : files) {
                Document document = new Document();
                document.add(new StoredField("id", file.getName()));
                document.add(new TextField("data", io.readAsString(file.getAbsolutePath()), Field.Store.YES));
                indexWriter.addDocument(document);
            }

            return files.length;
        } catch (Throwable throwable) {
            logger.warn(throwable, "创建Lucene索引时发生异常！");

            return -1;
        }
    }

    @Override
    public List<String> query(String key, List<String> words, boolean and, int size) {
        if (validator.isEmpty(words) || size <= 0)
            return new ArrayList<>();

        StringBuilder query = new StringBuilder();
        words.forEach(word -> query.append(" \"").append(word).append('"'));

        return query(key, query.substring(1), and, size);
    }

    @Override
    public List<String> query(String key, String string, boolean and, int size) {
        List<String> list = new ArrayList<>();
        if (validator.isEmpty(string) || size <= 0)
            return list;

        try (IndexReader indexReader = DirectoryReader.open(get(key))) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            QueryParser queryParser = new QueryParser("data", new HanLPAnalyzer());
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

    private Directory get(String key) throws IOException {
        if (map.containsKey(key))
            return map.get(key);

        Path path = Paths.get(context.getAbsoluteRoot(), root, key, "index");
        io.mkdirs(path.toFile());
        if (logger.isInfoEnable())
            logger.info("设置Lucene索引根目录[{}:{}]。", key, path);
        Directory directory = FSDirectory.open(path);
        map.put(key, directory);

        return directory;
    }
}
