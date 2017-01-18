package bdmp.project;

public class MyPoint3D extends Point {
	double x1;
	double x2;
	double x3;
	public MyPoint3D(String id, double x1, double x2, double x3, double prob) {
		super(prob, id);
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
	}
	
	public String toString(){
		String result = "[ " + getId() + ", " + x1 + ", " + x2 + ", " + x3 + ", " + getProb() + " ]";
		return result;
	}
	
}
