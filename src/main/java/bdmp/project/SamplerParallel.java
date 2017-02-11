package bdmp.project;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.random.RandomRDDs;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

public class SamplerParallel  {
	private static int dimension; //watch out, class variable
	JavaSparkContext sc;
	SparkSession spark;
	
	public SamplerParallel(int dimension, JavaSparkContext sc, SparkSession spark){
		this.dimension = dimension;
		this.sc = sc;
		this.spark = spark;
	}
	
	static private PairFunction<Double, String, Double> mapDoubleToPair = new PairFunction<Double, String, Double>() {
		
		int index = 0;
		public Tuple2<String, Double> call(Double t) throws Exception {
			return new Tuple2<String, Double>(""+index++, t);

		}
	};
	
	static private PairFunction <Tuple2<String,Iterable<Iterable<Double>>>, String, Iterable<Iterable<Double>>> addProbabilityDiff = new PairFunction<Tuple2<String,Iterable<Iterable<Double>>>, String, Iterable<Iterable<Double>>>() {
		
		public Tuple2<String, Iterable<Iterable<Double>>> call(Tuple2<String, Iterable<Iterable<Double>>> t)
				throws Exception {
			List<Iterable<Double>> itList = new ArrayList<Iterable<Double>>();
			List<Double> list1 = new ArrayList<Double>();
			List<Double> list2 = new ArrayList<Double>();
			Iterator<Iterable<Double>> it = t._2().iterator();
			while(it.hasNext()){
				itList.add(it.next()); // add two iterators of the lists
			}
			Iterator<Double> it1 = itList.get(0).iterator(); // get the iterators of the list 1
			Iterator<Double> it2 = itList.get(1).iterator(); // get the iterators of the list 2
			
			while(it1.hasNext()){
				list1.add(it1.next());
			}
			list1.add(0.8); // add probability
			
			while(it2.hasNext()){
				list2.add(it2.next());
			}
			list2.add(0.2);
			List<Iterable<Double>> res = new ArrayList<>();
			res.add(list1);
			res.add(list2);
			
			return new Tuple2<String, Iterable<Iterable<Double>>>(t._1, res);
		}
	};
	
	static private PairFunction <Tuple2<String,Iterable<Iterable<Double>>>, String, Iterable<Iterable<Double>>> addProbability = new PairFunction<Tuple2<String,Iterable<Iterable<Double>>>, String, Iterable<Iterable<Double>>>() {

		public Tuple2<String, Iterable<Iterable<Double>>> call(Tuple2<String, Iterable<Iterable<Double>>> t)
				throws Exception {
			List<Iterable<Double>> itList = new ArrayList<Iterable<Double>>();
			List<Double> list1 = new ArrayList<Double>();
			List<Double> list2 = new ArrayList<Double>();
			Iterator<Iterable<Double>> it = t._2().iterator();
			while(it.hasNext()){
				itList.add(it.next()); // add two iterators of the lists
			}
			Iterator<Double> it1 = itList.get(0).iterator(); // get the iterators of the list 1
			Iterator<Double> it2 = itList.get(1).iterator(); // get the iterators of the list 2

			while(it1.hasNext()){
				list1.add(it1.next());
			}
			list1.add(0.5); // add probability

			while(it2.hasNext()){
				list2.add(it2.next());
			}
			list2.add(0.5);
			List<Iterable<Double>> res = new ArrayList<>();
			res.add(list1);
			res.add(list2);

			return new Tuple2<String, Iterable<Iterable<Double>>>(t._1, res);
		}
	};
	
