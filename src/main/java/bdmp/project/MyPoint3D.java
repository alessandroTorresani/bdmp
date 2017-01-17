package bdmp.project;

public class MyPoint3D extends Point {
	double x1;
	double x2;
	double x3;
	public MyPoint3D(double prob, String id, double x1, double x2, double x3) {
		super(prob, id);
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
	}
}
