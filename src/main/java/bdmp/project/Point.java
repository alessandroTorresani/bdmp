package bdmp.project;

public class Point {
	private double prob;
	private String id;
	
	public Point(double prob, String id) {
		this.setProb(prob);
		this.setId(id);
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