	static private FlatMapFunction <Tuple2<String,Iterable<Iterable<Double>>>, String> prepareToWrite = new FlatMapFunction<Tuple2<String,Iterable<Iterable<Double>>>, String>() {

		public Iterator<String> call(Tuple2<String, Iterable<Iterable<Double>>> t) throws Exception {
			// TODO Auto-generated method stub
			
			List<Iterable<Double>> itList = new ArrayList<Iterable<Double>>();
			Iterator<Iterable<Double>> it = t._2().iterator();
			StringBuilder sb = new StringBuilder();
			
			while(it.hasNext()){
				itList.add(it.next()); // add two iterators of the lists
			}
			Iterator<Double> it1 = itList.get(0).iterator(); // get the iterators of the list 1
			Iterator<Double> it2 = itList.get(1).iterator(); // get the iterators of the list 2
			
			//add headers
			//sb.append("id,d1,d2,p\n");
			sb.append(t._1);
			sb.append(",");
			int index = 0;
			
			while(it1.hasNext()){
				sb.append(it1.next());
				if(index < dimension){
					sb.append(",");
				}
				index++;
			}
			
			sb.append("\n");
			sb.append(t._1);
			sb.append(",");
			index = 0;
			while(it2.hasNext()){
				sb.append(it2.next());
				if(index < dimension){
					sb.append(",");
				}
				index++;
			}
			//sb.append("\n");
			List<String> l= new ArrayList<>();
			l.add(sb.toString());
			
			return l.iterator();
		}
	};
	
	
	public void simpleSample(int numberOfSamples, double[] means,   boolean highDifference) throws FileNotFoundException{
		JavaDoubleRDD dr1 = RandomRDDs.poissonJavaRDD(sc, means[0], numberOfSamples, dimension); // (Sparkcontext ,mean ,numberOfSamples)
		JavaDoubleRDD dr2 = RandomRDDs.poissonJavaRDD(sc, means[1], numberOfSamples, dimension);
		
		/*
		 * Map each elements to a key-value pair. (d1,d2,d3,d4...) -> (1,d1), (2,d2), (3,d3) ... for each worker
		 * THIS SOLUTIONS WORKS ONLY FOR 2 ELEMENTS. I need to modify it
		 */
		JavaPairRDD<String, Double> pair1 = dr1.mapToPair(mapDoubleToPair);
		
		//System.out.println("pair1");
		//System.out.println(pair1.collect());
		JavaPairRDD<String, Double> pair2 = dr2.mapToPair(mapDoubleToPair);
		//System.out.println("pair2");
		//System.out.println(pair2.collect());
		
		/*
		 * Group the work of each worker. Shape [(key1,[[x1,x2],[y1,y2]], (key2,[[x1,x2],[y1,y2]])...]
		 */
		JavaPairRDD<String, Iterable<Double>> pairGrouped1 = pair1.groupByKey();
		JavaPairRDD<String, Iterable<Double>> pairGrouped2 = pair2.groupByKey();
		JavaPairRDD<String, Iterable<Double>> pairgrouped = pairGrouped1.union(pairGrouped2); //Shape (KEY, [[x1,x2],[y1,y2]])
		//System.out.println(pairgrouped.groupByKey().collect()); //
		
		/*
		 * GOAL: (KEY, [[x1,x2,p1],[y1,y2,p2]]) 
		 */
		JavaPairRDD<String, Iterable<Iterable<Double>>> unionGrouped = pairgrouped.groupByKey();
		//System.out.println(unionGrouped.collect());
		
		JavaPairRDD<String, Iterable<Iterable<Double>>> unionGroupedWithProbability;
		if (highDifference){ 
			unionGroupedWithProbability = unionGrouped.mapToPair(addProbabilityDiff);
		} else {
			unionGroupedWithProbability = unionGrouped.mapToPair(addProbability);
		}
		
		//System.out.println("Res:" );
		//System.out.println(uWithProb.collect());
		
		JavaRDD<String> output = unionGroupedWithProbability.flatMap(prepareToWrite);
		
		//headers
		StringBuilder sb = new StringBuilder();
		sb.append("identifier,");
		for(int i=1; i<dimension+1;i++){
			sb.append("d"+i+",");
		}
		sb.append("p");
		JavaRDD<String> headers = sc.parallelize(Arrays.asList(sb.toString())); 
		
		//System.out.println(output.collect());
		headers.union(output).repartition(1).saveAsTextFile(System.getProperty("user.home")+"/Documents/bdmpFiles/input/spark");
		//run a script that copy "part00000" file to input/simpleSampleParalle and delete spark folder
		Utilities.handleSparkOutput("SimpleSamplePar"+dimension+"D.csv");
		System.out.println("finished");
	}
	
	
}
