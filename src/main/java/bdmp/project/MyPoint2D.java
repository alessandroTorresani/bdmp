package bdmp.project;


public class MyPoint2D extends Point {
	double x1;
	double x2;
	public MyPoint2D(double prob, String id, double x1, double x2) {
		super(prob, id);
		this.x1 = x1;
		this.x2 = x2;
	}
	
	public String toString(){
		String result = "[ " + getId() + ", " + x1 + ", " + x2 + ", " + getProb() + " ]";
		return result;
	}
	
}
