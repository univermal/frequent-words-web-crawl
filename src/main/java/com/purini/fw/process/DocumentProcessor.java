package com.purini.fw.process;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import com.purini.fw.utils.TextToWordUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Processes the document through completion service.
 * Iterated over all the elements, extracts the text and passes them
 * to Word count store service.
 */
public class DocumentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessor.class);
    private final WordCountStoreService wordCountStoreManager;
    private final AtomicInteger docCount = new AtomicInteger();
    private final CompletionService<Void> completionService;

    public DocumentProcessor(CompletionService<Void> completionService, WordCountStoreService wordCountStoreManager) {
        this.wordCountStoreManager = wordCountStoreManager;
        this.completionService = completionService;
    }

    public void submitAsync(final Document document) {
        completionService.submit(() -> process(document));
    }

    private Void process(Document document) {
        extract(document);
        logger.debug("docs processed - {}", docCount.incrementAndGet());
        return null;
    }

    private void extract(Document doc) {
        for (Element element : doc.getAllElements()) {

            List<Word> words = TextToWordUtil.toWords(element.ownText());
            wordCountStoreManager.addWords(words);

            List<WordPair> wordPairs = TextToWordUtil.toWordPairs(words);
            wordCountStoreManager.addWordPairs(wordPairs);
        }
    }

}
