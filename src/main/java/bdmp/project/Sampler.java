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
				p1 = new PointKD(id, this.dimension, getDimensions(minValue, maxValue) , 0.8);
				p2 = new PointKD(id, this.dimension, getDimensions(minValue, maxValue) , 0.2);
			} else {
				p1 = new PointKD(id, this.dimension, getDimensions(minValue, maxValue) , 0.5);
				p2 = new PointKD(id, this.dimension, getDimensions(minValue, maxValue) , 0.5);
			}
			points.add(p1);
			points.add(p2);
			counter++;
		}
		printToFile(points, "simpleSample"+this.dimension+"D.csv");
	}

	// completely random algorithm that for each sample generate a list of uncertain points with random probabilities, where the max length of this list is maxNumberOfUncertainPoints
	public void genericSample(int numberOfSamples, int maxNumberOfUncertainPoints,  int minValue, int maxValue) throws FileNotFoundException{ 
		List<PointKD> points = new ArrayList<PointKD>();
		PointKD p;
		int numberOfUncertainPoints, counter = 0, unCounter = 0;
		String id;
		
		while (counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			numberOfUncertainPoints = 1 + (int)(Math.random() * maxNumberOfUncertainPoints); // choose number of uncertain points
			double[] probabilities = getRandomProbabilities(numberOfUncertainPoints);	// get a probability vector of size equal to numberOfUncertainPoints
			unCounter = 0;
			while (unCounter < numberOfUncertainPoints){	// generate the uncertainPoints using values inside the probabilities vector as uncertain points' probabilities
				p = new PointKD(id, this.dimension, getDimensions(minValue, maxValue), probabilities[unCounter]);
				points.add(p);
				unCounter++;
			}
			counter++;
		}
		printToFile(points, "genericSample"+this.dimension+"D.csv");
	}
	
	// For each sample generate uncertain points that are sampled from a Multivariate Gaussian distribution with mean = means and covariance = covariances
	public void gaussianSample(int numberOfSamples, double [] means, double[][] covariances) throws FileNotFoundException{
		
		if (means.length != this.dimension || covariances.length != this.dimension || covariances[0].length != dimension)
		{
			System.out.println("Error, you pass a vector of size " + means.length + ", but dimension of this sampler is :" + this.dimension);
		} 
		else 
		{
			MultivariateNormalDistribution gaussian = new MultivariateNormalDistribution(means, covariances); // Gaussian distribution
			List<PointKD> points = new ArrayList<PointKD>();
			double[] sample; 
			double totalProbability = 0;
			String id;
			int counter = 0;
			
			while(counter < numberOfSamples){
				id = UUID.randomUUID().toString();
				PointKD p;
				totalProbability = 0;
				while (totalProbability <= 1){ // iterate until the sum of the probabilities of the uncertain points is 1 (almost)
					sample = gaussian.sample();
					p = new PointKD(id, this.dimension, sample, gaussian.density(sample));
					totalProbability += gaussian.density(sample);
					points.add(p);
				}
				counter++;
			}
			printToFile(points, "gaussianSample"+this.dimension+"D.csv");
		}
	}
	
	// Poisson distribution sample
	public void PoissonSample(int numberOfSamples, int maxNumberOfUncertainPoints, double mean){
		List<PointKD> points = new ArrayList<PointKD>();
		int counter = 0;
		
		String id;
	/*	while(counter < numberOfSamples){
			id = UUID.randomUUID().toString();	// generate a random identifier
			//p = new PointKD(id, maxNumberOfUncertainPoints, dimensions, prob);
		}*/
		
		getPoissonSample(mean);
	}
	
	private List<Pair<int[],Double>> getPoissonSample(double mean){
		List<Pair<int[],Double>> list= new ArrayList();
		PoissonDistribution []poissons = new PoissonDistribution[this.dimension];
		for (int i = 0; i < this.dimension; i++){
			poissons[i] = new PoissonDistribution(mean);
		}
		int [] samples = new int[this.dimension];
		double totProb = 0;
		double sampleProb;
		boolean cycle = true;
		while (cycle){
			sampleProb = 1;
			if (totProb == 1.00){
				cycle = false;
			}
			for (int i = 0; i < this.dimension; i++ ){
				samples[i] = poissons[i].sample();
			}
			for (int i = 0; i < this.dimension; i++){
				sampleProb = roundTwo(sampleProb * poissons[i].cumulativeProbability(samples[i]));
			}
			if (sampleProb + roundTwo(totProb) <= 1.00 && sampleProb > 0.0){ 
				list.add(new Pair<int[], Double>(samples,sampleProb));
				totProb = roundTwo(totProb + sampleProb);
			}
			System.out.println("Remaining prob: " + totProb);
		}
		System.out.println(list.toString());
		return list;
	}
	
	private double[] getRandomProbabilities(int n)
	{
		Random rand = new Random();
		double randomProbabilities[] = new double[n], sum = 0.0;
		
		for (int i = 0; i < n; i++){
			randomProbabilities[i] = roundTwo(rand.nextDouble());
			sum = roundTwo(sum+randomProbabilities[i]);
		}
		
		// Divide obtaining double array that sums to 1
		for(int i = 0; i < n; i++){
			randomProbabilities[i] = roundTwo(randomProbabilities[i]/sum);
		}
		
		return randomProbabilities;
	}
	
	private double[] getDimensions(int minValue, int maxValue){
		double[] dimensions = new double[this.dimension];
		for (int i = 0; i < this.dimension; i++){
			dimensions[i] = roundTwo(minValue + Math.random() * (maxValue - minValue));
		}
		return dimensions;
	}
	
	private void printToFile(List<PointKD> points, String filename) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("input/"+filename));
		StringBuilder sb = new StringBuilder();
		while(!points.isEmpty()){
			PointKD p = points.remove(0);
			sb.append(p.getId());
			sb.append(",");
			for (int i = 0; i < this.dimension; i++){ // Cycle all over dimensions
				sb.append(p.getDimensions()[i]);
				sb.append(",");
			}
			sb.append(p.getProb());
			sb.append("\n");
		}
		pw.write(sb.toString());
		pw.close();
	}
	
	private double roundTwo(double num){
		DecimalFormat df = new DecimalFormat("#.##");
    	double b = Double.parseDouble(df.format(num).replaceAll(",", "."));
		return b;
	}

}
