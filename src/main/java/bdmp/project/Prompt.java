package bdmp.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.math3.util.Pair;

import net.sf.javaml.core.Dataset;

public class Prompt {
	static final int MINVALUE = 0;
	static final int MAXVALUE = 100;
	static final int POINTS = 10;
	static final int MAXUNCERTAINPOINTS = 10;
	
	static void sampleChoice() throws IOException{
		
		// Input scanners
		Scanner choiceScan = new Scanner(System.in);
		Scanner dimensionSCan = new Scanner(System.in);
		Scanner kScan = new Scanner(System.in);
		
		// Sampler variables
		Sampler sampler;
		int dimension;
		
		// Prompt variables
		boolean samplingCycle = true;
		String samplingChoice;
		
		// K-means algorithm variables
		Pair<Dataset[],Dataset[]> clusters;
		Dataset[] clustersMostProbable; 
		Dataset[] clustersAverage;
		Map<String, Pair<double[],List<double[]>>> centroidsAndPointsAverage;
		Map<String, Pair<double[],List<double[]>>> centroidsAndPointsMostProbable;
		
		System.out.println("Choose a sampling algorithm: \n \"s\" for simple sampling. "
				+ "\n \"r\" for random sampling. \n \"p\" for Poisson sampling. \n \"n\""
				+ " for skipping the sampling and reuse previuos calculated data");
		samplingChoice = choiceScan.nextLine().toLowerCase();
		
		while(samplingCycle){
			switch (samplingChoice){
				case "s" : 
					// get points dimension, sampler and run average and most probable algorithm on uncertain data
					dimension = askDimension(dimensionSCan);
					sampler = new Sampler(dimension);
					sampler.simpleSample(POINTS, MINVALUE, MAXVALUE, true); 
					samplingCycle = false;
					removeUncertainty("simpleSample", dimension);
					
					// run k-mean on average and most probable data sets
					clusters = runKMeans(askK(kScan)); 
					clustersAverage = clusters.getFirst();
					clustersMostProbable = clusters.getSecond();
					
					// get a map where to each key(cluster) is associated a list of points and the centroid of the cluster. Use this maps to compute 
					// silhouetteScore of the cluster.
					centroidsAndPointsAverage = Utilities.getCentroidsAndPoints(clustersAverage); 
					centroidsAndPointsMostProbable = Utilities.getCentroidsAndPoints(clustersMostProbable);
					System.out.println("[Average] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsAverage));
					System.out.println("[Most Probable] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsMostProbable));
				break;
				case "r" : 
					// get points dimension, sampler and run average and most probable algorithm on uncertain data
					dimension = askDimension(dimensionSCan);
					sampler = new Sampler(dimension);
					sampler.randomSample(POINTS, MAXUNCERTAINPOINTS, MINVALUE, MAXVALUE);
					samplingCycle = false;
					removeUncertainty("randomSample", dimension);
					
					// run k-mean on average and most probable data sets
					clusters = runKMeans(askK(kScan));
					clustersAverage = clusters.getFirst();
					clustersMostProbable = clusters.getSecond();
					
					// get a map where to each key(cluster) is associated a list of points and the centroid of the cluster. Use this maps to compute 
					// silhouetteScore of the cluster.
					centroidsAndPointsAverage = Utilities.getCentroidsAndPoints(clustersAverage);
					centroidsAndPointsMostProbable = Utilities.getCentroidsAndPoints(clustersMostProbable);
					System.out.println("[Average] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsAverage));
					System.out.println("[Most Probable] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsMostProbable));
				break;
				case "p" : 
					// get points dimension, sampler and run average and most probable algorithm on uncertain data
					dimension = askDimension(dimensionSCan);
					sampler = new Sampler(dimension);
					sampler.poissonSample(POINTS, Utilities.getRandomMeanVectors(dimension, POINTS, MINVALUE, MAXVALUE));
					samplingCycle = false;
					removeUncertainty("poissonSample", dimension);
					
					// run k-mean on average and most probable data sets
					clusters = runKMeans(askK(kScan));
					clustersAverage = clusters.getFirst();
					clustersMostProbable = clusters.getSecond();
					
					// get a map where to each key(cluster) is associated a list of points and the centroid of the cluster. Use this maps to compute 
					// silhouetteScore of the cluster.
					centroidsAndPointsAverage = Utilities.getCentroidsAndPoints(clustersAverage);
					centroidsAndPointsMostProbable = Utilities.getCentroidsAndPoints(clustersMostProbable);
					System.out.println("[Average] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsAverage));
					System.out.println("[Most Probable] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsMostProbable));
				break;
				case "n" : 
					// skip sampling stage, reuse previous "certain" data computed by average and most probable algorithms on the sampled data
					samplingCycle = false;
					
					// run k-mean on average and most probable data sets
					clusters = runKMeans(askK(kScan));
					clustersAverage = clusters.getFirst();
					clustersMostProbable = clusters.getSecond();
					
					// get a map where to each key(cluster) is associated a list of points and the centroid of the cluster. Use this maps to compute 
					// silhouetteScore of the cluster.
					centroidsAndPointsAverage = Utilities.getCentroidsAndPoints(clustersAverage);
					centroidsAndPointsMostProbable = Utilities.getCentroidsAndPoints(clustersMostProbable);
					System.out.println("[Average] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsAverage));
					System.out.println("[Most Probable] Silhouette score is: " + Utilities.silhouetteScore(centroidsAndPointsMostProbable));
				break;
			}
		}
		
		// close the scanners
		choiceScan.close();
		dimensionSCan.close();
		kScan.close();
		
	}
	
	// prompt points' dimension 
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
				System.err.println("Dimension must be an integer between 2 and 100.");
			}
		}
		return dimension;
	}
	
	static void removeUncertainty(String filename, int dimension) throws FileNotFoundException{
		Utilities.computeExpectedMeans(Utilities.loadSamples(filename+dimension+"D.csv"));
		Utilities.chooseMostProbablePoints(Utilities.loadSamples(filename+dimension+"D.csv"));
	}
	
	static int askK(Scanner scan){
		System.out.println("Select K for K-means algorithm: ");
		int k = Integer.parseInt(scan.nextLine());
		return k;
	}
	
	static Pair<Dataset[], Dataset[]>runKMeans(int k) throws IOException{
		return new Pair<Dataset[], Dataset[]>(Utilities.computeClustering("averageCertainSet.csv", k, "average" ), Utilities.computeClustering("mostProbableCertainSet.csv", k, "mostProbable" ));
	}
}
