package com.purini.fw;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import com.purini.fw.process.*;
import com.purini.fw.utils.UrlUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.*;

/**
 * Primary class for running the application
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private final int levels;
    private final String url;
    private final boolean internalLinksOnly;
    private final PropertyStore propertyStore;

    /**
     * @param levels            - depth to which child links should be traversed
     * @param url               - main Url
     * @param internalLinksOnly - whether to traverse links to same website only
     */
    public Application(int levels, String url, boolean internalLinksOnly) {
        this.levels = levels;
        this.url = url;
        this.internalLinksOnly = internalLinksOnly;
        this.propertyStore = PropertyStore.getInstance();
    }

    /**
     * Run the app
     * @param topNCount - count of top words and top word pairs to be printed
     * @return Pair of List of word count pairs and List of wordPair count pairs
     * or null in case of validation failures
     */
    @Nullable
    public Pair<List<Pair<Word, Integer>>, List<Pair<WordPair, Integer>>> getTopNWordsAndWordPairs(int topNCount) {

        if (!UrlUtil.isValid(url)) {
            logger.error("Invalid Url - {}, should be in http://... or https://... format.", url);
            return null;
        }

        //The stores (one for words, and one for word pairs) will contain the final results
        final WordCountStoreService wordCountStores = new WordCountStoreService();

        //Executor services to process dom documents in parallel
        final ExecutorService documentProcessorExecutor = newExecutorService();
        final ExecutorCompletionService<Void> documentProcessorCompletionService = new ExecutorCompletionService<>(documentProcessorExecutor);

        try {
            //This will do the actual processing using the executor service passed and produce output in the word count stores
            DocumentProcessor documentProcessor = new DocumentProcessor(documentProcessorCompletionService, wordCountStores);

            //This traverses, parses the dom and asynchronously submits the dom documents to document processor
            UrlTraverser urlTraverser = new UrlTraverser(documentProcessor, internalLinksOnly, new DocumentProvider());
            int taskCount = urlTraverser.traverse(url, levels);

            //This is to track the completion. CompletionService helps in tracking the completion regardless of the order.
            for (int i = 0; i < taskCount; i++) {
                try {
                    Future<Void> result = documentProcessorCompletionService.take();
                    result.get();
                } catch (ExecutionException | InterruptedException e) {
                    logger.debug("while fetching results from documentProcessorExecutor", e);
                }
            }
            return Pair.of(wordCountStores.getTopNWords(topNCount), wordCountStores.getTopNWordPairs(topNCount));
        } finally {
            documentProcessorExecutor.shutdown();
        }
    }

    private ExecutorService newExecutorService() {
        return new ThreadPoolExecutor(
                propertyStore.getIntProperty(PropertyStore.DOCUMENT_PROCESSOR_CORE_POOL_SIZE),
                propertyStore.getIntProperty(PropertyStore.DOCUMENT_PROCESSOR_MAX_POOL_SIZE),
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("doc-processor-%d").build()); //this names the threads, useful for debugging
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        final int topNCount = 10;
        Pair<List<Pair<Word, Integer>>, List<Pair<WordPair, Integer>>> result =
                new Application(4, "https://www.314e.com/", true).getTopNWordsAndWordPairs(topNCount);
        assert result != null;
        logger.info("top {} words - {}", topNCount, result.getLeft());
        logger.info("top {} word pairs - {}", topNCount, result.getRight());
        logger.debug("seconds elapsed - {}", (System.currentTimeMillis() - start) / 1000);
    }

}
