package net.xuset.smoothLife.nnetwork;

/**
 * The layer of neurons for the brain. This class is apart of the artificial
 * neural network.
 * 
 * @author xuset
 * @since 1.0
 * @see Brain
 */
final class Layer {

	/** The input count for the layer. */
	final int inputs;

	/** The output count for the layer. */
	final int outputs;

	/** The weight count for the layer. */
	final int weightCount;

	/** The neurons for this layer. */
	final Neuron neurons[];

	/**
	 * Instantiate a new layer.
	 * 
	 * @param inputs the input count for the layer
	 * @param outputs the output count (number of neurons) for the layer
	 * @param sigmoid should the outputs be applied to the sigmoid function
	 */
	Layer(int inputs, int outputs, boolean sigmoid) {
		this.inputs = inputs;
		this.outputs = outputs;
		neurons = new Neuron[outputs];
		int totalWeights = 0;
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(inputs, sigmoid);
			totalWeights += neurons[i].weights.length;
		}
		weightCount = totalWeights;
	}

	/**
	 * Instantiate a new layer.
	 * 
	 * @param inputs the input count for the layer
	 * @param outputs the output count (number of neurons) for the layer\
	 */
	Layer(int inputs, int outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
		neurons = new Neuron[outputs];
		int totalWeights = 0;
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(inputs, false);
			totalWeights += neurons[i].weights.length;
		}
		weightCount = totalWeights;
	}

	/**
	 * Stimulate the layer with the given input.
	 * 
	 * @param input the input to stimulate the layer with
	 * @return the output from the layer
	 */
	double[] stimulate(double input[]) {
		double output[] = new double[neurons.length];
		for (int i = 0; i < neurons.length; i++) {
			Neuron neuron = neurons[i];
			output[i] = neuron.stimulate(input);
		}
		return output;
	}

	/**
	 * Replace the layer's current weights with the given weights.
	 * 
	 * @param offset the index of the array to start at.
	 * @param weights the array of weights to use
	 * @return the index of the array that this method stop at
	 */
	int putWeights(int offset, double[] weights) {
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].weights.length; j++) {
				neurons[i].weights[j] = weights[offset++];
			}
		}
		return offset;
	}
}
