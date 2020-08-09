package com.hackathon.omcose;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LuceneManager {

    private static  Analyzer analyzer =new StandardAnalyzer(Version.LUCENE_47);
    private static  Directory index;
    private static IndexSearcher searcher ;

    static {
        try {
            index = FSDirectory.open(Environment.getExternalStorageDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static  IndexWriter writer=null;


    public static void  addDoc(String title , String content, String filePath) {
        Log.d(LuceneManager.class.getName(), Environment.getDataDirectory().getAbsolutePath());
        Log.d("LuceneManager","Adding doc");
        try {
            IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_47, analyzer);
            writer = new IndexWriter(index, conf);
            addDoc(writer, title, content, filePath);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    static void  addDoc(IndexWriter writer,  String title , String content, String filePath) {
        try {
            Document doc = new Document();
            FieldType contentFieldType = new FieldType();
            contentFieldType.setTokenized(true);
            contentFieldType.setStored(true);
            Log.v("Content", "the content added is " +content);
            doc.add(new Field("Title", title, Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("Content", content,Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("FilePath", filePath,Field.Store.YES, Field.Index.ANALYZED));

            writer.addDocument(doc);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static List<HashMap<String,String>> query(String query){
//        List<HashMap<String,String>> list = new LinkedList<>();
        Map<String, HashMap<String,String>> map = new HashMap<>();
        try {
            IndexReader reader = IndexReader.open(index);;
            searcher = new IndexSearcher(reader);

            Query q = new QueryParser(Version.LUCENE_47,"Content", analyzer).parse(query);
            int hitsPerPage = 10;
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                HashMap<String,String> document= new HashMap<String,String>();
                document.put("FilePath" ,d.get("FilePath") );
                document.put("Title",d.get("Title"));
//                list.add(document);
                map.put(d.get("FilePath"),document);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>(map.values());
    }





}
