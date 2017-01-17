package bdmp.project;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sampler2D {		
	
	public void simpleSample(int num, int min, int max) throws FileNotFoundException{ // num is number of samples and the max number of points for each identifier
		int counter = 0;
		String id;
		
		List<MyPoint2D> points = new ArrayList<MyPoint2D>();
		
		
		while (counter < num){
			id = UUID.randomUUID().toString();
			MyPoint2D p;
			for(int i = 0; i < 2; i ++){
				p = new MyPoint2D(0.5, id, min + Math.random() * (max - min), min + Math.random() * (max - min));
				points.add(p);
			}
			counter++;
		}
		
		PrintWriter pw = new PrintWriter(new File("input/sample1.csv"));
		StringBuilder sb = new StringBuilder();
		while(!points.isEmpty()){
			MyPoint2D p = points.remove(0);
			sb.append(p.getId());
			sb.append(",");
			sb.append(p.x1);
			sb.append(",");
			sb.append(p.x2);
			sb.append(",");
			sb.append(p.getProb());
			sb.append("\n");
		}
		pw.write(sb.toString());
		pw.close();
	}
	
	public void Sample2(int num, int uncertainPoints,  int min, int max) throws FileNotFoundException{ // num is number of samples and the max number of points for each identifier
		int counter = 0;
		String id;
		List<MyPoint2D> points = new ArrayList<MyPoint2D>();
		
		while (counter < num){
			id = UUID.randomUUID().toString();
			MyPoint2D p;
			double totProb = 1;
			int unCounter = 0;
			double tempProb;
			boolean cycle = true;
			while (cycle){
				tempProb = Math.random();
				if (tempProb < totProb || tempProb == totProb){
					p = new MyPoint2D(tempProb, id, min + Math.random() * (max - min), min + Math.random() * (max - min));
					totProb -= tempProb;
				} else {
					
				}
				
				p = new MyPoint2D(Math.random(), id, min + Math.random() * (max - min), min + Math.random() * (max - min));
			}
		}
		
	}

}
