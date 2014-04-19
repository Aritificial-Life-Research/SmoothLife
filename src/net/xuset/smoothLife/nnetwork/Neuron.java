package net.xuset.smoothLife.nnetwork;

final class Neuron {
	final boolean sigmoidOutput;
	
	final double weights[];
	
	Neuron(int inputs) {
		this(inputs, false);
	}
	
	Neuron(int inputs, boolean sigmoidOutput) {
		weights = new double[inputs + 1];
		this.sigmoidOutput = sigmoidOutput;
		randomizeWeights();
	}
	
	void randomizeWeights() {
		weights[0] = -Math.random(); //-Math.random() * (weights.length - 1); // the bias
		for (int i = 1; i < weights.length; i++)
			weights[i] = Math.random();
	}
	
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
