package com.purini.fw.process;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import com.purini.fw.utils.TextToWordUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordCountStoreServiceTest {

    private final WordCountStoreService wordCountStoreService = new WordCountStoreService();

    @Test
    public void testWords() {

        List<Word> words = TextToWordUtil.toWords("The first technical break-through in quantum gravity! " +
                "Understanding quantum stuff is quite technical, so you first gotta learn from first.");
        wordCountStoreService.addWords(words);
        List<Pair<Word, Integer>> wordCounts = wordCountStoreService.getTopNWords(100);
        Map<Word, Integer> wordCountMap = wordCounts.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(10, wordCountMap.size());
        assertEquals(3, wordCountMap.get(new Word("first")));
        assertEquals(2, wordCountMap.get(new Word("technical")));
        assertEquals(2, wordCountMap.get(new Word("quantum")));
        assertEquals(1, wordCountMap.get(new Word("break-through")));
        assertEquals(1, wordCountMap.get(new Word("gravity")));
        assertEquals(1, wordCountMap.get(new Word("Understanding")));
        assertEquals(1, wordCountMap.get(new Word("stuff")));
        assertEquals(1, wordCountMap.get(new Word("quite")));
        assertEquals(1, wordCountMap.get(new Word("gotta")));
        assertEquals(1, wordCountMap.get(new Word("learn")));

        //Ensure order
        assertEquals(3, wordCounts.get(0).getRight());
        assertEquals(2, wordCounts.get(1).getRight());
        assertEquals(2, wordCounts.get(2).getRight());
        assertEquals(1, wordCounts.get(3).getRight());

    }

    @Test
    public void testWordPairs() {

        List<Word> words = TextToWordUtil.toWords("'The human brain is of high plasticity', the human I reckon.");
        List<WordPair> wordPairs = TextToWordUtil.toWordPairs(words);
        wordCountStoreService.addWordPairs(wordPairs);
        List<Pair<WordPair, Integer>> wordCounts = wordCountStoreService.getTopNWordPairs(100);
        Map<WordPair, Integer> wordCountMap = wordCounts.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(8, wordCountMap.size());
        assertEquals(2, wordCountMap.get(new WordPair("the", "human")));
        assertEquals(1, wordCountMap.get(new WordPair("human", "brain")));
        assertEquals(1, wordCountMap.get(new WordPair("brain", "is")));
        assertEquals(1, wordCountMap.get(new WordPair("of", "high")));
        assertEquals(1, wordCountMap.get(new WordPair("high", "plasticity")));
        assertEquals(1, wordCountMap.get(new WordPair("plasticity", "the")));
        assertEquals(1, wordCountMap.get(new WordPair("human", "I")));
        assertEquals(1, wordCountMap.get(new WordPair("I", "reckon")));

        //Ensure order
        assertEquals(2, wordCounts.get(0).getRight());
        assertEquals(1, wordCounts.get(1).getRight());

    }
}
