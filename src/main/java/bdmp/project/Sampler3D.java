package bdmp.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

public class Sampler3D {
	public void simpleSample(int num, int min, int max, boolean highDifference) throws FileNotFoundException{ // num is number of samples and the max number of points for each identifier
		int counter = 0;
		String id;
		
		List<MyPoint3D> points = new ArrayList<MyPoint3D>();
		
		while (counter < num){
			id = UUID.randomUUID().toString();
			MyPoint3D p1,p2;
			if(highDifference){
				p1 = new MyPoint3D(id, min + Math.random() * (max - min), min + Math.random() * (max - min), min + Math.random() * (max - min), 0.8);
				p2 = new MyPoint3D(id, min + Math.random() * (max - min), min + Math.random() * (max - min), min + Math.random() * (max - min), 0.2);
			} else {
				p1 = new MyPoint3D(id, min + Math.random() * (max - min), min + Math.random() * (max - min), min + Math.random() * (max - min), 0.5);
				p2 = new MyPoint3D(id, min + Math.random() * (max - min), min + Math.random() * (max - min), min + Math.random() * (max - min), 0.5);
			}
			points.add(p1);
			points.add(p2);
			counter++;
		}
		printToFile(points, "simplesample3D.csv");
	}
	
	public void genericSample(int num, int uncertainPointsMax,  int min, int max) throws FileNotFoundException{ // num is number of samples and the max number of points for each identifier
		int uncertainPoints, counter = 0;
		String id;
		List<MyPoint3D> points = new ArrayList<MyPoint3D>();
		
		while (counter < num){
			id = UUID.randomUUID().toString();
			MyPoint3D p;
			
			// Choose number of uncertain points
			uncertainPoints = 1 + (int)(Math.random() * uncertainPointsMax);
			int unCounter = 0;
			double[] probabilities = randSum(uncertainPoints);
			System.out.println("before cycle");
			while (unCounter < uncertainPoints){
				System.out.println(unCounter);
				p = new MyPoint3D(id, min + Math.random() * (max - min), min + Math.random() * (max - min), min + Math.random() * (max - min), probabilities[unCounter]);
				points.add(p);
				unCounter++;
			}
			counter++;
		}
		printToFile(points, "genericSample3D.csv");
	}
	
	public void gaussianSample(int num, double [] means, double[][] covariances) throws FileNotFoundException{
		String id;
		int counter = 0;
		MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means, covariances);
		List<MyPoint3D> points = new ArrayList<MyPoint3D>();
		while(counter < num){
			double[] sample; 
			id = UUID.randomUUID().toString();
			MyPoint3D p;
			double totProb = 0;
			while (totProb <= 1){ //until prob sum to 1
				sample = mnd.sample();
				p = new MyPoint3D(id, sample[0], sample[1], sample[2], mnd.density(sample));
				totProb += mnd.density(sample);
				points.add(p);
			}
			counter++;
		}
		printToFile(points, "gaussianSample3D.csv");
		
	}
	
	private double[] randSum(int n)
	{
		Random rand = new Random();
		double randNums[] = new double[n], sum = 0;
		
		for (int i = 0; i < n; i++){
			randNums[i] = rand.nextDouble();
			sum += randNums[i];
		}
		
		// Divide obtaining a sum to 1 double array
		for(int i = 0; i < n; i++){
			randNums[i] /= sum;
		}
		
		return randNums;
	}
	
	private void printToFile(List<MyPoint3D> points, String filename) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("input/"+filename));
		StringBuilder sb = new StringBuilder();
		while(!points.isEmpty()){
			MyPoint3D p = points.remove(0);
			sb.append(p.getId());
			sb.append(",");
			sb.append(p.x1);
			sb.append(",");
			sb.append(p.x2);
			sb.append(",");
			sb.append(p.x3);
			sb.append(",");
			sb.append(p.getProb());
			sb.append("\n");
		}
		pw.write(sb.toString());
		pw.close();
	}
}
