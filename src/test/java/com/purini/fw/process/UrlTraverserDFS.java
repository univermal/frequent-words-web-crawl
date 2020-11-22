package com.purini.fw.process;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.purini.fw.utils.UrlUtil.getHostPort;
import static com.purini.fw.utils.UrlUtil.isValid;

public class UrlTraverserDFS {

    private static final Logger logger = LoggerFactory.getLogger(UrlTraverserDFS.class);
    private final DocumentProcessor documentProcessor;
    private final boolean internalLinksOnly;
    private final DocumentProvider documentProvider;
    private final Set<String> visited;
    private final AtomicInteger counter = new AtomicInteger();

    public UrlTraverserDFS(DocumentProcessor documentProcessor, boolean internalLinksOnly, DocumentProvider documentProvider) {
        this.documentProcessor = documentProcessor;
        this.internalLinksOnly = internalLinksOnly;
        this.documentProvider = documentProvider;
        this.visited = new HashSet<>();
    }

    /**
     * Recursive method to traverse the hyperlinks.
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
        if (!visited.contains(url) && levels >= 0) {
            visited.add(url);

            Document doc = null;
            try {
                doc = documentProvider.getDocument(url);
            } catch (IOException e) {
                logger.warn("Error: {}, likely dead/erroneous link: {}", e.getMessage(), url);
                return 0;
            }
            logger.debug("processing doc for url - {}, level - {}", url, levels);
            documentProcessor.submitAsync(doc);
            counter.incrementAndGet();

            List<String> links = getLinks(doc, internalLinksOnly);
            for (String link : links) {
                traverse(link, levels - 1);
            }
        }
        return counter.get();
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
        List<Element> links = doc.getElementsByTag("a");
        return links.stream()
                .map(l -> l.attr("href"))
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
