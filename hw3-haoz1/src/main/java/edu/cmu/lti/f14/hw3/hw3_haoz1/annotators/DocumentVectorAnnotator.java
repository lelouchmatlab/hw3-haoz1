package edu.cmu.lti.f14.hw3.hw3_haoz1.annotators;

import java.util.*;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.tartarus.snowball.SnowballStemmer;

import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_haoz1.utils.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {
    
	protected StanfordCoreNLP pipeline;
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
       
		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}
	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {
		/*
		Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, lemma");

        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = new StanfordCoreNLP(props);
		*/
		String docText = doc.getText();
		//TO DO: construct a vector of tokens and update the tokenList in CAS
		String[] docWords = docText.split("\\s+");
		 
        
		//String[] docWords = docText.replaceAll("[^a-zA-Z]", " ").toLowerCase().split("\\s+");
		
		//List<String> docWords = lemmatize(docText);
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		for(String word : docWords){
			if(counter.containsKey(word)){
				counter.put(word, counter.get(word)+1);
			}
			else{
				counter.put(word, 1);
			}
		}
		ArrayList<Token> token_list = new ArrayList<Token>();
		for(String unique_word : counter.keySet()){
			Token token = new Token(jcas);
			token.setText(unique_word);
			token.setFrequency(counter.get(unique_word));
			token_list.add(token);
		}
		FSList fsTokenList = Utils.fromCollectionToFSList(jcas, token_list);
		doc.setTokenList(fsTokenList);
	}
	
	 public List<String> lemmatize(String documentText)
	    {
	        List<String> lemmas = new LinkedList<String>();

	        // create an empty Annotation just with the given text
	        edu.stanford.nlp.pipeline.Annotation document = new edu.stanford.nlp.pipeline.Annotation(documentText);

	        // run all Annotators on this text
	        this.pipeline.annotate(document);

	        // Iterate over all of the sentences found
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) {
	            // Iterate over all tokens in a sentence
	            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	                // Retrieve and add the lemma for each word into the list of lemmas
	                lemmas.add(token.get(LemmaAnnotation.class));
	            }
	        }

	        return lemmas;
	    }

}
