

import java.util.*;
import java.io.BufferedInputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;


/* Slope One version
 * ****Simple slope one*****
 * Here is presented only the calculation of the diffs matrix (the most expensive part)
 * the prediction part is in another file
 */

public class SlopeOne {

    int maxItemsId = 0;
    Map<Integer,ArrayList<String>> allInfoUsers = new HashMap<Integer, ArrayList<String>>();
    Map<Integer,ArrayList<String>> allInfoMovies = new HashMap<Integer, ArrayList<String>>();
    float mteste[][];
    int mFreq[][];
    int targetUser = 2;
    static String input;
    Map<Integer,Map<Integer,Float>> usersMatrix;

    public static void main(String args[]){
    	
    	Scanner in = new Scanner(System.in);
    	System.out.println("Sem Influencia-0\nInfluencia Sexo-1\nInfluencia Ano de Filme-2\nInfluencia Idade e Ano Filme-3\nInfluencia Idade-4");
    	input = in.nextLine();
    	
    	 System.out.println("\nStarting Slope One");
    	 long start = System.currentTimeMillis();
         SlopeOne slopeOne = new SlopeOne();
         /* Estimates time */
         long end = System.currentTimeMillis();
         System.out.println("\nExecution time was "+(end-start)+" ms.");
        
    }


