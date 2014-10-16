package edu.cmu.lti.f14.hw3.hw3_haoz1.casconsumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_haoz1.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {
    
	
	/* Discard
	public ArrayList<Integer> qIdList;

	public ArrayList<Integer> relList;
	
    public ArrayList<HashMap<String,Integer>> tokenfrequencyList;
    */
	
	/**
	 * Here I put qID, rel and other information in one single Object: Documents.
	 * This will simply later Cosine Distance calculation. 
	 */
	
	/** Global dictionary **/
    public HashSet<String> dictionary;
    
    /** NonQuery documents **/
    public ArrayList<Documents> NonQuery;
    
    /** Query documents **/
    public ArrayList<Documents> Query;
		
	public void initialize() throws ResourceInitializationException {
		
		dictionary = new HashSet<String>();
        
		NonQuery = new ArrayList<Documents>();
		
		Query = new ArrayList<Documents>();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
		HashMap<String,Integer> buffer = new HashMap<String,Integer>(); 
		if (it.hasNext()) {
			Document doc = (Document) it.next();
			String content = doc.getText();
			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);
            for(Token token : tokenList){
                String word = token.getText();
                int value = token.getFrequency();
                if(!dictionary.contains(word)){
                	dictionary.add(word);
                }
                buffer.put(word, value);
            }
			Documents document = new Documents(doc.getQueryID(),doc.getRelevanceValue(),buffer,content);
			if(doc.getRelevanceValue()==99){
				Query.add(document);
			}
			else{
				NonQuery.add(document);
			}
			//Do something useful here

		}

	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
        List<List<CosDIndex>> query_index = new ArrayList<List<CosDIndex>>();
		
		// TODO :: compute the cosine similarity measure
		for(Documents query : Query){
			List<CosDIndex> rank = new ArrayList<CosDIndex>();
			int qID = query.qid;
			for(int i=0;i<NonQuery.size();i++){
				if(NonQuery.get(i).qid==qID){
					double cosdist = computeCosineSimilarity(query.token_fre, NonQuery.get(i).token_fre);
					rank.add(new CosDIndex(cosdist,i));
				}
			}
			Collections.sort(rank, new Coscompare());
			query_index.add(rank);
		}
		
		// TODO :: compute the rank of retrieved sentences
		List<Integer> res = new ArrayList<Integer>();
		BufferedWriter out = new BufferedWriter(new FileWriter(new File("report.txt")));
		for(int i=0;i<Query.size();i++){
			List<CosDIndex> rank = query_index.get(i);
			int qID = Query.get(i).qid;
			int j = 0;
			//&& NonQuery.get(rank.get(j).index_to_NonQuery).qid !=qID
			while(j<rank.size() && NonQuery.get(rank.get(j).index_to_NonQuery).rel !=1){
				/* Error analysis
				int rel = NonQuery.get(rank.get(j).index_to_NonQuery).rel;
				String content = NonQuery.get(rank.get(j).index_to_NonQuery).content;
				int j_inc1 = j+1;
				out.write("cosine="+String.format("%.4f",rank.get(j).cosdist)+"\t"+"rank="+j_inc1+"\t"+"qid="+qID+"\t"+"rel="+rel+"\t"+content+"\n");
				*/
				j++;
			}
		    int rel = NonQuery.get(rank.get(j).index_to_NonQuery).rel;
		    String content = NonQuery.get(rank.get(j).index_to_NonQuery).content;
			res.add(j+1);
			int j_inc1 = j+1;
			out.write("cosine="+String.format("%.4f",rank.get(j).cosdist)+"\t"+"rank="+j_inc1+"\t"+"qid="+qID+"\t"+"rel="+rel+"\t"+content+"\n");
		}
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr(res);
		out.write("MRR="+String.format("%.4f",metric_mrr)+"\n");
		out.flush();
		System.out.println("(MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}
    
	
	private class Documents{
		public int qid;
		public int rel;
		public String content;
		public HashMap<String,Integer> token_fre;
		public Documents(int qid, int rel, HashMap<String,Integer> token_fre, String content){
			this.qid = qid;
			this.rel = rel;
			this.token_fre = token_fre;
			this.content = content;
		}
	}
	
	private class CosDIndex{
		public Double cosdist;
		public int index_to_NonQuery;
		public CosDIndex(double cosdist, int index_to_NonQuery){
			this.index_to_NonQuery = index_to_NonQuery;
			this.cosdist = cosdist;
		}
	}
	
	private class Coscompare implements Comparator<CosDIndex>{
		@Override
		public int compare(CosDIndex i, CosDIndex j) {
			return -i.cosdist.compareTo(j.cosdist);
		}
	}
	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;

		// TODO :: compute cosine similarity between two sentences
		double nominator = 0;
		double query_len = 0;
		double doc_len = 0;
		for(String key : queryVector.keySet()){
			
			query_len = query_len + Math.pow(queryVector.get(key),2);
			if(docVector.containsKey(key)){
				nominator = nominator + queryVector.get(key)*docVector.get(key);
			}
		}
		for(String key : docVector.keySet()){
			doc_len = doc_len + Math.pow(docVector.get(key),2);
		}
		cosine_similarity = nominator/(Math.sqrt(doc_len)*Math.sqrt(query_len));
        
		return cosine_similarity;
	}

	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr(List<Integer> res) {
		
		double metric_mrr=0.0;

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		for(Integer i : res){
			metric_mrr = metric_mrr + 1.0/i;
		}
		
		return metric_mrr/res.size();
	}

}
