package lab6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Skeleton for the password cracking task
 * @author Fabian
 *
 */
public class Crack {

    /** The name used for the Web service */
    public static String name;

    /** List of accounts */
    public static List<Integer> accounts;
    public static ArrayList<String> passwords;
    /** Loads a file as a list of strings, one per line */
    public static List<String> load(File f) {
        try {
            return (java.nio.file.Files.readAllLines(f.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String random_choose(int i, String password)
    {
    	if(i==1)
        	password=password.replaceAll("o", "0");
    	if(i==2)
    		password=password.replaceAll("l", "1");
    	if(i==3)
    		password=password.replaceAll("a", "@");
    	if(i==4)
    	password=password.replaceAll("e", "3");
		return password;
    }

    /**
     * Tries to crack the accounts with the password, returns TRUE if it worked
     */
    public static boolean crack(String password) {
        if (Collections.binarySearch(accounts, password.hashCode()) >= 0) {
            try (Scanner s = new Scanner(
                    new URL("http://julienromero.com/ATHENS/submit?name=" + name + "&password=" + password)
                            .openStream())) {
                if (s.nextLine().equals("True")) {
                    accounts.remove(new Integer(password.hashCode()));
                    System.out.println("Password: " + password);
                    passwords.add(password);
                    return (true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (false);
    }

    /**
     * Takes as arguments (1) a file with account numbers and (2) your name (as
     * given on our Web page). Prints the passwords one per line (in any order).
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        // Uncomment for your convenience, comment it out again before
        // submitting
        // args=new String[]{"./src/lab4solution/accounts/elvis.txt", "elvis"};
        accounts = load(new File(args[0])).stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
        name = args[1];
        List<String> common_word=load(new File(args[2])).stream().collect(Collectors.toList());
        //List<String> citys=load(new File(args[3])).stream().collect(Collectors.toList());
        FileReader reader = new FileReader(args[3]);

        BufferedReader br = new BufferedReader(reader);
        String str = null;
        List<String> citys=new ArrayList<>();
        try {//try all cities
			while((str = br.readLine()) != null) {
				
				crack(str);
				//crack(c1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Collections.sort(accounts);
        passwords=new ArrayList<>();
        ArrayList<String> Popular_passwords=new ArrayList<String>(){{
        	add("123456");add("Password");add("12345678");add("qwerty");add("12345");add("123456789");
        	add("letmein");add("1234567");add("football");add("iloveyou");add("admin");add("welcome");
        	add("monkey");add("login");add("abc123");add("starwars");add("123123");add("dragon");
        	add("passw0rd");add("master");add("hello");add("freedom");add("qazwsx");add("trustno1");
        	
        }};/* common password
        for(String password:Popular_passwords)
        {
        	crack(password);
        }the common english word*/
        /*
        for(String password:common_word)
        {	
        	crack(password);
        }
        //The dictionary attack, randomly replacing letters with the corresponding numbers or symbols (e.g., “earlier” -> “e@rli3r”) [3 points]
        for(int i=0;i<99999;i++)
        {
        	for(String password:common_word)
        {	
        	int random_number=(int) Math.random()*5;
        	password=random_choose(random_number,password);
        	crack(password);
        }
        
        }/*Brute force with up to 10 digits
        for(int j=0;j<10;j++)
        	for(long i = 0; i < 999999999; i++)
        	{
        		
        		crack(j+""+i);
        	}
        *///Brute force with up to 5 letters
        /*
        String alpha="qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        for(int i=0;i<alpha.length();i++){
        	crack(""+alpha.charAt(i));
        	for(int i1=0;i1<alpha.length();i1++){
        		crack(alpha.charAt(i)+""+alpha.charAt(i1));
        		for(int i2=0;i2<alpha.length();i2++){
        			crack(alpha.charAt(i)+""+alpha.charAt(i1)+""+alpha.charAt(i2));
        			for(int i3=0;i3<alpha.length();i3++){
        				crack(alpha.charAt(i)+""+alpha.charAt(i1)+""+alpha.charAt(i2)+""+alpha.charAt(i3));
        				for(int i4=0;i4<alpha.length();i4++){
        					crack(alpha.charAt(i)+""+alpha.charAt(i1)+""+alpha.charAt(i2)+""+alpha.charAt(i3)+""+alpha.charAt(i4));
        	        	}
                	}
            	}
        	}
        }
        */
        //	*/
        /*
        //Diceware passwords built from the 10000 most common English words (in lowercase with hyphens in between) up to length 2
        for(String pw1:common_word)
        {
        	for(String pw2:common_word)
        		crack(pw1+"-"+pw2);
        }
        */
        //try all city

        // Smartness goes here
        
    }

}