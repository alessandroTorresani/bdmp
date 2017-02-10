package bdmp.project;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.util.Pair;

public class Sampler {
	
	private int dimension;
	
	public Sampler(int dimension){
		this.dimension = dimension;
	}

	// very basic algorithm that generate for each sample two uncertain points in range (minValue,maxValue) with high difference in probability if parameter highDifference is true
	public void simpleSample(int numberOfSamples, int minValue, int maxValue, boolean highDifference) throws FileNotFoundException{ 
		List<PointKD> points = new ArrayList<PointKD>();
		PointKD p1,p2;
		int counter = 0;

		while (counter < numberOfSamples){
			if(highDifference){
				p1 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.8);
				p2 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.2);
			} else {
				p1 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.5);
				p2 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.5);
			}
			points.add(p1);
			points.add(p2);
			counter++;
		}
		Utilities.writeUncertainPointsToFile(points, "simpleSample"+this.dimension+"D.csv");
	}
	
	// completely random algorithm that for each sample generate a list of uncertain points with random probabilities, where the max length of this list is maxNumberOfUncertainPoints
	public void randomSample(int numberOfSamples, int maxNumberOfUncertainPoints,  int minValue, int maxValue) throws FileNotFoundException{ 
		List<PointKD> points = new ArrayList<PointKD>();
		PointKD p;
		int numberOfUncertainPoints, counter = 0, uncertainCounter = 0;
		
		while (counter < numberOfSamples){	
			numberOfUncertainPoints = 1 + (int)(Math.random() * maxNumberOfUncertainPoints); // choose number of uncertain points
			double[] probabilities = getRandomProbabilities(numberOfUncertainPoints);	// get a probability vector of size equal to numberOfUncertainPoints
			uncertainCounter = 0;
			while (uncertainCounter < numberOfUncertainPoints){	// generate the uncertainPoints using values inside the probabilities vector as uncertain points' probabilities
				p = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue), probabilities[uncertainCounter]);
				points.add(p);
				uncertainCounter++;
			}
			counter++;
		}
		Utilities.writeUncertainPointsToFile(points, "randomSample"+this.dimension+"D.csv");
	}
	
	// Poisson distribution sample
	public void poissonSample(int numberOfSamples, List<double[]> lambdas) throws FileNotFoundException{
		List<PointKD> points = new ArrayList<PointKD>();
		int counter = 0;
		List<Pair<int[],Double>> samples;
		PointKD p;
		
		while(counter < numberOfSamples){
			int maxNumberOfUncertainPoints = 1 + (int)(Math.random() * 10);
			samples = normalizePoissonSample(getPoissonSample(lambdas.get(counter),maxNumberOfUncertainPoints));
			for (int j = 0; j < samples.size(); j++){
				double[] uncertainDimensions = new double[samples.get(j).getFirst().length];
				for (int i = 0; i < uncertainDimensions.length; i++){	// Convert array of integers to array of doubles
					uncertainDimensions[i] = samples.get(j).getFirst()[i];
				}
				p = new PointKD(counter, this.dimension,  uncertainDimensions, samples.get(j).getSecond());
				points.add(p);
			}
			counter++;
		}
		Utilities.writeUncertainPointsToFile(points, "poissonSample"+this.dimension+"D.csv");
	}
	
	
	public List<Pair<int[],Double>> getPoissonSample(double []lambdas, int numberOfPoints){
		List<Pair<int[],Double>> uncertainPairs= new ArrayList<Pair<int[],Double>>();
		PoissonDistribution []poissons = new PoissonDistribution[this.dimension]; 
		for (int i = 0; i < this.dimension; i++){
			poissons[i] = new PoissonDistribution(lambdas[i]);
		}
		int counter = 0;
		double sampleProbability;
		while (counter < numberOfPoints){
			int [] samples = new int[this.dimension];
			sampleProbability = 1;
			for (int i = 0; i < this.dimension; i++ ){
				samples[i] = poissons[i].sample(); // Sample coordinates (x1,x2,..,xn) where each xn is a Poisson Distribution
			}
			for (int i = 0; i < this.dimension; i++){
				sampleProbability = Utilities.roundTo2decimals(sampleProbability * poissons[i].cumulativeProbability(samples[i])); // assumes that Poisson distribution are independent 
			}
			if (sampleProbability > 0.0){
				uncertainPairs.add(new Pair<int[], Double>(samples,sampleProbability));
				counter++;
			}
		}
		return uncertainPairs;
	}
	
	public List<Pair<int[],Double>> normalizePoissonSample(List<Pair<int[],Double>> list){
		List<Pair<int[],Double>> uncertainPairs= new ArrayList<Pair<int[],Double>>();
		double sum = 0;
		for (int i = 0; i < list.size(); i++){
			sum+= list.get(i).getSecond();
		}
		for (int j= 0; j < list.size(); j++){
			uncertainPairs.add(new Pair<int[], Double>(list.get(j).getFirst(), Utilities.roundTo2decimals(list.get(j).getSecond()/sum)));
		}
		return uncertainPairs;
	}
	
	private double[] getRandomProbabilities(int n)
	{
		Random rand = new Random();
		double randomProbabilities[] = new double[n], sum = 0.0;
		
		for (int i = 0; i < n; i++){
			randomProbabilities[i] = rand.nextDouble();
			sum = sum + randomProbabilities[i];
		}
		
		// Divide obtaining double array that sums to 1
		for(int i = 0; i < n; i++){
			randomProbabilities[i] = Utilities.roundTo2decimals(randomProbabilities[i]/sum);
		}
		
		return randomProbabilities;
	}
	
	private double[] getRandomDimensions(int minValue, int maxValue){
		double[] dimensions = new double[this.dimension];
		for (int i = 0; i < this.dimension; i++){
			dimensions[i] = Utilities.roundTo2decimals(minValue + Math.random() * (maxValue - minValue));
		}
		return dimensions;
	}
	
	// ************************* Parallel ************************* //
	
	public void simpleSampleParallel(int numberOfSamples, int minValue, int maxValue, boolean highDifference){
		
	}
	
	// ************************* Experimental ************************* //
	
	// Experimental version of simple sample that stores gradually points into csv file
	public void simpleSampleEx(int numberOfSamples, int minValue, int maxValue, boolean highDifference) throws IOException{
		List<PointKD> points = new ArrayList<PointKD>();
		PointKD p1,p2;
		int counter = 0, writeCounter = 0;
		Utilities.initializeFile("simpleSample"+this.dimension+"D.csv", this.dimension); // Initialize empty file
		while (counter < numberOfSamples){
			if(highDifference){
				p1 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.8);
				p2 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.2);
			} else {
				p1 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.5);
				p2 = new PointKD(counter, this.dimension, getRandomDimensions(minValue, maxValue) , 0.5);
			}
			points.add(p1);
			points.add(p2);
			if(writeCounter == 10000){
				Utilities.writeUncertainPointsToFileEx(points, "simpleSample"+this.dimension+"D.csv");
				writeCounter = 0;
				points.clear();
			}
			writeCounter++;
			counter++;
		}
		Utilities.writeUncertainPointsToFileEx(points, "simpleSample"+this.dimension+"D.csv"); // If writecounter didn't reach 10000, write anyway points to file 
	}

	// Old Poisson sampling method. It may suffer from infinite loop when poisson sampling is selected (2D)
	/*private List<Pair<int[],Double>> getPoissonSampleOld(double []lambdas){ 
		List<Pair<int[],Double>> uncertainPairs= new ArrayList<Pair<int[],Double>>();
		PoissonDistribution []poissons = new PoissonDistribution[this.dimension]; 
		for (int i = 0; i < this.dimension; i++){
			poissons[i] = new PoissonDistribution(lambdas[i]);
		}
		double currentProbability = 0;
		double sampleProbability;
		boolean cycle = true;
		while (cycle){
			int [] samples = new int[this.dimension];
			sampleProbability = 1;
			if (currentProbability == 1.00 || currentProbability == 0.99){
				cycle = false;
			}
			for (int i = 0; i < this.dimension; i++ ){
				samples[i] = poissons[i].sample(); // Sample coordinates (x1,x2,..,xn) where each xn is a Poisson Distribution
			}
			for (int i = 0; i < this.dimension; i++){
				sampleProbability = Utilities.roundTo2decimals(sampleProbability * poissons[i].cumulativeProbability(samples[i])); // assumes that Poisson distribution are independent 
			}
			if (sampleProbability + currentProbability <= 1.00 && sampleProbability > 0.0){ 
				uncertainPairs.add(new Pair<int[], Double>(samples,sampleProbability));
				currentProbability = Utilities.roundTo2decimals(currentProbability + sampleProbability);
			}
			System.out.println("Sample probability: "+ sampleProbability);
			System.out.println("Current probability: "+ currentProbability);
		}
		return uncertainPairs;
	}*/

}
