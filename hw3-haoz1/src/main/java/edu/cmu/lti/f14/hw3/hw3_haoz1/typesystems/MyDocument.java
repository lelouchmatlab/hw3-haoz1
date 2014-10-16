

/* First created by JCasGen Tue Oct 14 13:15:54 EDT 2014 */
package edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FloatArray;


/** 
 * Updated by JCasGen Tue Oct 14 15:25:45 EDT 2014
 * XML source: /home/haoz1/mygit/hw3-haoz1/hw3-haoz1/src/main/resources/descriptors/retrievalsystem/MyAnnotatorDescriptor.xml
 * @generated */
public class MyDocument extends Document {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(MyDocument.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected MyDocument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public MyDocument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public MyDocument(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public MyDocument(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: FeatureVector

  /** getter for FeatureVector - gets 
   * @generated
   * @return value of the feature 
   */
  public FloatArray getFeatureVector() {
    if (MyDocument_Type.featOkTst && ((MyDocument_Type)jcasType).casFeat_FeatureVector == null)
      jcasType.jcas.throwFeatMissing("FeatureVector", "edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.MyDocument");
    return (FloatArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((MyDocument_Type)jcasType).casFeatCode_FeatureVector)));}
    
  /** setter for FeatureVector - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeatureVector(FloatArray v) {
    if (MyDocument_Type.featOkTst && ((MyDocument_Type)jcasType).casFeat_FeatureVector == null)
      jcasType.jcas.throwFeatMissing("FeatureVector", "edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.MyDocument");
    jcasType.ll_cas.ll_setRefValue(addr, ((MyDocument_Type)jcasType).casFeatCode_FeatureVector, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for FeatureVector - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public float getFeatureVector(int i) {
    if (MyDocument_Type.featOkTst && ((MyDocument_Type)jcasType).casFeat_FeatureVector == null)
      jcasType.jcas.throwFeatMissing("FeatureVector", "edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.MyDocument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((MyDocument_Type)jcasType).casFeatCode_FeatureVector), i);
    return jcasType.ll_cas.ll_getFloatArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((MyDocument_Type)jcasType).casFeatCode_FeatureVector), i);}

  /** indexed setter for FeatureVector - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setFeatureVector(int i, float v) { 
    if (MyDocument_Type.featOkTst && ((MyDocument_Type)jcasType).casFeat_FeatureVector == null)
      jcasType.jcas.throwFeatMissing("FeatureVector", "edu.cmu.lti.f14.hw3.hw3_haoz1.typesystems.MyDocument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((MyDocument_Type)jcasType).casFeatCode_FeatureVector), i);
    jcasType.ll_cas.ll_setFloatArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((MyDocument_Type)jcasType).casFeatCode_FeatureVector), i, v);}
  }

    