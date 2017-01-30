package bdmp.project;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.util.Pair;




public class App 
{
    public static void main( String[] args ) throws IOException
    {
    		final int K = 3;
    		final int MINVALUE = 0;
    		final int MAXVALUE = 100;
    		final int POINTS = 10000;
    		
    		int dimension = 2;
    		Scanner scan1 = new Scanner(System.in);
    		boolean dimensionCycle = true;
    		Utilities.createFolders(); // Initialize input and output folders
    		
    		while(dimensionCycle){
    			System.out.println("Choose the dimension of the points: ");
    			try {
    				dimension = scan1.nextInt();
    			} catch(InputMismatchException e){
    				System.err.println("Dimension must be an integer.");
    				System.exit(1);
    			}
    			if (dimension >= 2 && dimension <= 100){
    				dimensionCycle = false;
    			} else {
    				System.err.println("Dimension must be an integer greater or equal to 2.");
    			}
    		}
    		
    		Sampler sampler = new Sampler(dimension);	
    		String samplingChoice;
    		String algorithmChoice;
    		boolean samplingCycle = true;
    		boolean algorihtmCycle = true;
    		Scanner scan = new Scanner(System.in);
    		while (samplingCycle){
    			System.out.println("[Dimension: "+dimension + "] Choose a sampling algorithm: \n \"s\" for simple sampling. "
    					+ "\n \"r\" for random sampling. \n \"p\" for Poisson sampling.");
    			samplingChoice = scan.nextLine().toLowerCase();
    			switch (samplingChoice){
    			
    			case "s" : sampler.simpleSample(POINTS, MINVALUE, MAXVALUE, true); 
    			/*algorihtmCycle = true;
    			while (algorihtmCycle){
    				System.out.println("[Dimension: "+dimension + "][Simple sampling] Choose how to manage uncertainty: "
    						+ "\n \"e\" for expected points algorithm. \n \"m\" for most probable points algorithm." );
    				algorithmChoice = scan.nextLine().toLowerCase();
    				if (algorithmChoice.equals("e")){
    					Utilities.computeExpectedMeans(Utilities.readCsvFile("simpleSample"+dimension+"D.csv"));
    					Utilities.computeClustering("averageCertainSet.csv", K );
    					algorihtmCycle = false;
    					
    				} else if (algorithmChoice.equals("m")){
    					Utilities.chooseMostProbablePoints(Utilities.readCsvFile("simpleSample"+dimension+"D.csv"));
    					Utilities.computeClustering("mostProbableCertainSet.csv", K );
    					algorihtmCycle = false;
    				}
    			}*/
    			samplingCycle = false;
    			Utilities.computeExpectedMeans(Utilities.readCsvFile("simpleSample"+dimension+"D.csv"));
    			Utilities.chooseMostProbablePoints(Utilities.readCsvFile("simpleSample"+dimension+"D.csv"));
    			Utilities.computeClustering("averageCertainSet.csv", K, "average" );
    			Utilities.computeClustering("mostProbableCertainSet.csv", K, "mostProbable" );
    			
    			break;
    			
    			case "r" : sampler.randomSample(POINTS,10, MINVALUE, MAXVALUE); 
    			/*algorihtmCycle = true;
    			while (algorihtmCycle){
    				System.out.println("[Dimension: "+dimension + "][Random sampling] Choose how to manage uncertainty: "
    						+ "\n \"e\" for expected points algorithm. \n \"m\" for most probable points algorithm." );
    				algorithmChoice = scan.nextLine().toLowerCase();
    				if (algorithmChoice.equals("e")){
    					Utilities.computeExpectedMeans(Utilities.readCsvFile("randomSample"+dimension+"D.csv"));
    					Utilities.computeClustering("averageCertainSet.csv", K );
    					algorihtmCycle = false;
    				} else if (algorithmChoice.equals("m")){
    					Utilities.chooseMostProbablePoints(Utilities.readCsvFile("randomSample"+dimension+"D.csv"));
    					Utilities.computeClustering("mostProbableCertainSet.csv", K );
    					algorihtmCycle = false;
    				}
    			}*/
    			samplingCycle = false;
    			Utilities.computeExpectedMeans(Utilities.readCsvFile("randomSample"+dimension+"D.csv"));
    			Utilities.chooseMostProbablePoints(Utilities.readCsvFile("randomSample"+dimension+"D.csv"));
    			Utilities.computeClustering("averageCertainSet.csv", K, "average" );
    			Utilities.computeClustering("mostProbableCertainSet.csv", K, "mostProbable" );
    			
    			break;
    			
    			case "p" : sampler.poissonSample(POINTS,Utilities.getRandomMeanVectors(dimension, POINTS, MINVALUE, MAXVALUE)); 
    			/*algorihtmCycle = true;
    			while (algorihtmCycle){
    				System.out.println("[Dimension: "+dimension + "][Poisson sampling] Choose how to manage uncertainty: "
    						+ "\n \"e\" for expected points algorithm. \n \"m\" for most probable points algorithm." );
    				algorithmChoice = scan.nextLine().toLowerCase();
    				if (algorithmChoice.equals("e")){
    					Utilities.computeExpectedMeans(Utilities.readCsvFile("poissonSample"+dimension+"D.csv"));
    					Utilities.computeClustering("averageCertainSet.csv", K );
    					algorihtmCycle = false;
    				} else if (algorithmChoice.equals("m")){
    					Utilities.chooseMostProbablePoints(Utilities.readCsvFile("poissonSample"+dimension+"D.csv"));
    					Utilities.computeClustering("mostProbableCertainSet.csv", K );
    					algorihtmCycle = false;
    				}
    			}*/
    			samplingCycle = false;
    			Utilities.computeExpectedMeans(Utilities.readCsvFile("poissonSample"+dimension+"D.csv"));
    			Utilities.chooseMostProbablePoints(Utilities.readCsvFile("poissonSample"+dimension+"D.csv"));
    			Utilities.computeClustering("averageCertainSet.csv", K, "average" );
    			Utilities.computeClustering("mostProbableCertainSet.csv", K, "mostProbable" );
    			break;
    			}
    		}
    }
     
}
