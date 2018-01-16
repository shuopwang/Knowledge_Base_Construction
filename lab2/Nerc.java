package lab2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.print.attribute.standard.RequestingUserName;
import javax.security.auth.kerberos.KerberosKey;

/** Skeleton for NERC task.
 * 
 * @author Fabian M. Suchanek
 *
 */
public class Nerc {

    /** Labels that we will attach to the words*/
    public enum Class {
        ARTIFACT, EVENT, GEO, NATURAL, ORGANIZATION, PERSON, TIME, OTHER
    }
	static String[] pos={"CC","DT","EX","IN","FW","JJR","JJS","LS","POS","MD","PDT","TO","RP","RB","RBS","RBR","PRP","PRP$","UH","VB","VBD","VBG","VBN","VBP","VBZ","SYM","WDT","WP","WP$","WRB","',",".",",",":"};
	static Set<String> set = new HashSet<String>(Arrays.asList(pos));
	static String[] titleStrings={"Dr.","Chairman","Councilor","President","Mr.","Minister"};
	static Set<String> titleSet = new HashSet<String>(Arrays.asList(titleStrings));
	static String[] vbStrings={"VB","VBD","VBG","VBN","VBZ","VBP"};
	static Set<String> vbSet = new HashSet<String>(Arrays.asList(vbStrings));
	static String[] eventString={"Olympic","War","I","II","Cup"};
	static Set<String> eventSet = new HashSet<String>(Arrays.asList(vbStrings));
	static String[] timeString={"Monday","Tuesday","Thursday","Wednesday","Friday","Saturday","Sunday","January","February","March","April","May","June","July","August","October","September","November","December"};
	static Set<String> timeSet = new HashSet<String>(Arrays.asList(timeString));
	static String[] artifactString={"GDP","NRC","AGOA","Web","Internet","Times","Museum","Nobel"};
	static Set<String> artifactSet = new HashSet<String>(Arrays.asList(artifactString));
	static int[] weights={2,4,2,2,2,5,3,2};
    //static ArrayList<Class> label_value=new ArrayList<>();
    /** Determines the class for the word at position 0 in the window*/
    public static Class findClass(Window window) {

        // Magic goes here
    	int i=1;
    	int width=window.width;
    	String[] word_window = new String[width * 2 + 1];
    	String[] sentenceNumbers=new String[width * 2 + 1];
    	String[] posTag=new String[width*2+1];
    	word_window[0]=window.getWordAt(0);
    	sentenceNumbers[0]=window.getSentenceNumberAt(0);
    	posTag[0]=window.getTagAt(0);
        if (set.contains(posTag[0]))
        	return Class.OTHER;
    	if(artifactSet.contains(word_window[0]))
    		return Class.ARTIFACT;
        if(eventSet.contains(word_window[0]))
    		return Class.EVENT;
    	if (timeSet.contains(word_window[0]))
    		return Class.TIME;
    	if (titleSet.contains(word_window[0]))
    		return Class.PERSON;

    	for(;i<=width;i++)
    	{	String Number=window.getSentenceNumberAt(-i);
    		if (Number.equals(sentenceNumbers[0]))
    			{
    			word_window[2*width+1-i]=window.getWordAt(-i);
    			posTag[2*width+1-i]=window.getTagAt(-i);
    			
    			}
    		else {
				word_window[2*width+1-i]="&NULL";
				
				posTag[2*width+1-i]="&NULL";
			}
    		Number=window.getSentenceNumberAt(i);
    		if(Number.equals(sentenceNumbers[0]))
    			{
    				word_window[i]=window.getWordAt(i);
    				posTag[i]=window.getTagAt(i);
    			}
    		else {
				word_window[i]="&NULL";
				posTag[i]="&NULL";
			}
    	}
    	/*if(!label_value.isEmpty())
    	{
    		if(posTag[0].equals("NNP")||posTag[0].equals("JJ"))
    			for(i=2*width;i>width;i--)
    			{
    				if (posTag[i].equals("CC"))
    					return label_value.get((label_value.size()-1));
    			}
    	}*/
    	float score=0;
    	float best=1;
    	Class prefect=Class.OTHER;
    	Class[] labels={Class.ARTIFACT,Class.EVENT,Class.GEO,Class.NATURAL,Class.ORGANIZATION,Class.OTHER,Class.PERSON,Class.TIME};
    	
    	for(Class label:labels){
    		
    		score=weights[0]*f1(word_window,posTag,0,label)+weights[1]*f2(word_window,width,posTag,0,label)+weights[2]*f3(word_window,width,posTag,0,label);
    		score+=weights[3]*f4(word_window,width,posTag,0,label)+weights[4]*f5(word_window,width,posTag,0,label);
    		score+=weights[5]*f6(word_window, width, posTag, 0, label)+weights[6]*f7(word_window, width, posTag, 0, label);
    		if(score>best)
    		{
    			best=score;
    			prefect=label;
    		}
    		score=0;
    		}
/*
    	boolean zim=false;
    	for(i=0;i<width+1;i++)
    	{

    		if(posTag[i].equals("CC"))
    			zim=true;
    		if(zim&&(posTag[i].equals("NNP")||posTag[i].equals("JJ")))
    			if(!prefect.equals(Class.OTHER))
    				label_value.add(prefect);
    			
    	}
    	*/
    	return (prefect);
    }
    private static int f5(String[] word_window, int width, String[] posTag, int i, Class label) {
		// TODO Auto-generated method stub
    	int k=0;
    	boolean flag=false;
    	if(posTag[i].equals("NNP"))
    	{
    		for(k=width+1;k<2*width+1;k++)
        		{
    			if (posTag[k].equals("TO")||posTag[k].equals("WRB"))
    				if (label.equals(Class.NATURAL))
    					return 1;
    			if (posTag[k].equals("IN"))
    				flag=true;
        		}
    		for(k=0;k<width+1;k++)
    		{
    			if (flag&&word_window[k].equals("."))
    				return 1;
    		}
    	}
		return 0;
	}
    private static int f6(String[] word_window, int width, String[] posTag, int i, Class label) {
		// TODO Auto-generated method stub
    	int k=0;
    	boolean flag=false;
    	if(posTag[i].equals("NNP"))
    	{	if(word_window[i].matches("^[A-Z]*$"))
    			flag=true;
    		for(k=2*width;k>width;k--)
    		{	
    			if (posTag[k].equals("DT"))
    				flag=true;
    		}
    		for(k=0;k<width+1;k++)
        	{
        		if (vbSet.contains(posTag[k])&&label.equals(Class.ORGANIZATION))
        			if(flag)
        				return 3;
        	}
    		
    	}
		return 0;
	}
    private static int f7(String[] word_window, int width, String[] posTag, int i, Class label) {
		// TODO Auto-generated method stub
    	if(posTag[i].equals("NNP"))
    	{
    		if (posTag[2*width].equals("CD")||posTag[1+i].equals("CD"))
    			if(label.equals(Class.TIME))
    				return 1;
    		
    	}
    	if(posTag[i].equals("CD") && word_window[i].matches("^[0-9]*$"))
    	{	
    		if (posTag[2*width].equals("IN")&&(!vbSet.contains(posTag[i+1]))&&label.equals(Class.TIME))
    			return 1;
    		if (timeSet.contains(word_window[2*width])&&label.equals((Class.TIME)))
    			return 3;
    	}
    	if(posTag[i].equals("NN"))
    	{	if (timeSet.contains(word_window[2*width])&&label.equals((Class.TIME)))
    			return 3;}
		return 0;
	}
	private static int f3(String[] word_window, int width,String[] poStrings,int i,Class y) {
		// TODO Auto-generated method stub
    	int k=0;
    	if(poStrings[i].equals("NNP"))
    	{	if(word_window[i].matches(".*-.*")&&y.equals(Class.ARTIFACT))
    			return 2;
    		for(k=width+1;k<2*width+1;k++)
        	{
        		if (poStrings[k].equals("IN"))
        			return 0;
        		if(vbSet.contains(poStrings[k])&&y.equals(Class.ARTIFACT))
        			return 1;
        		if(word_window[i].equals("site")&&y.equals(Class.ARTIFACT))
        			return 1;
        	}
    		if(artifactSet.contains(word_window[2*width])||(artifactSet.contains(word_window[i+1]))&&y.equals(Class.ARTIFACT))
    				return 2;
    	}
		return 0;
	}
    private static int f4(String[] word_window, int width,String[] poStrings,int i,Class y) {
		// TODO Auto-generated method stub
    	int k=0;
    	if(poStrings[i].equals("NNP"))
    	{	
			if (poStrings[i+1].equals("POS"))
				return 0;
			if(eventSet.contains(word_window[2*width])&&(y.equals(Class.EVENT)))
				return 2;
			if(eventSet.contains(word_window[i+1])&&(y.equals(Class.EVENT)))
				return 2;
    		for(k=1;k<width+1;k++)
        	{
        		if ((poStrings[k].equals("NNPS")||(poStrings[k].equals("NNP"))))
        			if (poStrings[k+1].equals("POS"))
        				return 0;
        			else
        				if (y.equals(Class.EVENT))
        				return 1;
        	}
    		for(k=2*width;k>width;k--)
    		{
    			if ((poStrings[k].equals("NNPS")||(poStrings[k].equals("NNP"))))
        			return 1;
    		}
    		
    	}
		return 0;
	}
	public static float f1(String[] word_window, String[] poStrings,int i,Class y){
		int k=0;
		boolean flag=false,zim=false;
		if (Character.isUpperCase(word_window[i].charAt(0)) && y.equals(Class.PERSON))
			{

			for(;k<6;k++)
				{
					if (vbSet.contains(poStrings[k]))
						flag=true;
				}
			for(k=10;k>5;k--)
			{
				if(poStrings[k].equals("POS"))
					return 0;
			}
			if (word_window[i].matches("^[A-Z]*$"))
				return (float) 0.5;
			if (flag)
				if(poStrings[10].equals("&NULL"))
					return (float) 0.5;
				else
					return 1;
		}
		
		if(poStrings[i].equals("NNP")&&y.equals(Class.PERSON))
			{
				for(k=10;k>5;k--)
			
				{	
					if(!poStrings[k].equals("NNP"))
						break;
					if(titleSet.contains(word_window[k]))
						return 3;
				}
				if (titleSet.contains(word_window[i+1]))
					return 2;
			}
		if(word_window[i].endsWith("ist")||word_window[i].endsWith("man"))
			if(y.equals(Class.PERSON))
				return 1;
    	return 0;
    }
	
