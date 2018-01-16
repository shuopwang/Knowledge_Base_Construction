package lab5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Skeleton class for a program that maps the entities from one KB to the
 * entities of another KB.
 * 
 * @author Fabian
 *
 */
public class EntityMapper {

    /**
     * Takes as input (1) one knowledge base (2) another knowledge base.
     * 
     * Prints "entity1 TAB entity2 NEWLINE" to the file results.tsv, if the first
     * entity from the first knowledge base is the same as the second entity
     * from the second knowledge base. Output 0 or 1 line per entity1.
     */
    public static void main(String[] args) throws IOException {
        KnowledgeBase kb1 = new KnowledgeBase(new File(args[0]));
        KnowledgeBase kb2 = new KnowledgeBase(new File(args[1]));
        try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
            for (String entity1 : kb1.facts.keySet()) {
                String mostLikelyCandidate = null;
                // Something smart here
                Map<String, Set<String>> relations=kb1.facts.get(entity1);
                HashMap<String, Double> entity_score=new HashMap<String,Double>();
                //HashMap<String, Integer> entity_count=new HashMap<String,Integer>();
                double score=0;
                //int cnt=0;
                for(String entity2:kb2.facts.keySet())
                {	//System.out.println(entity2);
                	if (!entity_score.containsKey(entity2))
                		{//System.out.print("works");
                		score=0;}
                		//cnt=0;}
                	else{
                		//System.out.println("test");
                		score=entity_score.get(entity2);
                    	//cnt=entity_count.get(entity2);
                	}
                	Map<String, Set<String>> entity2_relations=kb2.facts.get(entity2);
                	for(String relation:relations.keySet())
                	{	Set<String> V1=relations.get(relation);
                		for(String relation2:entity2_relations.keySet())
                		{
                			//if(relation.equals(relation2))
                				//score+=1;
                			Set<String> V2=entity2_relations.get(relation2);
                			for(String value:V1)
                			{
                				for(String value2:V2)
                				{
                					if(value.equals(value2))
                						score+=2;
                				}
                			}
                		}
                		score=score/((double) V1.size());
                	}
                	score=score/(double)relations.size();
                	entity_score.put(entity2, score);
                }
                mostLikelyCandidate=Collections.max(entity_score.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
                double max_score=entity_score.get(mostLikelyCandidate);
                /*for(String key:entity_score.keySet())
                {
                	if (key.equals(mostLikelyCandidate))
                		continue;
                	if (entity_score.get(key)==max_score)
                	{
                		if(kb2.facts.get(key).size()<kb2.facts.get(mostLikelyCandidate).size())
                		{
                			mostLikelyCandidate=key;
                		}
                	}
                }*/
                if (mostLikelyCandidate != null) {
                    out.write(entity1 + "\t" + mostLikelyCandidate + "\n");
                }
               
            }
        }
    }
}