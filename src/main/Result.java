package main;

/**
 * This class creates a result based on how far the ball has been moved and how far the creature has moved
 */
public class Result implements Comparable<Result> {
	public final Individual individual;
	public float score;
	public float normalised;

	public Result(Individual individual, float distance) {
		this.individual = individual;
		if(distance < 0) {
			this.score = 0;
		} else {
			this.score = distance;
		}
	}

	public int compareTo(Result other) {
		double difference = score - other.score;

		if (difference < 0) {
			return 1;
		} else if (difference > 0) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return score + "";
	}

}
