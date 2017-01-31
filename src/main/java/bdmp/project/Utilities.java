package bdmp.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

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
		PrintWriter pw = new PrintWriter(new File(System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename));
		StringBuilder sb = new StringBuilder();
		sb.append("identifier");
		sb.append(",");
		for(int i = 0; i < points.get(0).getDimension(); i++){
			sb.append("d"+(i+1));
			sb.append(",");
		}
		sb.append("probability");
		sb.append("\n");
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
		PrintWriter pw = new PrintWriter(new File(System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename));
    	StringBuilder sb = new StringBuilder();
    	sb.append("identifier");
    	sb.append(",");
		for(int i = 0; i < points.get(0).getDimension(); i++){
			sb.append("d"+(i+1));
			sb.append(",");
		}
		sb.append("\n");
    	while(!points.isEmpty()){
    		PointKD p = points.remove(0);
    		sb.append(p.getId());
    		sb.append(",");
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
    		br = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename));
    		String line="";
    		br.readLine(); // skip the first line (headers)
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
	
	public static void computeClustering(String filename, int k, String outputName) throws IOException{
		Dataset data = FileHandler.loadDataset(new File(System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename), 0, ",");
    	data.remove(0); // remove headers
        KMeans km = new KMeans(k);
        Dataset[] clusters = km.cluster(data);
        
        //computeRadius(clusters[0]);
        File output = new File(System.getProperty("user.home")+"/Documents/bdmpFiles/output/"+outputName+"/"); // creates output folders
		output.mkdirs();
        for (int i=0; i< k; i++){
        	FileHandler.exportDataset(clusters[i], new File(System.getProperty("user.home")+"/Documents/bdmpFiles/output/"+outputName+"/cluster"+i+".txt"));
        }
        System.out.println("Clustering completed, check the results at the following path: \n" + 
        		System.getProperty("user.home")+"/Documents/bdmpFiles/output/"+outputName+"/");
	}
	
	/*public static double computeRadius(Dataset clusters){
		double centroid;
		String input = new String();
		StringBuffer sb = new StringBuffer();
		for (Instance i : clusters){
			sb.append(i.toString());
		}
		input = sb.toString();
		System.out.println(input);
		input.replaceAll("\\}", "");
		input.replaceAll("\\{", "");
		input.replaceAll("\\]", "");
		input.replaceAll("\\[", "");
		System.out.println(input);
		return 0;
	}*/
	
	public static void initializeFolders() throws IOException{
		File input = new File(System.getProperty("user.home")+"/Documents/bdmpFiles/input/");
		input.mkdirs(); 
		
		File output = new File(System.getProperty("user.home")+"/Documents/bdmpFiles/output/");
		if (output.isDirectory()){
			File[] files = output.listFiles();
			int numberOfFiles = files.length;
			for (int i = 0; i < numberOfFiles; i++){
				if(files[i].isDirectory()){
					File[] subDirFiles = files[i].listFiles();
					for (File file : subDirFiles){
						file.delete();
					}
				} else {
					files[i].delete();
				}
			}
		}
		output.mkdirs();
	}
	
	
	
	public static double roundTo2decimals(double num){
		DecimalFormat df = new DecimalFormat("#.##");
    	double rounded = Double.parseDouble(df.format(num).replaceAll(",", "."));
		return rounded;
	}
	
	
	// ************************* Experimental ************************* //
	
	//Experimental version of writing to file that appends elements instead of writing them
	public static void writeUncertainPointsToFileEx(List<PointKD> points, String filename) throws FileNotFoundException{
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
		try {
			// This operation assumes that file already exists
			Files.write(Paths.get(System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename), sb.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			// Handle exception
			System.out.println(e.getMessage());
		}
	}
	
	public static void initializeFile(String filename, int dimension) throws IOException{

		String path = System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename; // Delete file if exists
		Path old = Paths.get(path);
		Files.deleteIfExists(old);
		
		File f = new File(path); // Creates a new file
		f.createNewFile(); 
		writeHeaders(dimension, filename); // Initialize the headers
	}
	
	public static void writeHeaders(int dimension, String filename) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		sb.append("identifier");
		sb.append(",");
		for(int i = 0; i < dimension; i++){
			sb.append("d"+(i+1));
			sb.append(",");
		}
		sb.append("probability");
		sb.append("\n");
		try {
			// This operation assumes that file already exists
			Files.write(Paths.get(System.getProperty("user.home")+"/Documents/bdmpFiles/input/"+filename), sb.toString().getBytes(), StandardOpenOption.WRITE);
		} catch (IOException e) {
			// Handle exception
			System.out.println(e.getMessage());
		}
	}
	
}
