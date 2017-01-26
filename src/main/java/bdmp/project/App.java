package bdmp.project;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;



public class App 
{
    public static void main( String[] args ) throws IOException
    {
    		final int K = 2;
    		int dimension = 2;
    		Scanner scan1 = new Scanner(System.in);
    		boolean dimensionCycle = true;
    		
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
    			
    			case "s" : sampler.simpleSample(100, 0, 100, true); 
    			algorihtmCycle = true;
    			while (algorihtmCycle){
    				System.out.println("[Dimension: "+dimension + "][Simple sampling] Choose how to manage uncertainty: "
    						+ "\n \"e\" for expected points algorithm. \n \"m\" for most probable points algorithm." );
    				algorithmChoice = scan.nextLine().toLowerCase();
    				if (algorithmChoice.equals("e")){
    					Utilities.computeExpectedMeans(Utilities.readCsvFile("input/simpleSample"+dimension+"D.csv"));
    					Utilities.computeClustering("averageCertainSet.csv", K );
    					algorihtmCycle = false;
    					
    				} else if (algorithmChoice.equals("m")){
    					Utilities.chooseMostProbablePoints(Utilities.readCsvFile("input/simpleSample"+dimension+"D.csv"));
    					Utilities.computeClustering("mostProbableCertainSet.csv", K );
    					algorihtmCycle = false;
    				}
    			}
    			
    			samplingCycle = false;
    			break;
    			
    			case "r" : sampler.randomSample(100,10, 0, 100); 
    			algorihtmCycle = true;
    			while (algorihtmCycle){
    				System.out.println("[Dimension: "+dimension + "][Random sampling] Choose how to manage uncertainty: "
    						+ "\n \"e\" for expected points algorithm. \n \"m\" for most probable points algorithm." );
    				algorithmChoice = scan.nextLine().toLowerCase();
    				if (algorithmChoice.equals("e")){
    					Utilities.computeExpectedMeans(Utilities.readCsvFile("input/randomSample"+dimension+"D.csv"));
    					Utilities.computeClustering("averageCertainSet.csv", K );
    					algorihtmCycle = false;
    				} else if (algorithmChoice.equals("m")){
    					Utilities.chooseMostProbablePoints(Utilities.readCsvFile("input/randomSample"+dimension+"D.csv"));
    					Utilities.computeClustering("mostProbableCertainSet.csv", K );
    					algorihtmCycle = false;
    				}
    			}
    			samplingCycle = false;
    			break;
    			
    			case "p" : sampler.poissonSample(100,Utilities.getRandomMeanVectors(dimension, 100, 0, 100)); 
    			algorihtmCycle = true;
    			while (algorihtmCycle){
    				System.out.println("[Dimension: "+dimension + "][Poisson sampling] Choose how to manage uncertainty: "
    						+ "\n \"e\" for expected points algorithm. \n \"m\" for most probable points algorithm." );
    				algorithmChoice = scan.nextLine().toLowerCase();
    				if (algorithmChoice.equals("e")){
    					Utilities.computeExpectedMeans(Utilities.readCsvFile("input/poissonSample"+dimension+"D.csv"));
    					Utilities.computeClustering("averageCertainSet.csv", K );
    					algorihtmCycle = false;
    				} else if (algorithmChoice.equals("m")){
    					Utilities.chooseMostProbablePoints(Utilities.readCsvFile("input/poissonSample"+dimension+"D.csv"));
    					Utilities.computeClustering("mostProbableCertainSet.csv", K );
    					algorihtmCycle = false;
    				}
    			}
    			samplingCycle = false;
    			break;
    			}
    		}
    }
     
}
