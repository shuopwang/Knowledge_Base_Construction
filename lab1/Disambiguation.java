package lab1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Skeleton class to perform disambiguation
 * 
 * @author Jonathan Lajus
 *
 */
public class Disambiguation {

    /**
     * This program takes 3 command line arguments, namely the paths to: 
     * - yagoLinks.tsv 
     * - yagoLabels.tsv 
     * - wikipedia-ambiguous.txt 
     * reverselabels(page.label)
     * links(each-specify thing)
     * labels(specify thing's feature)
     * check the feature in the content
     * in this order.
     * 
     * The program prints statements of the following form into the file
     * results.tsv: 
     *    <pageTitle> TAB <yagoEntity> NEWLINE 
     * It is OK to skip articles.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: Disambiguation <yagoLinks> <yagoLabels> <wikiText>");
            return;
        }
        File dblinks = new File(args[0]);
        File dblabels = new File(args[1]);
        File wiki = new File(args[2]);
   
        SimpleDatabase db = new SimpleDatabase(dblinks, dblabels);
        try (Parser parser = new Parser(wiki)) {
            try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
                while (parser.hasNext()) {
                    Page nextPage = parser.next();
                    String pageTitle = nextPage.title; // "Clinton_1"
                    String pageContent = nextPage.content; // "Hillary Clinton was..."}
                    String pageLabel = nextPage.label(); // "Clinton"
                    String correspondingYagoEntity="null";
                    	correspondingYagoEntity = disambiguation(db,pageLabel,pageContent);

                   out.write(pageTitle + "\t" + correspondingYagoEntity + "\n");
                }
            }
        }
    }

	private static String disambiguation(SimpleDatabase db, String pageLabel, String pageContent) {
		if(db.reverseLabels.containsKey(pageLabel))
			{	//System.out.println("pageLabel: "+pageLabel);
				Set<String> Entites=db.reverseLabels.get(pageLabel);
            	Set<String> set = new HashSet<String>();
            	pageContent=pageContent.toLowerCase();
            	pageContent=pageContent.replaceAll("[^\\w\\s]", " ");
            	String[] words=pageContent.split(" ");
            	String top=null;
            	float max=0;
            	for (String word:words)
            	{
            		set.add(word);
            	}
            	float score=0;
            	//System.out.println("set: "+set);
				//get some entities according to the page Label
				for(String Entity:Entites)
				{	//System.out.println(Entity);
					//for each entity find the entities correspond the entity
					Set<String> entites=db.links.get(Entity);
					if (entites==null)
						return null;
					float k=0;
					//System.out.println("entites: "+entites);
					for(String entity:entites)
					{	
						//System.out.println("entity: "+entity);
						Set<String> labels = db.labels.get(entity);
						
						if(labels!=null)
						{	
							//System.out.println("Labels: "+labels);
							for (String l:labels)
							{	
								l=l.toLowerCase();
								l=l.replaceAll("[^\\w\\s]", " ");
								Set<String> sublabel = new HashSet<String>(Arrays.asList(l.split(" ")));
								float size=sublabel.size();
								sublabel.retainAll(set);
								if (sublabel.size()>0)
								{
									k+=(sublabel.size()/size);
									break;
								}
							}
							
						}
					}
					//System.out.println("k : "+k);
					score=k/entites.size();
					if (score>max)
						{	
							max=score;
							//System.out.println("test");
							top=Entity;
						}
				}
				//System.out.println(top);
				return top;
			}
		return null;
	}
    
}