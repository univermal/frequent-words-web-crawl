package com.purini.fw.process;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocumentProcessorTest {

    @Test
    public void testDocumentProcessor() throws IOException, InterruptedException {

        final WordCountStoreService wordCountStoreService = new WordCountStoreService();
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        final ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<>(executor);

        Document levelZeroDoc = Jsoup.parse(getFileAsString("zero.html"));
        levelZeroDoc.setBaseUri("http://zero.com");

        new DocumentProcessor(completionService, wordCountStoreService).submitAsync(levelZeroDoc);
        completionService.take();
        executor.shutdown();
        List<Pair<Word, Integer>> words = wordCountStoreService.getTopNWords(10);
        List<Pair<WordPair, Integer>> wordPairs = wordCountStoreService.getTopNWordPairs(10);
        Assertions.assertFalse(words.isEmpty());
        Assertions.assertFalse(wordPairs.isEmpty());

    }

    private static String getFileAsString(String filePath) throws IOException {
        return String.join("", Files.readAllLines(Paths.get(UrlTraverserTest.class.getClassLoader().getResource(filePath).getFile())));
    }

}
