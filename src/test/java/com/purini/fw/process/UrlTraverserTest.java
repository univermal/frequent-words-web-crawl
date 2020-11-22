package com.purini.fw.process;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class UrlTraverserTest {

    private static final Logger logger = LoggerFactory.getLogger(UrlTraverserTest.class);

    private static Document levelZeroDoc;
    private static Document levelOne1Doc;
    private static Document levelOne2Doc;
    private static Document levelTwo1Doc;
    private static Document levelTwo2Doc;
    private static Document levelThree1Doc;
    private static Document levelThree2Doc;

    private static final String url = "http://zero.com";

    @BeforeAll
    static void setup() throws IOException {
        levelZeroDoc = Jsoup.parse(getFileAsString("zero.html"));
        levelZeroDoc.setBaseUri(url);
        levelOne1Doc = Jsoup.parse(getFileAsString("one1.html"));
        levelOne1Doc.setBaseUri(url + "/one1");
        levelOne2Doc = Jsoup.parse(getFileAsString("one2.html"));
        levelOne2Doc.setBaseUri(url + "/one2");
        levelTwo1Doc = Jsoup.parse(getFileAsString("two1.html"));
        levelTwo1Doc.setBaseUri(url + "/two1");
        levelTwo2Doc = Jsoup.parse(getFileAsString("two2.html"));
        levelTwo2Doc.setBaseUri(url + "/two2");
        levelThree1Doc = Jsoup.parse(getFileAsString("three1.html"));
        levelThree1Doc.setBaseUri(url + "/three1");
        levelThree2Doc = Jsoup.parse(getFileAsString("three2.html"));
        levelThree2Doc.setBaseUri(url + "/three2");
    }

    @Test
    public void testTraversal() throws IOException {

        DocumentProcessor documentProcessor = mock(DocumentProcessor.class);
        DocumentProvider documentProvider = mock(DocumentProvider.class);

        doNothing().when(documentProcessor).submitAsync(any(Document.class));
        when(documentProvider.getDocument(url)).thenReturn(levelZeroDoc);
        when(documentProvider.getDocument(url + "/one1")).thenReturn(levelOne1Doc);
        when(documentProvider.getDocument(url + "/one2")).thenReturn(levelOne2Doc);
        when(documentProvider.getDocument(url + "/two1")).thenReturn(levelTwo1Doc);
        when(documentProvider.getDocument(url + "/two2")).thenReturn(levelTwo2Doc);
        when(documentProvider.getDocument(url + "/three1")).thenReturn(levelThree1Doc);
        when(documentProvider.getDocument(url + "/three2")).thenReturn(levelThree2Doc);

        UrlTraverser urlTraverser = new UrlTraverser(documentProcessor, true, documentProvider);
        int noOfLinks = urlTraverser.traverse(url, 2);
        Assertions.assertEquals(6, noOfLinks);
        //Note three2 gets picked because zero is directly reference two & three1 doesn't as it is effectively more deep
        verify(documentProcessor, times(6)).submitAsync(any(Document.class));
    }

    private static String getFileAsString(String filePath) throws IOException {
        return String.join("", Files.readAllLines(Paths.get(UrlTraverserTest.class.getClassLoader().getResource(filePath).getFile())));
    }
}
