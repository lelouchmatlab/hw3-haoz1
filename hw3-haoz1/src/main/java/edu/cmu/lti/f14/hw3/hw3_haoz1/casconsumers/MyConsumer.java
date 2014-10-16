package edu.cmu.lti.f14.hw3.hw3_haoz1.casconsumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.MyDocument;
import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_haoz1.utils.Utils;

public class MyConsumer extends CasConsumer_ImplBase {
    
	
	
	
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
			MyDocument doc = (MyDocument) it.next();
			String content = doc.getText();
			//Make sure that your previous annotators have populated this in CAS
			FloatArray uima_feat = doc.getFeatureVector();
			float[] feat = uima_feat.toArray();
			Documents document = new Documents(doc.getQueryID(),doc.getRelevanceValue(),feat,content);
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
					double cosdist = computeDistance(query.feat, NonQuery.get(i).feat);
					rank.add(new CosDIndex(cosdist,i));
				}
			}
			Collections.sort(rank, new Coscompare());
			query_index.add(rank);
		}
		
		// TODO :: compute the rank of retrieved sentences
		List<Integer> res = new ArrayList<Integer>();
		//BufferedWriter out = new BufferedWriter(new FileWriter(new File("report.txt")));
		for(int i=0;i<Query.size();i++){
			List<CosDIndex> rank = query_index.get(i);
			int qID = Query.get(i).qid;
			int j = 0;
			
			//* for Debugging
			//j++;
			int rel = NonQuery.get(rank.get(j).index_to_NonQuery).rel;
			String content = NonQuery.get(rank.get(j).index_to_NonQuery).content;
			//System.out.println("cosine="+rank.get(j).cosdist+"\t"+"rank=2"+"\t"+"qid="+qID+"\t"+"rel="+rel+"\t"+content+"\n");
			//j--;
			//*/
			while(j<rank.size() && NonQuery.get(rank.get(j).index_to_NonQuery).rel !=1){
				j++;
			}
		    rel = NonQuery.get(rank.get(j).index_to_NonQuery).rel;
		    content = NonQuery.get(rank.get(j).index_to_NonQuery).content;
			res.add(j+1);
			int j_inc1 = j+1;
			System.out.println("Distance="+rank.get(j).cosdist+"\t"+"rank="+j_inc1+"\t"+"qid="+qID+"\t"+"rel="+rel+"\t"+content+"\n");
		}
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr(res);
		//out.write("MRR="+metric_mrr+"\n");
		//out.flush();
		System.out.println("(MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}
    
	
	private class Documents{
		public int qid;
		public int rel;
		public String content;
		public float[] feat;
		public Documents(int qid, int rel, float[] feat, String content){
			this.qid = qid;
			this.rel = rel;
			this.feat = feat;
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
	private double computeDistance(float[] queryVector,
			float[] docVector) {
		double cosine_similarity=0.0;
		double Edistance = 0.0;
		double KLdistance = 0.0;

		// TODO :: compute cosine similarity between two sentences
		
		float[] temp1 = new float[queryVector.length];
		float[] temp2 = new float[docVector.length];
		float sum1 =0;
		float sum2 = 0;
		for(int i=0;i<docVector.length;i++){
			temp1[i]=(float) Math.exp(queryVector[i]);
			temp2[i]=(float) Math.exp(docVector[i]);
			sum1+=temp1[i];
			sum2+=temp2[i];
		}
		for(int i=0;i<docVector.length;i++){
			temp1[i]=temp1[i]/sum1;
			temp2[i]=temp2[i]/sum2;
		}
		
		for(int i=0;i<docVector.length;i++){
			KLdistance = KLdistance + temp1[i]*Math.log(temp1[i]/temp2[i]);
		}
		
		///*
		for(int i=0;i<queryVector.length;i++){
			Edistance+=Math.pow((queryVector[i]-docVector[i]), 2);
		}
        //*/
		
		double nominator = 0;
		double query_len = 0;
		double doc_len = 0;
		for(int i=0;i<docVector.length;i++){
			nominator+=queryVector[i]*docVector[i];
			query_len+=Math.pow(queryVector[i],2);
			doc_len+=Math.pow(docVector[i],2);
		}
		cosine_similarity = nominator/(Math.sqrt(query_len)*Math.sqrt(doc_len));
		
		return KLdistance;
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
