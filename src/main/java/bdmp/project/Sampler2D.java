package bdmp.project;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

public class Sampler2D {		
	
	// very basic algorithm that generate for each sample two uncertain points in range (minValue,maxValue) with high difference in probability if parameter highDifference is true
	public void simpleSample(int numberOfSamples, int minValue, int maxValue, boolean highDifference) throws FileNotFoundException{ 
		List<MyPoint2D> points = new ArrayList<MyPoint2D>();
		MyPoint2D p1,p2;
		int counter = 0;
		String id;	// identifier that associates to each sample a list of uncertain points
		
		while (counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			if(highDifference){
				p1 = new MyPoint2D(id, minValue + Math.random() * (maxValue - minValue), minValue + Math.random() * (maxValue - minValue), 0.8);
				p2 = new MyPoint2D(id, minValue + Math.random() * (maxValue - minValue), minValue + Math.random() * (maxValue - minValue), 0.2);
			} else {
				p1 = new MyPoint2D(id, minValue + Math.random() * (maxValue - minValue), minValue + Math.random() * (maxValue - minValue), 0.5);
				p2 = new MyPoint2D(id, minValue + Math.random() * (maxValue - minValue), minValue + Math.random() * (maxValue - minValue), 0.5);
			}
			points.add(p1);
			points.add(p2);
			counter++;
		}
		printToFile(points, "simplesample2D.csv");
	}
	
	// completely random algorithm that for each sample generate a list of uncertain points with random probabilities, where the max length of this list is maxNumberOfUncertainPoints
	public void genericSample(int numberOfSamples, int maxNumberOfUncertainPoints,  int minValue, int maxValue) throws FileNotFoundException{ 
		List<MyPoint2D> points = new ArrayList<MyPoint2D>();
		MyPoint2D p;
		int numberOfUncertainPoints, counter = 0, unCounter = 0;
		String id;
		
		while (counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			numberOfUncertainPoints = 1 + (int)(Math.random() * maxNumberOfUncertainPoints); // choose number of uncertain points
			double[] probabilities = getRandomProbabilities(numberOfUncertainPoints);	// get a probability vector of size equal to numberOfUncertainPoints
			while (unCounter < numberOfUncertainPoints){	// generate the uncertainPoints using values inside the probabilities vector as uncertain points' probabilities
				p = new MyPoint2D(id, minValue + Math.random() * (maxValue - minValue), minValue + Math.random() * (maxValue - minValue), probabilities[unCounter]);
				points.add(p);
				unCounter++;
			}
			counter++;
		}
		printToFile(points, "genericSample2D.csv");
	}
	
	// For each sample generate uncertain points that are sampled from a Multivariate Gaussian distribution with mean = means and covariance = covariances
	public void gaussianSample(int numberOfSamples, double [] means, double[][] covariances) throws FileNotFoundException{
		MultivariateNormalDistribution gaussian = new MultivariateNormalDistribution(means, covariances); // Gaussian distribution
		List<MyPoint2D> points = new ArrayList<MyPoint2D>();
		double[] sample; 
		double totalProbability = 0;
		String id;
		int counter = 0;
		
		while(counter < numberOfSamples){
			id = UUID.randomUUID().toString();
			MyPoint2D p;
			
			while (totalProbability <= 1){ // iterate until the sum of the probabilities of the uncertain points is 1 (almost)
				sample = gaussian.sample();
				p = new MyPoint2D(id, sample[0], sample[1], gaussian.density(sample));
				totalProbability += gaussian.density(sample);
				points.add(p);
			}
			counter++;
		}
		printToFile(points, "gaussianSample2D.csv");
	}
	
	private double[] getRandomProbabilities(int n)
	{
		Random rand = new Random();
		double randomProbabilities[] = new double[n], sum = 0;
		
		for (int i = 0; i < n; i++){
			randomProbabilities[i] = rand.nextDouble();
			sum += randomProbabilities[i];
		}
		
		// Divide obtaining double array that sums to 1
		for(int i = 0; i < n; i++){
			randomProbabilities[i] /= sum;
		}
		
		return randomProbabilities;
	}
	
	private void printToFile(List<MyPoint2D> points, String filename) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("input/"+filename));
		StringBuilder sb = new StringBuilder();
		while(!points.isEmpty()){
			MyPoint2D p = points.remove(0);
			sb.append(p.getId());
			sb.append(",");
			sb.append(p.x1);
			sb.append(",");
			sb.append(p.x2);
			sb.append(",");
			sb.append(p.getProb());
			sb.append("\n");
		}
		pw.write(sb.toString());
		pw.close();
	}

}