    public static int f2(String[] word_window, int width,String[] poStrings,int i,Class y)
    {	int k=0;
    	if (i==width+1)
    		return 0;
    	if (poStrings[i].equals("NNP")||poStrings[i].equals("NNPS"))
    		{	
    			for(k=2*width;k>width;k--)
    			{	
    				if (poStrings[k].equals("',"))
    					return 0;
    				if ((poStrings[k].equals("IN")||poStrings[k].equals("TO"))&&y.equals(Class.GEO))
    					return 1;
    			}
    		}
		if ((poStrings[k].equals("POS"))&&y.equals(Class.GEO))
			return 1;
    	if (poStrings[i].equals("JJ") && (word_window[i].endsWith("an"))&&y.equals(Class.GEO))
    	{
    		return 1;
    	}
    	
    	return 0;
    }
    /** Takes as arguments:
     * (1) a testing file with sentences
     * (2) optionally: a training file with labeled sentences
     * 
     *  Writes to the file result.tsv lines of the form
     *     X-WORD \t CLASS
     *  where X is a sentence number, WORD is a word, and CLASS is a class.
     */
    public static void main(String[] args) throws IOException {
        //args = new String[] { "/Users/fabian/Data/ner-test.tsv", "/Users/fabian/Data/ner-train.tsv" };
        args = new String[] { "/Users/codeur/Desktop/workspacejava/lab1/corpus/ner-test.tsv", "/Users/codeur/Desktop/workspacejava/lab1/corpus/ner-train.tsv" };

        // EXPERIMENTAL: If you wish, you can train a KNN classifier here
        // on the file args[1].
        // KNN<Nerc.Class> knn = new KNN<>(5);
        // knn.addTrainingExample(Nerc.Class.ARTIFACT, 1, 2, 3);
        try (BufferedWriter out = Files.newBufferedWriter(Paths.get("result.tsv"))) {
            try (BufferedReader in = Files.newBufferedReader(Paths.get(args[0]))) {
                String line;
                Window window = new Window(5);
                //System.out.println(window.position);
                while (null != (line = in.readLine())) {
                	window.add(line);
                    if (window.getWordAt(-window.width) == null) continue;
                    Class c = findClass(window);
                    if (c != null && c != Class.OTHER)
                        out.write(window.getSentenceNumberAt(0) + "-" + window.getWordAt(0) + "\t" + c + "\n");
                	}
                }
        }
    }
}