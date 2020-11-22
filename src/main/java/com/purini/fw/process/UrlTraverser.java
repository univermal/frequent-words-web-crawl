package com.purini.fw.process;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.purini.fw.utils.UrlUtil.*;

public class UrlTraverser {

    private static final Logger logger = LoggerFactory.getLogger(UrlTraverser.class);
    private final DocumentProcessor documentProcessor;
    private final boolean internalLinksOnly;
    private final DocumentProvider documentProvider;
    private final Set<String> visited;
    private final AtomicInteger counter = new AtomicInteger();

    public UrlTraverser(DocumentProcessor documentProcessor, boolean internalLinksOnly, DocumentProvider documentProvider) {
        this.documentProcessor = documentProcessor;
        this.internalLinksOnly = internalLinksOnly;
        this.documentProvider = documentProvider;
        this.visited = new HashSet<>();
    }

    /**
     * BFS method to traverse the hyperlinks, this ensures that priority is given to immediate levels.
     * BFS also allows certain level of parallelism as opposed to DFS in this case.
     * Stops when the given number of levels are traversed.
     * Keeps track of visited links to avoid duplicates.
     * Parses the document and collects links from it and traverses the links.
     * Submits the document to Document processor which processes it asynchronously.
     *
     * @param url - starting url to parse and extract links from
     * @param levels - number of levels to traverse
     * @return number of valid links processed
     */
    public int traverse(String url, int levels) {

        int depth = 0;
        Set<String> visited = ConcurrentHashMap.newKeySet();
        ConcurrentLinkedQueue<Pair<Document, Integer>> docsToTraverse = new ConcurrentLinkedQueue<>();

        visited.add(url);
        Document doc = processAndGetDoc(url, depth);
        if (doc == null) {
            logger.error("Invalid document or url {}", url);
            return 0;
        }
        counter.incrementAndGet();
        docsToTraverse.add(Pair.of(doc, depth));

        while (!docsToTraverse.isEmpty() && depth < levels) {
            final Pair<Document, Integer> docDepth = docsToTraverse.poll();
            Document currentDoc = docDepth.getLeft();
            final int currentDepth = docDepth.getRight();
            depth = currentDepth;
            List<String> childUrls = getLinks(currentDoc, internalLinksOnly);

            childUrls.parallelStream().forEach(childUrl -> {
                if (!visited.contains(childUrl)) {
                    visited.add(childUrl);
                    Document childDoc = processAndGetDoc(childUrl, currentDepth + 1);
                    if (childDoc != null) {
                        counter.incrementAndGet();
                        docsToTraverse.add(Pair.of(childDoc, currentDepth + 1));
                    }
                }
            });
        }
        return counter.get();
    }

    private Document processAndGetDoc(String url, int depth) {
        Document doc = null;
        try {
            doc = documentProvider.getDocument(url);
        } catch (IOException e) {
            logger.warn("Error: {}, likely dead/erroneous link: {}", e.getMessage(), url);
            return null;
        }
        logger.debug("processing doc for url - {}, depth - {}", url, depth);
        documentProcessor.submitAsync(doc);
        return doc;
    }

    /**
     * Gets links from the doc which are internal to the website.
     * E.g. for base uri http://www.abc.com:8080, it would only return
     * links that have base address as http://www.abc.com:8080 or https://www.abc.com:8080
     * @param doc - document to extract links from
     * @param internalLinksOnly whether to filter only internal links or all links
     * @return list of links
     */
    private List<String> getLinks(Document doc, boolean internalLinksOnly) {
        String docHostPort = getHostPort(doc.baseUri());
        String docProtocol = getProtocol(doc.baseUri());
        List<Element> links = doc.getElementsByTag("a");
        return links.stream()
                .map(l -> l.attr("href"))
                .map(s -> s.startsWith("/") ? formUrlFromRelativeUrl(docHostPort, docProtocol, s) : s)
                .filter(s -> {
                    final boolean isValidUrl = isValid(s);
                    if (internalLinksOnly) {
                        String childHostPort = isValidUrl ? getHostPort(s) : null;
                        return docHostPort != null && docHostPort.equalsIgnoreCase(childHostPort);
                    } else {
                        return isValidUrl;
                    }
                })
                .collect(Collectors.toList());
    }
}
