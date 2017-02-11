package bdmp.project;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;


public class App 
{
	static final String appName = "bigDataProject";
	static final String master ="local[2]";
	
    public static void main( String[] args ) throws IOException
    {
    		Utilities.initializeFolders(); // Initialize input and output folders
    		
    		// Spark stuff
    		/*SparkConf conf = new SparkConf();
    		conf.setAppName(appName).setMaster(master);
    		JavaSparkContext sc = new JavaSparkContext(conf); // for RDD
    		SparkSession spark = SparkSession.builder().appName(appName).master(master).getOrCreate(); // for dataset
    		
    		// Simplified version of the sampler that runs in parallel
    		SamplerParallel sp = new SamplerParallel(2, sc, spark);
    		
    		sp.simpleSample(200, Utilities.getRandomMeanVector(2, 0, 100), false);
    		sc.stop();
    		sc.close();
    		*/
    		Prompt.sampleChoice(); 	
    }  
}
