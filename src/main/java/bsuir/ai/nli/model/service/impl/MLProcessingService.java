package bsuir.ai.nli.model.service.impl;

import bsuir.ai.nli.model.service.ProcessingService;
import com.google.common.collect.ImmutableList;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import com.medallia.word2vec.neuralnetwork.NeuralNetworkType;
import javafx.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MLProcessingService implements ProcessingService {

    private String mainTerm;
    private static final int NUMBER_OF_SENTENCES = 7;

    @Override
    public String process(List<String> allSentences) {
        Word2VecModel model = getTestWord2Vec(allSentences);
        String fullText = String.join("", allSentences);
        String[] splitTextBySentences = getSplitTextBySentences(fullText);
        List<Pair> weightSentences = Arrays.stream(splitTextBySentences).map(sentence -> {
            String[] splitWords = getSplitWords(sentence);

            double weightSentence = Arrays.stream(splitWords).mapToDouble(word -> {
                return getWeightWord(model, word);
            }).sum();

            return new Pair<String, Double>(sentence, weightSentence);
        }).collect(Collectors.toList());

        Optional<Double> first = weightSentences.stream().map(pair -> (Double)pair.getValue())
                .sorted(Comparator.reverseOrder()).skip(NUMBER_OF_SENTENCES).findFirst();


        List<Object> snippetByML = first.map(aDouble -> weightSentences.stream()
                .filter(pair -> (Double)pair.getValue() > aDouble)
                .map(Pair::getKey).collect(Collectors.toList()))
                .orElseGet(() -> weightSentences.stream().map(Pair::getKey).collect(Collectors.toList()));

        return snippetByML.stream().map(sentence -> (String) sentence).collect(Collectors.joining("."));
    }

    private Word2VecModel getTestWord2Vec(List<String> allSentences) {

        try {
            String fullText = String.join("", allSentences);
            List<List<String>> sentences = allSentences.stream()
                    .map( document -> Arrays.stream(getSplitTextBySentences(document)).collect(Collectors.toList()))
                    .collect(Collectors.toList());

            String[] splitTextBySentences = getSplitTextBySentences(fullText);
            List<List<String>> collect = Arrays.stream(splitTextBySentences)
                    .map(sentence -> Arrays.stream(getSplitWords(sentence))
                            .collect(Collectors.toList())).collect(Collectors.toList());

            Word2VecModel model = Word2VecModel.trainer()
                    .setWindowSize(15)
                    .type(NeuralNetworkType.SKIP_GRAM)
                    .setLayerSize(6000)
                    .useNegativeSamples(25)
                    .setDownSamplingRate(1e-4)
                    .setNumIterations(5)
                    .train(collect);

            Iterable<String> vocab = model.getVocab();

            List<ImmutableList<Double>> vectors = new ArrayList<>();

            Iterator<String> iterator = vocab.iterator();

            for(int i = 0; i < 7; i++){
                String str = iterator.next();

                try {
                    if(str.length() >= 4) {
                        vectors.add(model.forSearch().getRawVector(str));
                    }else {
                        i--;
                    }
                } catch (Searcher.UnknownWordException e) {

                }
            }

            int size = vectors.get(0).size();

            List<Double> mainVector = new ArrayList<>();

            for(int i = 0; i < size; i++){
                Double property = 1D;

                for (ImmutableList<Double> vector : vectors) {
                    if (!vector.get(i).equals(0D)) {
                        property *= vector.get(i);
                    }
                }

                mainVector.add((calculate(Math.abs(property), (double)vectors.size())));
            }


            double[] target = new double[mainVector.size()];
            for (int i = 0; i < target.length; i++) {
                target[i] = mainVector.get(i);
            }

            List<Searcher.Match> matches = model.forSearch().getMatches(target, 1);
            mainTerm = matches.get(0).match();

            return model;
        }catch (Exception ex){
            return null;
        }
    }
    public Double calculate(Double base, Double n) {
        return Math.pow(Math.E, Math.log(base)/n);
    }

    private Double getWeightWord(Word2VecModel model, String term){
        try{
            return model.forSearch().cosineDistance(term, mainTerm);
        }catch (Exception ex){
            return 0.001;
        }
    }


    public static String getCleanText(String text){
        return text
                .replaceAll("[–,.;:!?]", "")
                .replaceAll("\n", " ")
                .replaceAll("\t", " ")
                .replaceAll("  ( )*", " ").toLowerCase();
    }

    public static String[] getSplitTextBySentences(String text){
        return text.split("[.!?]");
    }

    public static String[] getSplitWords(String text){
        return getCleanText(text).split(" ");
    }

}
