package net.xuset.smoothLife.nnetwork;

/**
 * This class is apart of the artificial neural network.
 * 
 * @author xuset
 * @since 1.0
 * @see Brain
 * @see Layer
 */
final class Neuron {
	/** Determines if the output should be applied to the sigmoid function. */
	final boolean sigmoidOutput;

	/** The weights for the neuron. */
	final double weights[];

	/**
	 * Instantiate a new neuron with the given amount of inputs.
	 * 
	 * @param inputs the input count for the neuron (weight count)
	 */
	Neuron(int inputs) {
		this(inputs, false);
	}

	/**
	 * Instantiate a new neuron with the given amount of inputs.
	 * 
	 * @param inputs the input count for the neuron (weight count)
	 * @param sigmoidOutput determines if the output should be applied to
	 * 		the sigmoid function
	 */
	Neuron(int inputs, boolean sigmoidOutput) {
		weights = new double[inputs + 1];
		this.sigmoidOutput = sigmoidOutput;
		randomizeWeights();
	}

	/**
	 * Randomizes the weights of the neuron.
	 */
	void randomizeWeights() {
		weights[0] = -Math.random(); // the bias
		for (int i = 1; i < weights.length; i++)
			weights[i] = Math.random();
	}

	/**
	 * Stimulates the neuron with the given inputs.
	 * 
	 * @param inputs the inputs for the neuron
	 * @return the output of the neuron
	 */
	double stimulate(double inputs[]) {
		double sum = weights[0];
		for (int i = 1; i < weights.length; i++) {
			sum += weights[i] * inputs[i-1];
		}
		if (sigmoidOutput) {
			return 1 / (1 + Math.exp(-sum));
		} else {
			if (sum > 0)
				return 1.0;
			else
				return 0.0;
		}
	}
}
