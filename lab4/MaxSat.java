package lab4;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.omg.CORBA.SystemException;

/**
 * Takes as argument a Max-Sat-file or a folder of Max-Sat-files, 
 * writes a KB to the corresponding output file(s).
 * Does not take longer than 5 minutes.
 */
public class MaxSat {

    /** Start time*/
	 public static long startTime;

    /** TRUE if we have to stop*/
	    public static boolean haveToStop() {
	        return (System.currentTimeMillis() - startTime > 5 * 60 * 1000);
	    }


    public static void main(String[] args) throws IOException {
        File argument = new File(args[0]);
        for (File file : argument.isDirectory() ? argument.listFiles() : new File[] { argument }) {
            startTime = System.currentTimeMillis();
            List<Clause> rules = Clause.readFrom(file);
            Set<Atom> bestKB = new HashSet<>();            
            // magic goes here  
            HashMap<String, Double> word_weight=new HashMap<>();
            
            for(Clause rule:rules)
            {	
				List<Atom> atoms=rule.atoms;
				for(Atom atom:atoms)
				{	
					Atom predicate=atom;

						if(!word_weight.containsKey(predicate.predicate))
						{
							
							if(predicate.isPositive)
								word_weight.put(predicate.predicate, rule.weight/rule.atoms.size());
							else
							{
								word_weight.put(predicate.predicate, -rule.weight/rule.atoms.size());
							}
						}
					
						else {
							if(predicate.isPositive)
								word_weight.put(predicate.predicate, word_weight.get(predicate.predicate)+rule.weight/rule.atoms.size());
							else
								word_weight.put(predicate.predicate, word_weight.get(predicate.predicate)-rule.weight/rule.atoms.size());
						}
					
				}
            }
            double min=999999;
            String minPredict=new String();
            ArrayList<String> tmp=new ArrayList<>();
            for(String key:word_weight.keySet())
            {
            	if(word_weight.get(key)<0)
            		{
            			tmp.add(key);
            			if(Math.abs(word_weight.get(key))<min)
            			{
            				min=Math.abs(word_weight.get(key));minPredict=key;
            			}
            		}
            	//System.out.print(key+"  "+word_weight.get(key)+"    ");
            }
            System.out.println();
            if(word_weight.size()!=tmp.size())
            {	            
            	/*if(word_weight.size()<2)
            	{	
            		bestKB=exhaustive_search(bestKB,rules,word_weight);
            	}*/
            if(word_weight.size()>0)
            	{	
            	bestKB=unit_propagation(bestKB,rules,word_weight);
            	}
            }
            else
            {	
            	Atom local=new Atom(minPredict,false);
            	bestKB.add(local);
            	word_weight.remove(minPredict);
                ArrayList<String> deleteV=new ArrayList<>();
                deleteV.add(minPredict);
            //	System.out.println(bestKB);
            	for(Clause rule:rules)
   		     {	
   				if(rule.atoms.isEmpty())
   		            continue;
   		        //System.out.println(rule.atoms);
   				List<Atom> atoms=rule.atoms;
   				for(Atom atom:atoms)
   				{	
   					Atom predicate=atom;
   					if (deleteV.contains(predicate.predicate))
   						break;
   					if(predicate.isPositive)
   						word_weight.put(predicate.predicate, word_weight.get(predicate.predicate)+rule.weight/rule.atoms.size());
   					else
   						word_weight.put(predicate.predicate, word_weight.get(predicate.predicate)-rule.weight/rule.atoms.size());

   					}
   		          			
   		     }	            	
            	bestKB=unit_propagation(bestKB, rules, word_weight);
            }
            //System.out.println(bestKB);
            try (Writer out = Files.newBufferedWriter(Paths.get(file.getName().replaceAll("\\.[a-z]+$", ".res")),
                    Charset.forName("UTF-8"))) {
            	for (Atom var : bestKB)
                    if (var!=null) 
                    	if(var.isPositive)
                    		out.write(var + "\n");
            }
        }
    }


