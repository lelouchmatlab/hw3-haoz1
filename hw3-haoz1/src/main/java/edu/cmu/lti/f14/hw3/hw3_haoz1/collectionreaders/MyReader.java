package edu.cmu.lti.f14.hw3.hw3_haoz1.collectionreaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;

public class MyReader extends CollectionReader_ImplBase {
    
	
	public static final String PARAM_Documents = "PARAM_Documents";
	public static final String PARAM_Features = "PARAM_Features";
	public BufferedReader in_docs;
	public BufferedReader in_feat;
	
	public int CurrentIndex;
	
	
	public void initialize(){
		CurrentIndex = 0;
		String docs = ((String) getConfigParameterValue(PARAM_Documents)).trim();
		String feat = ((String) getConfigParameterValue(PARAM_Features)).trim();
		FileReader file_docs = null;
		FileReader file_feat = null;
		try {
			file_docs = new FileReader(new File(docs));
		    in_docs = new BufferedReader(file_docs);
		    file_feat = new FileReader(new File(feat));
		    in_feat = new BufferedReader(file_feat);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	 
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		// TODO Auto-generated method stub
		CurrentIndex++;
		JCas jcas;
	    try {
	      jcas = aCAS.getJCas();
	    } catch (CASException e) {
	      throw new CollectionException(e);
	    }
	    
	    String text = in_docs.readLine();
	    String feat = in_feat.readLine();
	    jcas.setDocumentText(text+"|"+feat);

	}

	@Override
	public void close() throws IOException {
		in_docs.close();
		in_feat.close();
		// TODO Auto-generated method stub

	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		// TODO Auto-generated method stub
		return in_docs.ready();
	}

}
