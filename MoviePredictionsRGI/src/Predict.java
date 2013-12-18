

import java.util.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;


public class Predict{

    Map<Integer,Float> user = new HashMap<Integer, Float>();
    Map<Integer,String> allUsersGender = new HashMap<Integer, String>();
    HashMap<Integer,Float> predictions = new HashMap<Integer,Float>();
    /* User to whom the prediction will be made */
    int targetUser = 1;
    float mDiff[][];
    float mFreq[][];
    /* Contains the maximun number of items */
    int maxItem;

    public static void main(String args []){
    	
    	 System.out.println("\nStarting Prediction");
    	 
        long start = System.currentTimeMillis();
        Predict newPrediction = new Predict();
        /* Estimates time */
        long end = System.currentTimeMillis();
        System.out.println("\nExecution time was "+(end-start)+" ms.");
    }

    public Predict(){

        getUser(targetUser);
        readDiffs();
        float totalFreq[] =  new float [maxItem+1];

        /* Start prediction */
        for (int j=1; j <= maxItem; j++) {
            predictions.put(j,0.0f);
            totalFreq[j] = 0;
        }

        for (int j : user.keySet()) {
            for (int k = 1; k <= maxItem; k++) {
                if( j != k) {
                    /* Only for items the user has not seen */
                    if(!user.containsKey(k)){
                        float newVal = 0;
                        if(k < j) {
                            newVal = (float) (mFreq[j][k] * (mDiff[j][k] + user.get(j).floatValue()));
                        }
                        else {
                            newVal = (float) (mFreq[j][k] * (-1 * mDiff[j][k] + user.get(j).floatValue()));
                        }
                        totalFreq[k] = totalFreq[k] + mFreq[j][k];
                        predictions.put(k, predictions.get(k).floatValue() + newVal);
                    }
                } 
            }
        }

        /* Calculate the average */
        for (int j : predictions.keySet()) {
            predictions.put(j, (float) Math.sqrt(predictions.get(j).floatValue()/(totalFreq[j])));
        }

        /* Fill the predictions vector with the already known rating values */
        for (int j : user.keySet()) {
        		predictions.put(j, user.get(j));  		
        }

        /* Print predictions */
        System.out.println("\n" + "#### Predictions #### ");
        for (int j : predictions.keySet()) {
        	if(Float.isNaN (predictions.get(j).floatValue()))
        	{
        		//System.out.println( j + " " +0);
        	}else if(predictions.get(j).floatValue() < 0 && !user.containsKey(j)){
        		//float dobrarNegativo = predictions.get(j).floatValue() * predictions.get(j).floatValue();
        		//System.out.println( j + " " +predictions.get(j).floatValue());
        		
        	}else if(predictions.get(j).floatValue() == 0.0f && !user.containsKey(j)){
        			//NAO IMPRIME
        	}else if(!user.containsKey(j)){
        		
        			System.out.println( j + " " +(int)predictions.get(j).floatValue());

        	}
        }
    }

    /*
     * Function readDiff()
     * Read the precalculated Diffs between items
     *
     */
    public void readDiffs(){

        File foutput = new File("slope-intermidiary-output.txt");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
            fis = new FileInputStream(foutput);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            String line = dis.readLine();
            StringTokenizer t = new StringTokenizer(line, "\t");
            maxItem = Integer.parseInt(t.nextToken());
            mDiff = new float[maxItem + 1][maxItem + 1];
            mFreq = new float[maxItem + 1][maxItem + 1];

            for(int i = 1; i <= maxItem; i++)
              for(int j = 1; j <= maxItem; j++){
                mDiff[i][j] = 0;
                mFreq[i][j] = 0;
              }

            System.out.println("\n" + "#### Diffs #### ");

            while(dis.available() != 0){

                line = dis.readLine();
                t = new StringTokenizer(line, "\t");
                int itemID1 = Integer.parseInt(t.nextToken());
                int itemID2 = Integer.parseInt(t.nextToken());

                mDiff[itemID1][itemID2] = Float.parseFloat(t.nextToken());

                line = dis.readLine();
                t = new StringTokenizer(line, "\t");
                itemID1 = Integer.parseInt(t.nextToken());
                itemID2 = Integer.parseInt(t.nextToken());

                mFreq[itemID1][itemID2] = Float.parseFloat(t.nextToken());
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
     * Function getUser()
     * Get already known user preferences
     *
     */
    public  void getUser( int userID ){

        File fuser = new File("u2.base");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        System.out.println("\n" + "#### Initial User x Item x Rating #### ");
        try {
            fis = new FileInputStream(fuser);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            while(dis.available() != 0) {
                String line = dis.readLine();
                String lineWithCommas = line.replace("\t",",");
                String[]t = lineWithCommas.split(",");
                int tempUser = Integer.parseInt(t[0]);

                while(tempUser == userID){
                	
                    user.put(Integer.parseInt(t[1]),Float.parseFloat(t[2]));
                    //line = dis.readLine();
                    //String lineWithCommas2 = line.replace("\t",",");
                    //t = lineWithCommas2.split(",");
                    //tempUser = Integer.parseInt(t[0]);
                    
                    if(dis.available() != 0) {
                        line = dis.readLine();
                        String lineWithCommas3 = line.replace("\t",",");
                        t = lineWithCommas3.split(",");
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
     * Function getUsersGender()
     * Get already known user preferences
     *
     */
    public  void getUsersGender( int userID ){

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
                String lineWithCommas = line.replace("\t",",");
                String[]t = lineWithCommas.split(",");
            	allUsersGender.put(Integer.parseInt(t[0]), t[2]);
            }   
            
            	String specificUserGender = null;

            	if(allUsersGender.containsKey(userID))
            	{
            		specificUserGender = allUsersGender.get(userID);
            	}
            	
            	for (int j : allUsersGender.keySet()) {
            		
            		if(!allUsersGender.get(j).equals(specificUserGender))
            		{
            			allUsersGender.remove(j);
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

}