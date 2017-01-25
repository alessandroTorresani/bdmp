package bdmp.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    	
    	//sampler2D.simpleSample(10,0,100, true);
    	//sampler2D.randomSample(100,10,0,100);
    		
    	//sampler3D.simpleSample(10000,0 , 100, false);
    	//sampler3D.randomSample(10000,10, 0, 100);
    	
    	//sampler20D.simpleSample(500, 0, 100, true);
    	//sampler20D.genericSample(500, 15, 0, 100);
    	
    	//sampler2D.poissonSample(10, Utilities.getRandomMeanVectors(2, 10, 0, 100));
    	
    	//Utilities.computeExpectedMeans(Utilities.readCsvFile("input/randomSample2D.csv"));
    	//Utilities.chooseMostProbablePoints(Utilities.readCsvFile("input/randomSample2D.csv"));
    	
    	Dataset data = FileHandler.loadDataset(new File("input/mostProbableCertainSet.csv"), 1, ",");
        int k = 2;
        KMeans km = new KMeans(k);
        Dataset[] clusters = km.cluster(data);
        for (int i=0; i< k; i++){
        	FileHandler.exportDataset(clusters[i], new File("output/cluster"+i+".txt"));
        }	
        
    	
    }
    
    	
    
    
    
    
   
    
    
}
