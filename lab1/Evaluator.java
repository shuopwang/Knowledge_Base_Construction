package lab1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton for an evaluator
 */
public class Evaluator {

	/**
	 * Takes as arguments (1) the gold standard and (2) the output of a program.
	 * Prints to the screen one line with the precision
	 * and one line with the recall.
	 * 
	 * get the output label
	 * G.s standand
	 * computer prec and recall
	 */
	public static void main(String[] args) throws Exception {
        File results = new File(args[0]);
        File goldstandard = new File(args[1]);
        
        Map<String,String> gold_s=new HashMap<>();
        @SuppressWarnings("resource")
		BufferedReader g=new BufferedReader(new FileReader(goldstandard));
        String line;
        int length=0;
        while((line=g.readLine())!=null)
        {
        	String[] tandt=line.trim().split("\t");
        	String title=tandt[0];
        	String truth=tandt[1];
        	gold_s.put(title, truth);
        	length+=1;
        }
        @SuppressWarnings("resource")
		BufferedReader r=new BufferedReader(new FileReader(results));
        int right=0;
        int size=0;
        while((line=r.readLine())!=null)
        {
        	String[] tandt=line.trim().split("\t");
        	String title=tandt[0];
        	String truth=tandt[1];
        	if(!gold_s.containsKey(title))
        		continue;
        	if (gold_s.get(title).equals(truth))
        	{
        		right+=1;
        	}
        	if(truth!=null)
        		size+=1;
        }
        System.out.println("prec:"+right/(float)size);
        System.out.println("recall:"+right/(float)length);
	}
}