    public SlopeOne(){
    	
    	getAllUserInfo(targetUser);
        getAllMovieInfo();
    	readInput();
        buildDiffMatrix();


        /* Print the output */
        try{
            FileOutputStream output = new FileOutputStream("slope-intermidiary-output.txt");
            /* Print the maximum number of items */
            output.write(String.valueOf(maxItemsId).getBytes());
            output.write( String.valueOf("\n").getBytes()  );

            for(int i = 1; i <= maxItemsId; i++){
                for(int j = i; j <= maxItemsId; j++){

                    if(!Float.isNaN (mteste[i][j])){
                        /* Print the rates */
                        output.write( String.valueOf(i).getBytes()  );
                        output.write( String.valueOf("\t").getBytes()  );
                        output.write( String.valueOf(j).getBytes()  );
                        output.write( String.valueOf("\t").getBytes()  );
                        output.write( String.valueOf( mteste[i][j] ).getBytes());
                        output.write( String.valueOf("\n").getBytes()  );

                        /* Print the frequencies */
                        output.write( String.valueOf(i).getBytes()  );
                        output.write( String.valueOf("\t").getBytes()  );
                        output.write( String.valueOf(j).getBytes()  );
                        output.write( String.valueOf("\t").getBytes()  );
                        output.write( String.valueOf( mFreq[i][j] ).getBytes());
                        output.write( String.valueOf("\n").getBytes()  );
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Function readInput()
     * Read the input and saves it in the usersMatrix
     *
     */
    public void readInput(){
        File file = new File("u1.base");

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        int i;
        int j;

        usersMatrix = new HashMap<Integer,Map<Integer,Float>>();
        String line;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            line = dis.readLine();

            /*
             * First get all ratings from one user, 
             * calculate the diffs for this user and save them 
             * in the mDiffMatrix
             */

            while (dis.available() != 0) {
            	line = dis.readLine();
            	String lineWithCommas = line.replace("\t",",");
                String[]t = lineWithCommas.split(","); 
                int user =  Integer.parseInt(t[0]);
                int tempUser = user;
                float influencia=1f;//influenciaSexo(tempUser);
                float influencia1=1f;//influenciaSexo(tempUser);
                float influencia2=1f;//influenciaSexo(tempUser);
                
                
               
                usersMatrix.put(user, new HashMap<Integer,Float>());
	
	                // Read all lines for one user
	                while(user == tempUser){
	                	
	                	/* Get item */
	                    i = Integer.parseInt(t[1]);

	                    if(input.equals("1"))
	                    {
	                    	influencia = influenciaSexo(tempUser);
	                    }
	                    else if(input.equals("2"))
	                    {
	                    	influencia = influenciaAnoFilme(t[1]);
	                    }
	                    else if(input.equals("3"))
	                    {
	                    	influencia = influenciaAnoFilmePessoaIdade(t[1],t[0]);
	                    }
	                    else if(input.equals("4"))
	                    {
	                    	influencia = influenciaPessoa(t[0]);
	                    }
	                    	
                    	if(input.equals("0")){
	                    	
	                    	influencia=1;
	                    }
	                	

	                    /* Get the quantity of items by finding the maximun value
	                    * of itemId */
	                    maxItemsId = maxItemsId < i ? i : maxItemsId;
	
	                    /* Save rating */
	                    usersMatrix.get(user).put(i, (float) Float.parseFloat(t[2])*influencia);
	
	                    if(dis.available() != 0) {
	                        line = dis.readLine();
	                        String lineWithCommas2 = line.replace("\t",",");
	                        t = lineWithCommas2.split(",");
	                        tempUser = Integer.parseInt(t[0]);
	                    }else{
	                        tempUser = -1;
	                    }
	
                	}
   
            }

            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*
     * Function buildDiffMatrix()
     * Calculates the DiffMatrix for all items
     *
     */
    public  void buildDiffMatrix() {

        mteste = new float[maxItemsId+1][maxItemsId+1];
        mFreq = new int[maxItemsId+1][maxItemsId+1];

        for(int i = 1; i <= maxItemsId; i++)
            for(int j = 1; j <= maxItemsId; j++){
                mteste[i][j] = 0;
                mFreq[i][j] = 0;
            }

        /* Iterate through all users, and then, through all items do calculate the diffs */
        for(int cUser : usersMatrix.keySet()){
            for(int i: usersMatrix.get(cUser).keySet()){
                for(int j : usersMatrix.get(cUser).keySet() ){
                    mteste[i][j] = mteste[i][j]  + 
                                   ( usersMatrix.get(cUser).get(i).floatValue() - (usersMatrix.get(cUser).get(j).floatValue()));
                    mFreq[i][j] = mFreq[i][j] + 1; //Numero de vezes/filmes que o user votou
                }
            }
        }

        /*  Calculate the averages (diff/freqs) */
        for(int i = 1; i<= maxItemsId; i++){
            for(int j = i; j <= maxItemsId; j++){
                if(mFreq[i][j] > 0 && mteste[i][i] > 0){
                    mteste[i][j] = mteste[i][j] / mFreq[i][j];
                }
            }
        }
    }
    
    
    /*
     * Function getUsersGender()
     * Get already known user preferences
     *
     */
    public  void getAllUserInfo( int userID ){

        File fuser = new File("u.user");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        System.out.println("\n" + "#### All Users x Gender#### ");
        try {
            fis = new FileInputStream(fuser);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            while(dis.available() != 0)
            {
                String line = dis.readLine();
                String lineWithCommas = line.replace("|",",");
                String[]t = lineWithCommas.split(",");
                ArrayList<String> personInfo= new ArrayList<>();
                personInfo.add(0, t[0]);
                personInfo.add(1, t[1]);
                personInfo.add(2, t[2]);
                personInfo.add(3, t[3]);
            	allInfoUsers.put(Integer.parseInt(t[0].toString()),personInfo);
            }   
            
            	String specificUserGender = null;

            	/*if(allInfoUsers.containsKey(userID))
            	{
            		specificUserGender = allInfoUsers.get(userID).get(2);
            		
            		System.out.println("\n" + "####USERID "+userID+" É DO SEXO "+specificUserGender+"#### ");
            	}*/
            	
            	ArrayList<Integer>  tempArray = new ArrayList<Integer>();
            	
            	for (int j : allInfoUsers.keySet()) {
            		
            		if(!allInfoUsers.get(j).get(2).equals(specificUserGender))
            		{
            			tempArray.add(j);
            		}
            	}
            	
            
            

            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Function getUsersGender()
     * Get already known user preferences
     *
     */
    public  void getAllMovieInfo()
    {

        File fuser = new File("u.item");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        System.out.println("\n" + "#### Movies x Info #### ");
        try {
            fis = new FileInputStream(fuser);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            while(dis.available() != 0)
            {
                String line = dis.readLine();
                String lineWithCommas = line.replace("|",",");
                String[]t = lineWithCommas.split(",");
                ArrayList<String> infoMovie = new ArrayList<>();
                infoMovie.add(0, t[0]); //id
                infoMovie.add(1, t[1]); //nome
                infoMovie.add(2, t[2]); //data lançamento
                infoMovie.add(3, t[4]); //link filme
            	allInfoMovies.put(Integer.parseInt(t[0].toString()),infoMovie);
            }   

            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> getSpecificMovieInfo(String movieId)
    {
    	if(allInfoMovies.containsKey(Integer.parseInt(movieId)))
    		return allInfoMovies.get(Integer.parseInt(movieId));
		return null;
    }
    
    public ArrayList<String> getSpecificPersonInfo(String personId)
    {
    	if(allInfoUsers.containsKey(Integer.parseInt(personId)))
    		return allInfoUsers.get(Integer.parseInt(personId));
		return null;
    }
    
    
    
    public float influenciaSexo(int tempUser)
    {
    	 if(allInfoUsers.containsKey(tempUser))
         {
         	return 0.8f;
         }else{
         	return 0.2f;
         }
    }
    
    public float influenciaAnoFilme(String anoFilme)
    {
    	float influencia = 0;
    	ArrayList<String> movieInfo = getSpecificMovieInfo(anoFilme);
    	if(movieInfo.size() == 4 && movieInfo.get(2).split("-").length == 3)
    	{
        if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1940 && 
    	Integer.parseInt(movieInfo.get(2).split("-")[2])<1970)
        {
        	influencia = 0.2f;
        	
        }else if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1971 && 
            	Integer.parseInt(movieInfo.get(2).split("-")[2])<1990)
        {
        	influencia = 0.9f;
        	
        }else {
        	
        	influencia = 1.4f;
        	
        }
    	}
		return influencia;
    }
    
    public float influenciaAnoFilmePessoaIdade(String anoFilme, String pessoaIdade)
    {
    	float influencia = 0;
    	ArrayList<String> movieInfo = getSpecificMovieInfo(anoFilme);
    	ArrayList<String> personInfo = getSpecificPersonInfo(pessoaIdade);
    	if(movieInfo.size() == 4 && movieInfo.get(2).split("-").length == 3 && personInfo.size() == 4)
    	{
	        if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1940 && 
	    	Integer.parseInt(movieInfo.get(2).split("-")[2])<1953 && 
	    	Integer.parseInt(personInfo.get(1))>60)
	        {
	        	influencia = 0.76f;
	        	
	        }else if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1953 && 
	            	Integer.parseInt(movieInfo.get(2).split("-")[2])<1973 && 
	            	Integer.parseInt(personInfo.get(1))>50 &&
	            	Integer.parseInt(personInfo.get(1))<60)
	        {
	        	influencia = 0.66f;
	        	
	        }else if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1973 && 
	            	Integer.parseInt(movieInfo.get(2).split("-")[2])<1987 && 
	            	Integer.parseInt(personInfo.get(1))>40 &&
	            	Integer.parseInt(personInfo.get(1))<50)
	        {
	        	
	        	influencia = 0.54f;
	        	
	        }else if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1987 && 
	            	Integer.parseInt(movieInfo.get(2).split("-")[2])<1993 && 
	            	Integer.parseInt(personInfo.get(1))>30 &&
	            	Integer.parseInt(personInfo.get(1))<40)
	        {
	        	influencia = 0.46f;
	        	
	        }else if(Integer.parseInt(movieInfo.get(2).split("-")[2])>=1993 && 
	            	Integer.parseInt(movieInfo.get(2).split("-")[2])<1998 && 
	            	Integer.parseInt(personInfo.get(1))>10 &&
	            	Integer.parseInt(personInfo.get(1))<20)
	        {
	        	influencia = 0.7f;
	        	
	        }else{
	        	
	        	influencia = 0.1f;
	        }
    	}
		return influencia;
    }
    
    public float influenciaPessoa(String pessoa)
    {
    	float influencia = 0;
    	ArrayList<String> personInfo = getSpecificPersonInfo(pessoa);
    	if(personInfo.size() == 4)
    	{
        if(Integer.parseInt(personInfo.get(1))>=0 && 
    	Integer.parseInt(personInfo.get(1))<10)
        {
        	influencia = 0.1f;
        	
        }else if(Integer.parseInt(personInfo.get(1))>=11 && 
            	Integer.parseInt(personInfo.get(1))<20)
        {
        	influencia = 0.3f;
        	
        }else if(Integer.parseInt(personInfo.get(1))>=21 && 
            	Integer.parseInt(personInfo.get(1))<40){
        	
        	influencia = 0.6f;
        	
        }else if(Integer.parseInt(personInfo.get(1))>=40 && 
            	Integer.parseInt(personInfo.get(1))<50){
        	
        	influencia = 0.5f;
        	
        }else if(Integer.parseInt(personInfo.get(1))>=51 && 
            	Integer.parseInt(personInfo.get(1))<60){
        	
        	influencia = 0.4f;
        	
        }else{
        	influencia = 0.3f;
        }
    	}
		return influencia;
    }

}