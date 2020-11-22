package com.purini.fw.process;

import com.purini.fw.domain.Word;
import com.purini.fw.domain.WordPair;
import com.purini.fw.utils.TextToWordUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordCountStoreTest {

    @Test
    public void testWord() {
        WordCountStore<Word> wordCountStore = new WordCountStore<>();
        String[] words = {"This", "is", "a", "test", "for", "a", "test", "in", "test"};
        wordCountStore.addWords(Arrays.stream(words).map(Word::new).collect(Collectors.toList()));
        List<Pair<Word, Integer>> wordFrequency =  wordCountStore.getTopNWords(10);
        Map<Word, Integer> wordFrequencyMap = wordFrequency.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(6, wordFrequency.size());
        assertEquals(6, wordFrequencyMap.size());

        assertEquals(3, wordFrequencyMap.get(new Word("test")));
        assertEquals(2, wordFrequencyMap.get(new Word("a")));
        assertEquals(1, wordFrequencyMap.get(new Word("This")));
        assertEquals(1, wordFrequencyMap.get(new Word("is")));
        assertEquals(1, wordFrequencyMap.get(new Word("for")));
        assertEquals(1, wordFrequencyMap.get(new Word("in")));

        //Ensure order of results is correct
        assertEquals(Pair.of(new Word("test"), 3), wordFrequency.get(0));
        assertEquals(Pair.of(new Word("a"), 2), wordFrequency.get(1));

    }

    @Test
    public void testWordWithMultipleCalls() {
        WordCountStore<Word> wordCountStore = new WordCountStore<>();

        String[] words = {"This", "is", "a", "test", "for", "a", "test", "in", "test"};
        wordCountStore.addWords(Arrays.stream(words).map(Word::new).collect(Collectors.toList()));

        String[] words2 = {"continuing", "second", "run", "of", "a", "test", "in", "test"};
        wordCountStore.addWords(Arrays.stream(words2).map(Word::new).collect(Collectors.toList()));

        List<Pair<Word, Integer>> wordFrequency =  wordCountStore.getTopNWords(10);
        Map<Word, Integer> wordFrequencyMap = wordFrequency.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(10, wordFrequency.size());
        assertEquals(10, wordFrequencyMap.size());

        assertEquals(5, wordFrequencyMap.get(new Word("test")));
        assertEquals(3, wordFrequencyMap.get(new Word("a")));
        assertEquals(2, wordFrequencyMap.get(new Word("in")));
        assertEquals(1, wordFrequencyMap.get(new Word("This")));
        assertEquals(1, wordFrequencyMap.get(new Word("is")));
        assertEquals(1, wordFrequencyMap.get(new Word("for")));
        assertEquals(1, wordFrequencyMap.get(new Word("continuing")));
        assertEquals(1, wordFrequencyMap.get(new Word("second")));
        assertEquals(1, wordFrequencyMap.get(new Word("run")));
        assertEquals(1, wordFrequencyMap.get(new Word("of")));

        //Ensure order of results is correct
        assertEquals(Pair.of(new Word("test"), 5), wordFrequency.get(0));
        assertEquals(Pair.of(new Word("a"), 3), wordFrequency.get(1));
        assertEquals(Pair.of(new Word("in"), 2), wordFrequency.get(2));


    }

    @Test
    public void testWordWithMixedCase() {
        WordCountStore<Word> wordCountStore = new WordCountStore<>();
        String[] words = {"This", "is", "a", "test", "with", "A", "Test", "in", "test"};
        wordCountStore.addWords(Arrays.stream(words).map(Word::new).collect(Collectors.toList()));
        List<Pair<Word, Integer>> wordFrequency =  wordCountStore.getTopNWords(10);
        Map<Word, Integer> wordFrequencyMap = wordFrequency.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(6, wordFrequency.size());
        assertEquals(6, wordFrequencyMap.size());

        assertEquals(3, wordFrequencyMap.get(new Word("test"))); //last occurrence wins
        assertEquals(2, wordFrequencyMap.get(new Word("A"))); //last occurrence wins
        assertEquals(1, wordFrequencyMap.get(new Word("This")));
        assertEquals(1, wordFrequencyMap.get(new Word("is")));
        assertEquals(1, wordFrequencyMap.get(new Word("with")));
        assertEquals(1, wordFrequencyMap.get(new Word("in")));

    }

    @Test
    public void testWordWithNLessThanSize() {
        WordCountStore<Word> wordCountStore = new WordCountStore<>();
        String[] words = {"This", "is", "a", "test", "within", "A", "Test", "in", "test"};
        wordCountStore.addWords(Arrays.stream(words).map(Word::new).collect(Collectors.toList()));
        List<Pair<Word, Integer>> wordFrequency =  wordCountStore.getTopNWords(3);
        Map<Word, Integer> wordFrequencyMap = wordFrequency.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(3, wordFrequency.size());
        assertEquals(3, wordFrequencyMap.size());

        assertEquals(3, wordFrequencyMap.get(new Word("test")));
        assertEquals(2, wordFrequencyMap.get(new Word("A")));

    }

    @Test
    public void testWordPair() {
        WordCountStore<WordPair> wordCountStore = new WordCountStore<>();
        String[] words = {"This", "is", "a", "Test", "for", "a", "test", "in", "test"};
        List<WordPair> wordPairs = TextToWordUtil.toWordPairs(Arrays.stream(words).map(Word::new).collect(Collectors.toList()));
        wordCountStore.addWords(wordPairs);
        List<Pair<WordPair, Integer>> wordFrequency =  wordCountStore.getTopNWords(10);

        Map<WordPair, Integer> wordPairFrequencyMap = wordFrequency.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(7, wordFrequency.size());
        assertEquals(7, wordPairFrequencyMap.size());

        assertEquals(2, wordPairFrequencyMap.get(new WordPair("a", "test"))); //last one wins
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("This", "is")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("is", "a")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("Test", "for")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("for", "a")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("test", "in")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("in", "test")));
    }

    @Test
    public void testWordPairWithMultipleCalls() {
        WordCountStore<WordPair> wordCountStore = new WordCountStore<>();
        String[] words = {"This", "is", "a", "Test", "for", "a", "test", "in", "test"};
        List<WordPair> wordPairs = TextToWordUtil.toWordPairs(Arrays.stream(words).map(Word::new).collect(Collectors.toList()));
        wordCountStore.addWords(wordPairs);

        String[] words2 = {"continuing", "second", "run", "of", "a", "test", "in", "test"};
        List<WordPair> wordPairs2 = TextToWordUtil.toWordPairs(Arrays.stream(words2).map(Word::new).collect(Collectors.toList()));
        wordCountStore.addWords(wordPairs2);

        List<Pair<WordPair, Integer>> wordFrequency =  wordCountStore.getTopNWords(12);
        Map<WordPair, Integer> wordPairFrequencyMap = wordFrequency.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        assertEquals(11, wordFrequency.size());
        assertEquals(11, wordPairFrequencyMap.size());

        assertEquals(3, wordPairFrequencyMap.get(new WordPair("a", "test"))); //last one wins
        assertEquals(2, wordPairFrequencyMap.get(new WordPair("test", "in")));
        assertEquals(2, wordPairFrequencyMap.get(new WordPair("in", "test")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("This", "is")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("is", "a")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("Test", "for")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("for", "a")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("continuing", "second")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("second", "run")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("run", "of")));
        assertEquals(1, wordPairFrequencyMap.get(new WordPair("of", "a")));

        //Ensure order of results is correct
        assertEquals(Pair.of(new WordPair("a", "test"), 3), wordFrequency.get(0));
        assertEquals(2, wordFrequency.get(1).getRight());
        assertEquals(2, wordFrequency.get(2).getRight());
        assertEquals(1, wordFrequency.get(3).getRight());

    }


}