	private static Set<Atom> unit_propagation(Set<Atom> bestKB, List<Clause> rules,HashMap<String, Double> word_weight) {
		// TODO Auto-generated method stub
		double max=0;
		Set<Atom> sKB=bestKB;
		boolean flag=true;
		Atom tmp=null;
		Set<Atom> KB=new HashSet();
		String best_predict=new String();
		HashMap<String, Double> tempHash=new HashMap<>();
		ArrayList<String> deleteV=new ArrayList<>();

		while(flag)
		{	
			KB.clear();
			KB.addAll(sKB);
			max=0;
			best_predict="NONE";
			for(String key:word_weight.keySet())
			{	//System.out.print(key+" "+word_weight.get(key)+"    ");
				if(word_weight.get(key)<0)
					continue;
				if(word_weight.get(key)>=max)
				{
					best_predict=key;
					max=word_weight.get(key);
				}
				word_weight.put(key, (double) 0);
			}
			//System.out.println();
			if(best_predict=="NONE")
				{flag=false;break;}
			word_weight.remove(best_predict);deleteV.add(best_predict);
			tmp=new Atom(best_predict, true);
			
			sKB.add(tmp.asPositive());
			//System.out.println(sKB);
			
			
			rules=clean(tmp, rules);
			
			
			for(Clause rule:rules)
		     {	
				if(rule.atoms.isEmpty())
		            continue;
		        //System.out.println(rule.atoms);
				List<Atom> atoms=rule.atoms;
				for(Atom atom:atoms)
				{	
					Atom predicate=atom;
					if (deleteV.contains(predicate.predicate))
						break;
					if(predicate.isPositive)
						word_weight.put(predicate.predicate, word_weight.get(predicate.predicate)+rule.weight/rule.atoms.size());
					else
						word_weight.put(predicate.predicate, word_weight.get(predicate.predicate)-rule.weight/rule.atoms.size());

					}
		          			
		     }
			if(KB.equals(sKB))
				flag=false;
		}
		//System.out.println(sKB);
		return sKB;
	}
	
	public static  List<Clause> clean(Atom thing,List<Clause> rules)
	{	Atom second=thing;
		second.isPositive=false;
		Iterator<Clause> it = rules.iterator();	
		int i=0;
		while(it.hasNext())
		{	
			Clause rule=it.next();
			i=rules.indexOf(rule);
			if(rule.atoms.contains(thing))
				{	//int index=rules.indexOf(rule);
					rules.get(i).atoms.remove(thing);
				}
			if(rule.atoms.contains(second))
			{
				rules.get(i).atoms.remove(second);
			}
		}
		return rules;
	}
/*
	private static  Set<Atom> exhaustive_search(Set<Atom> bestKB, List<Clause> rules, HashMap<String, Double> word_weight) {
		// TODO Auto-generated method stub
		double max=0;
		Set<Atom> KB=new HashSet<>();
		int i=0;
		double weight=0;
		Atom tmp;
		max=getweight(rules, KB);
   		for(i=0;i<predicates.size();i++)
		{	tmp=predicates.get(i);
			KB.add(tmp);
			weight=getweight(rules,KB);
			if(weight>max)
			{
				max=weight;
				bestKB=KB;
			}
			KB.remove(tmp);
		}
   		for(i=0;i<predicates.size();i++)
   		{	Atom t=predicates.get(i);
			KB.add(t);
   			for(int j=i+1;j<predicates.size()-1;j++)
   			{
   				tmp=predicates.get(i);
   				KB.add(tmp);
   				weight=getweight(rules,KB);
   				if(weight>max)
   				{
   					max=weight;
   					bestKB=KB;
   				}
   				KB.remove(tmp);
   			}
   			KB.remove(t);
   		}
   		return bestKB;
	}

	public static double getweight(List<Clause> rules,Set<Atom> KB)
	{	double weight = 0;
		for(Clause rule:rules)
		{                 
			if (rule.isSatisfiedIn(KB)) {
				weight += rule.weight;
			}
		}
		return weight;
	}
	*/
}