package bdmp.project;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
      
    	Sampler2D sampler2D = new Sampler2D(); //2D sampler
    	Sampler3D sampler3D = new Sampler3D(); //3D sampler
    	
    	//sampler2D.simpleSample(500,-100,+100, true);
    	//sampler2D.genericSample(500,10,-100,+100);
    	
    	double[] means = {50,10};
    	double[][] covariances = {
    			{1, 0},
    			{0, 1}
    	};
    	sampler2D.gaussianSample(100, means, covariances);
    	
    	
    	//sampler3D.simpleSample(500,-100 , 100, false);
    	//sampler3D.genericSample(500,10, -100 , 100);
    	
    	
    	double[] means3 = {50,50,50};
    	double[][] covariances3 = {
    			{1, 0, 0},
    			{0, 1, 0},
    			{0, 0, 1}
    	};
    	//sampler3D.gaussianSample(10, means3, covariances3);
    	
    	
    	/*double[] res;
    	MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means, covariances);
    	int i = 0;
    	res = mnd.sample();
    	System.out.println("Dimension: " + res.length);
    	double prob = mnd.density(res);
    	double tot = 0;
    	int counter = 0;
    	
    	while (tot <= 1){
    		System.out.println("tot: " + tot);
    		tot = tot + prob;
    		counter++;
    		System.out.println(counter);
    	}*/
    	/*for (int x = 0; x < res.length; x++){
    		System.out.println(res[x]);
    		System.out.println("Prob: " + mnd.density(res));
    	}*/
    
    	
    	
    	
    	
    	
    	// Working k-mean algorithm
       /* Dataset data = FileHandler.loadDataset(new File("input/aa.csv"), 2, ",");
        int k = 3;
        KMeans km = new KMeans(k);
        Dataset[] clusters = km.cluster(data);
        for (int i=0; i< k; i++){
        	System.out.println(clusters[i]+"\n");
        }
        */
        
    }
}
