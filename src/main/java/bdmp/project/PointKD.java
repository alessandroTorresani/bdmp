package bdmp.project;

public class PointKD extends Point {
	private double[] dimensions;
	private int dimension;

	public PointKD(String id, int dimension, double[] dimensions,double prob) {
		super(prob, id);
		this.setDimension(dimension);
		this.setDimensions(dimensions);
	}

	public double[] getDimensions() {
		return dimensions;
	}

	public void setDimensions(double[] dimensions) {
		this.dimensions = dimensions;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	
}
