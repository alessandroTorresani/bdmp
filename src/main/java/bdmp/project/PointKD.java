package bdmp.project;

public class PointKD {
	private String id;
	private double[] dimensions;
	private int dimension;
	private double prob;
	

	public PointKD(String id, int dimension, double[] dimensions,double prob) {
		this.setId(id);
		this.setProb(prob);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}
	
}
