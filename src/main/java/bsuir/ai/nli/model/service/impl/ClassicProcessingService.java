package bsuir.ai.nli.model.service.impl;

import bsuir.ai.nli.model.service.ProcessingService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicProcessingService implements ProcessingService {

    private static final int NUMBER_OF_SENTENCES = 7;

    private List<String> documents;

    @Override
    public String process(List<String> allSentences) {
        documents = allSentences;
        List<String> meaningfulSentences = getMeaningfulSentences();

        return String.join(". ", meaningfulSentences);
    }

    private Double getPositionInDocument(String document, String sentence){
        return 1 - ((double)document.indexOf(sentence) / document.length());
    }

    private Double getPositionInParagraph(String document, String sentence){
        String[] splitTextByParagraphs = getSplitTextByParagraphs(document);

        String paragraph = " ";
        for (String splitTextByParagraph : splitTextByParagraphs) {
            if(splitTextByParagraph.contains(sentence)){
                paragraph = splitTextByParagraph;
                break;
            }
        }


        return 1 - ((double) paragraph.indexOf(sentence) /  paragraph.length());
    }

    private List<String> getMeaningfulSentences(){
        Map<String, Double> weightSentences = getWeightSentences();

        Optional<Double> first = weightSentences.values()
                .stream().sorted(Comparator.reverseOrder()).skip(NUMBER_OF_SENTENCES).findFirst();

        return first.map(aDouble -> weightSentences.entrySet().stream()
                .filter(entrySet -> entrySet.getValue() > aDouble)
                .map(Map.Entry::getKey).collect(Collectors.toList()))
                .orElseGet(() -> new ArrayList<>(weightSentences.keySet()));
    }

    private Map<String, Double> getWeightSentences(){
        Map<String, Double> weightSentences = new LinkedHashMap<>();

        documents.forEach(document -> {
            String[] sentencesByDocument = getSplitTextBySentences(document);
            for (String sentence : sentencesByDocument) {
                Double weightSentence = getWeightSentence(document, sentence)
                        * getPositionInDocument(document, sentence)
                        * getPositionInParagraph(document, sentence);

                weightSentences.put(sentence, weightSentence);
            }
        });

        return weightSentences;
    }

    //Score(Si)
    private Double getWeightSentence(String document, String sentence){
        String[] splitWords = getSplitWords(sentence);

        return Arrays.stream(splitWords).mapToDouble((term) ->
                getTermFrequencyByDocument(term, sentence) * getIDFTermInDocument(term, document)).sum();
    }

    //w(t,D)
    private Double getIDFTermInDocument(String term, String document){
        Double result = 0.5 * (1 + (getTermFrequencyByDocument(term, document) /
                getMaxTermFrequencyInDocument(document)));

        result *= Math.log((double)documents.size() / getNumberDocumentWithTerm(term));

        return result;
    }

    //tf(t,D)
    private Double getTermFrequencyByDocument(String term, String document){
        String[] splitWords = getSplitWords(document);
        Map<String, Integer> termsOccurrences = getTermsOccurrences(splitWords);

        if(!termsOccurrences.containsKey(term)){
            return 0.0;
        }
        return (double)termsOccurrences.get(term) / splitWords.length;
    }



    //df(t)
    private Integer getNumberDocumentWithTerm(String term){
        Integer number = documents.stream()
                .mapToInt(document -> document.toLowerCase().contains(term) ? 1 : 0).sum();

        if(number == 0){
            return 0;
        }

        return number;
    }

    //tf max(D)
    private Double getMaxTermFrequencyInDocument(String document){
        String[] splitWords = getSplitWords(document);
        Map<String, Integer> termsOccurrences = getTermsOccurrences(splitWords);

        Double maxFrequency = termsOccurrences.values().stream()
                .mapToDouble(quantity -> (double) quantity / splitWords.length).max()
                .orElse(0.0);

        if(maxFrequency == 0.0){
            return 0.0;
        }

        return maxFrequency;
    }


    public static String[] getSplitWords(String text){
        return getCleanText(text).split(" ");
    }

    public static String[] getSplitTextBySentences(String text){
        return text.split("[.!?]");
    }

    public static String[] getSplitTextByParagraphs(String text){
        return text.split("\n\n");
    }

    public static String getCleanText(String text){
        return text
                .replaceAll("[–,.;:!?]", "")
                .replaceAll("\n", " ")
                .replaceAll("\t", " ")
                .replaceAll("  ( )*", " ").toLowerCase();
    }

    public static Map<String, Integer> getTermsOccurrences(String[] words){

        Map<String, Integer> initialForms = new HashMap<>();

        for (String word : words) {

            if (initialForms.containsKey(word)) {
                initialForms.put(word, initialForms.get(word) + 1);
            } else {
                initialForms.put(word, 1);
            }
        }

        return initialForms;
    }

}
