package edu.cmu.lti.f14.hw3.hw3_haoz1.annotators;

import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FloatArray;

import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.MyDocument;

public class MyAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		// reading sentence from the CAS 
				String sLine = jcas.getDocumentText();
				
				// TODO: make sure information from text collection are extracted correctly
				ArrayList<String> docInfo = parseDataLine(sLine);
				
				//This is to make sure that parsing done properly and 
				//minimal data for rel,qid,text are available to proceed 
				if(docInfo.size()<3){
					System.err.println("Not enough information in the line");
					return;
				}
				int rel = Integer.parseInt(docInfo.get(0));
				int qid = Integer.parseInt(docInfo.get(1));
				String txt = docInfo.get(2);
				String[] buf = docInfo.get(3).split(",");
				int dim = buf.length;
				//System.out.println(dim);
				FloatArray v = new FloatArray(jcas,dim);
				float[] vv= new float[dim];
				float sum = 0;
				for(int i=0;i<vv.length;i++){
					vv[i]=Float.parseFloat(buf[i]);
					sum = (float) (sum + Math.exp(vv[i]));
				}
				for(int i=0;i<vv.length;i++){
					vv[i]=(float) (Math.exp(vv[i])/sum);
					
				}
				
				v.copyFromArray(vv, 0, 0, dim);
				MyDocument doc = new MyDocument(jcas);
				doc.setText(txt);
				doc.setQueryID(qid);
				//Setting relevance value
				doc.setRelevanceValue(rel);
				doc.setFeatureVector(v);
				doc.addToIndexes();
				
				//Adding populated FeatureStructure to CAS
				jcas.addFsToIndexes(doc);

	}
	
	public static ArrayList<String> parseDataLine(String line) {
		ArrayList<String> docInfo;
        String [] buf = line.split("\\|");
		String [] rec  = buf[0].split("[\\t]");
		String    sResQid = (rec[0]).replace("qid=", "");
		String    sResRel = (rec[1]).replace("rel=", "");
		

		StringBuffer sResTxt = new StringBuffer();
		for (int i=2; i<rec.length; i++) {
			sResTxt.append(rec[i]).append(" ");					
		}

		docInfo = new ArrayList<String>();
		docInfo.add(sResRel);
		docInfo.add(sResQid);
		docInfo.add(sResTxt.toString());
		docInfo.add(buf[1]);
		return docInfo;
	}

}
