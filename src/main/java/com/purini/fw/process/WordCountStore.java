package com.purini.fw.process;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.purini.fw.domain.WordConstruct;
import com.purini.fw.domain.WordInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common store for storing word constructs: words or word pairs.
 *
 * The responsibility of this class is:
 *
 * 1) to store the word constructs keeping a count of the frequency of occurrence
 * 2) give out top N words by frequency (descending)
 *
 * For this it has two collections:
 *
 * One HashMap(Word Construct -> Frequency) which serves as an index on the
 * Word Construct, so that it is easy to get/update it's frequency.
 *
 * Second collection is for keeping the results always sorted by Frequency (descending).
 * It is a Sorted Multimap (Frequency -> Word Construct)
 *
 * Both the collections are not synchronized but the addWord method is.
 * The addWord method mutates both the collections and relies on exchange of information
 * between them, thus the need for synchronization.
 *
 * @param <T> generic parameter to denote either a WordConstruct i.e. Word or WordPair
 */
public class WordCountStore<T extends WordConstruct<T>> {

    private Map<T, Integer> wordToCount = new HashMap<>();
    private TreeMultimap<Integer, WordInfo<T>> countToWords = TreeMultimap.create(Ordering.<Integer>natural().reverse(), Ordering.natural());

    /**
     * Adds the word to both the collections.
     * - For the HashMap, it increases the frequency
     * - For the Multimap, it first removes the old frequency entry and then puts the new frequency
     * @param word - word to add
     */
    synchronized public void addWord(T word) {

        T lowerCaseWord = word.toLowerCase();
        int newCount = wordToCount.compute(lowerCaseWord, (w, c) -> c == null ? 1 : c + 1);
        if (newCount > 1) {
            // first remove from old count
            final int oldCount = newCount - 1;
            countToWords.remove(oldCount, new WordInfo<>(lowerCaseWord, word));
        }
        countToWords.put(newCount, new WordInfo<>(lowerCaseWord, word));
    }

    /**
     * Takes a list of words to add
     * @param words - words to add
     */
    public void addWords(List<T> words) {
        words.forEach(this::addWord);
    }

    /**
     * Gives out top n words, sorted by their frequency in descending order.
     * Note that it would return <n elements if the collection has <n
     * @param n - number of top words to return
     * @return a list of pair of word and count
     */
    public List<Pair<T, Integer>> getTopNWords(int n) {
        int maxTopWords = Math.min(n, wordToCount.size());
        List<Pair<T, Integer>> topWords = new ArrayList<>();
        int i = 0;
        for (Map.Entry<Integer, WordInfo<T>> entry : countToWords.entries()) {
            if (i >= maxTopWords) {
                break;
            }
            topWords.add(Pair.of(entry.getValue().getOriginalWord(), entry.getKey()));
            i++;
        }
        return topWords;
    }

}
