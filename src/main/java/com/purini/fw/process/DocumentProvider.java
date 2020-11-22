package com.purini.fw.process;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.io.IOException;

public class DocumentProvider {

    /**
     * Jsoup library is used for fetching the page and parsing the DOM
     *
     * @param url - url to fetch
     * @return Document object representing the DOM
     * @throws IOException if there is problem fetching the page. e.g. 404, 500 etc.
     */
    @Nullable
    public Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
