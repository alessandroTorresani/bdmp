package bdmp.project;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
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
		String id;	// identifier that associates to each sample a list of uncertain points

		while (counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			if(highDifference){
				p1 = new PointKD(id, this.dimension, getRandomDimensions(minValue, maxValue) , 0.8);
				p2 = new PointKD(id, this.dimension, getRandomDimensions(minValue, maxValue) , 0.2);
			} else {
				p1 = new PointKD(id, this.dimension, getRandomDimensions(minValue, maxValue) , 0.5);
				p2 = new PointKD(id, this.dimension, getRandomDimensions(minValue, maxValue) , 0.5);
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
		String id;
		
		while (counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			numberOfUncertainPoints = 1 + (int)(Math.random() * maxNumberOfUncertainPoints); // choose number of uncertain points
			double[] probabilities = getRandomProbabilities(numberOfUncertainPoints);	// get a probability vector of size equal to numberOfUncertainPoints
			uncertainCounter = 0;
			while (uncertainCounter < numberOfUncertainPoints){	// generate the uncertainPoints using values inside the probabilities vector as uncertain points' probabilities
				p = new PointKD(id, this.dimension, getRandomDimensions(minValue, maxValue), probabilities[uncertainCounter]);
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
		String id;
		
		while(counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			samples = getPoissonSample(lambdas.get(counter));
			for (int j = 0; j < samples.size(); j++){
				double[] uncertainDimensions = new double[samples.get(j).getFirst().length];
				for (int i = 0; i < uncertainDimensions.length; i++){	// Convert array of integers to array of doubles
					uncertainDimensions[i] = samples.get(j).getFirst()[i];
				}
				p = new PointKD(id, this.dimension,  uncertainDimensions, samples.get(j).getSecond());
				points.add(p);
			}
			counter++;
		}
		Utilities.writeUncertainPointsToFile(points, "poissonSample"+this.dimension+"D.csv");
	}
	
	private List<Pair<int[],Double>> getPoissonSample(double []lambdas){
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
			if (currentProbability == 1.00){
				cycle = false;
			}
			for (int i = 0; i < this.dimension; i++ ){
				samples[i] = poissons[i].sample();
			}
			for (int i = 0; i < this.dimension; i++){
				sampleProbability = Utilities.roundTo2decimals(sampleProbability * poissons[i].cumulativeProbability(samples[i])); // assumes that Poisson distribution are independent 
			}
			if (sampleProbability + Utilities.roundTo2decimals(currentProbability) <= 1.00 && sampleProbability > 0.0){ 
				uncertainPairs.add(new Pair<int[], Double>(samples,sampleProbability));
				currentProbability = Utilities.roundTo2decimals(currentProbability + sampleProbability);
			}
		}
		return uncertainPairs;
	}
	
	private double[] getRandomProbabilities(int n)
	{
		Random rand = new Random();
		double randomProbabilities[] = new double[n], sum = 0.0;
		
		for (int i = 0; i < n; i++){
			randomProbabilities[i] = Utilities.roundTo2decimals(rand.nextDouble());
			sum = Utilities.roundTo2decimals(sum+randomProbabilities[i]);
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
	
	

}
