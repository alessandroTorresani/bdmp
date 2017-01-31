package bdmp.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Prompt {
	static final int MINVALUE = 0;
	static final int MAXVALUE = 100;
	static final int POINTS = 100;
	static final int MAXUNCERTAINPOINTS = 10;
	
	static void sampleChoice() throws IOException{
		
		Scanner choiceScan = new Scanner(System.in);
		Scanner dimensionSCan = new Scanner(System.in);
		Scanner kScan = new Scanner(System.in);
		int dimension;
		Sampler sampler;
		boolean samplingCycle = true;
		String samplingChoice;
	
		System.out.println("Choose a sampling algorithm: \n \"s\" for simple sampling. "
				+ "\n \"r\" for random sampling. \n \"p\" for Poisson sampling. \n \"n\" for skipping the sampling and reuse previuos calculated data");
		samplingChoice = choiceScan.nextLine().toLowerCase();
		
		while(samplingCycle){
			switch (samplingChoice){
				case "s" : 
					dimension = askDimension(dimensionSCan);
					sampler = new Sampler(dimension);
					sampler.simpleSample(POINTS, MINVALUE, MAXVALUE, true); 
					samplingCycle = false;
					removeUncertainty("simpleSample", dimension);
					runKMeans(askK(kScan));
				break;
				case "r" : 
					dimension = askDimension(dimensionSCan);
					sampler = new Sampler(dimension);
					sampler.randomSample(POINTS, MAXUNCERTAINPOINTS, MINVALUE, MAXVALUE);
					samplingCycle = false;
					removeUncertainty("randomSample", dimension);
					runKMeans(askK(kScan));
				break;
				case "p" : 
					dimension = askDimension(dimensionSCan);
					sampler = new Sampler(dimension);
					sampler.poissonSample(POINTS, Utilities.getRandomMeanVectors(dimension, POINTS, MINVALUE, MAXVALUE));
					samplingCycle = false;
					removeUncertainty("poissonSample", dimension);
					runKMeans(askK(kScan));
				break;
				case "n" : 
					samplingCycle = false;
					runKMeans(askK(kScan));
				break;
			}
		}
		choiceScan.close();
		dimensionSCan.close();
		kScan.close();
		
	}
	
	static int askDimension(Scanner scan){
		int dimension = 2;
		boolean dimensionCycle = true;
		while(dimensionCycle){
			System.out.println("Choose the dimension of the points: ");
			try {
				dimension = scan.nextInt();
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
		return dimension;
	}
	
	static void removeUncertainty(String filename, int dimension) throws FileNotFoundException{
		Utilities.computeExpectedMeans(Utilities.readCsvFile(filename+dimension+"D.csv"));
		Utilities.chooseMostProbablePoints(Utilities.readCsvFile(filename+dimension+"D.csv"));
	}
	
	static int askK(Scanner scan){
		System.out.println("Select K for K-means algorithm: ");
		int k = Integer.parseInt(scan.nextLine());
		return k;
	}
	
	static void runKMeans(int k) throws IOException{
		Utilities.computeClustering("averageCertainSet.csv", k, "average" );
		Utilities.computeClustering("mostProbableCertainSet.csv", k, "mostProbable" );
	}
}
