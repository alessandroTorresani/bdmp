package bdmp.project;

import java.io.File;
import java.io.IOException;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
      
    	Sampler2D sampler = new Sampler2D(); //2D sampler
    	sampler.simpleSample(500,100,-100);
    	
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
