package bdmp.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
      
    	Sampler sampler2D = new Sampler(2); //2D sampler
    	Sampler sampler3D = new Sampler(3); //3D sampler
    	Sampler sampler10D = new Sampler(10); //3D sampler
    	
    	sampler2D.simpleSample(5,0,+100, true);
    	//sampler2D.genericSample(500,10,-100,+100);
    	//sampler2D.gaussianSample(100, sampleMean(2,0,100), ampleCovariance(2)); //PROBLEM: we should use different means and covariances for each set of uncertain points.
    		
    	//sampler3D.simpleSample(500,-100 , 100, false);
    	//sampler3D.genericSample(500,10, -100 , 100);
    	//sampler3D.gaussianSample(10, sampleMean(3,0,100), sampleCovariance(3)); 
    	
    	//sampler20D.simpleSample(500, 0, 100, true);
    	//sampler20D.genericSample(500, 15, 0, 100);
    	//sampler10D.gaussianSample(10, sampleMean(10,0,100), sampleCovariance(10));
    	
    	// Working k-mean algorithm
       /* Dataset data = FileHandler.loadDataset(new File("input/aa.csv"), 2, ",");
        int k = 3;
        KMeans km = new KMeans(k);
        Dataset[] clusters = km.cluster(data);
        for (int i=0; i< k; i++){
        	System.out.println(clusters[i]+"\n");
        }
        */
    	computeCentroids(readCsvFile("input/simplesample2D.csv"));
    }
    
    private static double[] sampleMean(int dimension,int minValue, int maxValue){
    	double[] means = new double[dimension];
    	for (int i = 0; i < dimension; i++){
    		means[i] = minValue + Math.random() * (maxValue - minValue);
    	}
    	return means;
    }
    
    private static double[][] sampleCovariance(int dimension){
    	double[][] covariances = new double[dimension][dimension];
    	for (int i = 0; i < dimension; i++){
    		for (int j = 0; j < dimension; j++){
    			if (i == j){
    				covariances[i][j] = 1;
    			}
    		}
    	}
    	return covariances;
    }
    
    private static void computeCentroids(Map<String, List<PointKD>> points){
    	List<PointKD> uncertainPoints = new ArrayList<PointKD>();
    	Set<String> keys = points.keySet();
    	Iterator<String> it = keys.iterator();
    	System.out.println("size keyset: " + keys.size());
    	while(it.hasNext()){
    		System.out.println(it.next());
    	}

    	
    	
    	
    	/*String k1 = keys.iterator().next();
    	System.out.println("k1: " + k1);
    	String k2 = keys.iterator().next();
    	System.out.println("k2: " + k2);
    	String k3 = keys.iterator().next();
    	System.out.println("k3: " + k3);
    	System.out.println(points.get(k1).toString());*/
    	
    }
    
    private static Map<String, List<PointKD>> readCsvFile(String filename){
    	final String COMMA_DELIMITER = ",";
    	BufferedReader br = null;
    	Map<String, List<PointKD>> points = new HashMap<String, List<PointKD>>();
    	List<PointKD> templist = new ArrayList<PointKD>();
    	try {
    		br = new BufferedReader(new FileReader(filename));
    		String line="";
    		while((line = br.readLine()) != null){
    			String[] parts = line.split(COMMA_DELIMITER);
    			System.out.println("parts: " + parts[0]);
    			if (parts.length > 0){
    				double[] dimensions = new double[parts.length-2];
    				for (int i = 0; i < parts.length-2; i++){
    					dimensions[i] = Double.parseDouble(parts[1+i]);
    				}
    				PointKD p = new PointKD(parts[0], parts.length-2, dimensions, Double.parseDouble(parts[parts.length-1]));
    				if(points.containsKey(parts[0])){
    					templist = points.get(parts[0]);
    					templist.add(p);
    					points.put(parts[0], templist);
    				} else {
    					templist.add(p);
    					points.put(parts[0], templist);
    				}
    				templist.clear();
    			}
    		}
    		return points;
    		
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
    	return null;
    }
}
