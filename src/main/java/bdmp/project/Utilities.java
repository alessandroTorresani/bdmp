package bdmp.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utilities {
	public static double[] getRandomMeanVector(int dimension,int minValue, int maxValue){
    	double[] means = new double[dimension];
    	for (int i = 0; i < dimension; i++){
    		means[i] = minValue + Math.random() * (maxValue - minValue);
    	}
    	return means;
    }
	
	public static List<double[]> getRandomMeanVectors(int dimension, int size, int minValue, int maxValue){
		List<double[]> means = new ArrayList<double[]>();
    	for (int i = 0; i<size; i++){
    		means.add(Utilities.getRandomMeanVector(dimension, minValue, maxValue));
    	}
    	return means;
	}
	
	public static void computeExpectedMeans(Map<String, List<PointKD>> points) throws FileNotFoundException{
    	List<PointKD> uncertainPoints = new ArrayList<PointKD>();
    	Set<String> keys = points.keySet();
    	Iterator<String> it = keys.iterator();
    	List<PointKD> expectedPoints = new ArrayList<PointKD>();
    	
    	while(it.hasNext() && points.size() > 0){
    		String key = it.next(); // get the next key
    		uncertainPoints = points.get(key); // get uncertain points related to that key
    		int dimension = uncertainPoints.get(0).getDimension(); // assumes that all points have the same dimension
    		double[] newDimensions = new double[dimension]; // expected mean dimensions
    		for (int i = 0; i < dimension; i++){
    			newDimensions[i] = computeMean(uncertainPoints, i); // compute expected dimensions
    		}
    		expectedPoints.add(new PointKD(uncertainPoints.get(0).getId() , dimension, newDimensions, 1));
    	}
    	
    	writeCertainPointsToFile(expectedPoints, "averageCertainSet.csv");
	}
	
	private static double computeMean(List<PointKD> uncertainPoints, int dimensionindex){
		double res = 0;
		for (int i = 0; i < uncertainPoints.size(); i++){
			PointKD p = uncertainPoints.get(i);
			res += p.getDimensions()[dimensionindex] * p.getProb();
		}
		return roundTo2decimals(res);
	}
	
	public static void chooseMostProbablePoints(Map<String, List<PointKD>> points) throws FileNotFoundException{
		List<PointKD> uncertainPoints = new ArrayList<PointKD>();
    	Set<String> keys = points.keySet();
    	Iterator<String> it = keys.iterator();
    	List<PointKD> mostProbablePoints = new ArrayList<PointKD>();
    	while(it.hasNext() && points.size() > 0){
    		String key = it.next(); // get the next key
    		uncertainPoints = points.get(key); // get uncertain points related to that key
    		PointKD bestPoint = chooseMostProbablePoint(uncertainPoints);
    		bestPoint.setProb(1);
    		mostProbablePoints.add(bestPoint);
    	}
    	writeCertainPointsToFile(mostProbablePoints, "mostProbableCertainSet.csv");
    	
	}
	
	private static PointKD chooseMostProbablePoint(List<PointKD> uncertainPoints){
		double bestProbability = uncertainPoints.get(0).getProb();
		PointKD bestPoint = uncertainPoints.get(0);
		for (int i = 1; i < uncertainPoints.size(); i++){
			if (uncertainPoints.get(i).getProb() > bestProbability){
				bestPoint = uncertainPoints.get(i);
				bestProbability = uncertainPoints.get(i).getProb();
			}
		}
		return bestPoint;
		
	}
	
	public static void writeUncertainPointsToFile(List<PointKD> points, String filename) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("input/"+filename));
		StringBuilder sb = new StringBuilder();
		while(!points.isEmpty()){
			PointKD p = points.remove(0);
			sb.append(p.getId());
			sb.append(",");
			for (int i = 0; i < p.getDimension(); i++){ // Cycle all over dimensions
				sb.append(p.getDimensions()[i]);
				sb.append(",");
			}
			sb.append(p.getProb());
			sb.append("\n");
		}
		pw.write(sb.toString());
		pw.close();
	}
	
	private static void writeCertainPointsToFile(List<PointKD> points, String filename) throws FileNotFoundException{
    	
    	PrintWriter pw = new PrintWriter(new File("input/"+filename));
    	StringBuilder sb = new StringBuilder();
    	while(!points.isEmpty()){
    		PointKD p = points.remove(0);
    		//sb.append(p.getId());
    		//sb.append(",");
    		for (int i = 0; i < p.getDimension(); i++){ // Cycle all over dimensions
    			sb.append(p.getDimensions()[i]);
    			sb.append(",");
    		}
    		sb.append("\n");
    	}
    	pw.write(sb.toString());
    	pw.close();
    }
	
	
	
	public static Map<String, List<PointKD>> readCsvFile(String filename){
    	final String COMMA_DELIMITER = ",";
    	BufferedReader br = null;
    	Map<String, List<PointKD>> points = new HashMap<String, List<PointKD>>();	// associate to each key(certain point) a list of uncertain points (uncertain points) 
    	try {
    		br = new BufferedReader(new FileReader(filename));
    		String line="";
    		while((line = br.readLine()) != null){	// Iterate until there are lines to read
    			String[] parts = line.split(COMMA_DELIMITER);
    			if (parts.length > 0){
    				double[] dimensions = new double[parts.length-2];
    				for (int i = 0; i < parts.length-2; i++){
    					dimensions[i] = Double.parseDouble(parts[1+i]);
    				}
    				PointKD p = new PointKD(parts[0], parts.length-2, dimensions, Double.parseDouble(parts[parts.length-1]));
    				if(points.containsKey(parts[0])){
    					points.get(parts[0]).add(p);
    					List<PointKD> a = points.get(parts[0]);
    				} else {
    					List<PointKD> templist = new ArrayList<PointKD>();
    					templist.add(p);
    					points.put(parts[0], templist);
    				}
    			}
    		}
    		
    	} catch(Exception ee)
        {
            ee.printStackTrace();
        }
    	finally
        {
            try
            {
                br.close();
                
            }
            catch(IOException ie)
            {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    	return points;
    }
	
	public static double roundTo2decimals(double num){
		DecimalFormat df = new DecimalFormat("#.##");
    	double rounded = Double.parseDouble(df.format(num).replaceAll(",", "."));
		return rounded;
	}
}
