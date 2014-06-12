package net.xuset.smoothLife.nnetwork;

/**
 * Models the structure of an artificial neural network. The brain maps a set
 * of inputs to outputs. How the brain maps the inputs and outputs can be
 * changed by setting the brain's weights to different values. To map a set of
 * input values to output values call the stimulate method.
 * 
 * @author xuset
 * @since 1.0
 */
public final class Brain {
	private final int inputCount;
	private final int outputCount;
	private final int weightCount;

	private final Layer[] layers;

	/**
	 * Instantiate a new brain with the given neuron layout.
	 * 
	 * @param neuronLayout the amount of neurons per layer as an array
	 * @throws IllegalArgumentException
	 * 		If the supplied array length is less than two or
	 * 		if the one of the array values is less than 0
	 */
	public Brain(int neuronLayout[]) {

		/*
		 * make sure there is at least two layers.
		 * one for an input layer, the other for an output layer
		 */
		if (neuronLayout.length < 2)
			throw new IllegalArgumentException("The arrays must have a length greater " +
					" than or equal to 2");

		inputCount = neuronLayout[0];
		outputCount = neuronLayout[neuronLayout.length - 1];
		layers = new Layer[neuronLayout.length - 1];
		int totalWeights = 0;
		for (int i = 0; i < layers.length; i++) {

			//check for valid neuron count
			if (neuronLayout[i] < 1 || neuronLayout[i + 1] < 1)
				throw new IllegalArgumentException("Array values must be greater than 0");

			layers[i] = new Layer(neuronLayout[i], neuronLayout[i+1]);

			totalWeights += layers[i].weightCount;
		}
		weightCount = totalWeights;
	}

	/**
	 * Sets the brain's weights to the given weights.
	 * 
	 * @param weights the new weights of the brain
	 * @throws IllegalArgumentException if the supplied array length does
	 * 		not equal the amount of weights the brain has
	 */
	public void putWeights(double weights[]) {

		//make sure the supplied weight count is equal to the brain's weight count
		if (weights.length != weightCount)
			throw new IllegalArgumentException("weight counts must equal");

		int offset = 0;
		for (int i = 0; i < layers.length; i++) {
			offset = layers[i].putWeights(offset, weights);
		}
	}

	/**
	 * Returns a copy of the brain's weights.
	 * 
	 * @return a copy of the brain's weights
	 */
	public double[] getWeights() {
		double[] totalWeights = new double[weightCount];
		int totalCount = 0;
		for (int i = 0; i < layers.length; i++) {
			Neuron neurons[] = layers[i].neurons;
			for (int j = 0; j < neurons.length; j++) {
				double weights[] = neurons[j].weights;
				for (int k = 0; k < weights.length; k++) {
					totalWeights[totalCount++] = weights[k];
				}
			}
		}
		return totalWeights;
	}

	/**
	 * Stimulates the brain by mapping the input to an output and returning
	 * the output.
	 * 
	 * @param input the input of the brain
	 * @return the output of the brain
	 * @throws IllegalArgumentException if the supplied input array length
	 * 		is not equal to the expected input count
	 */
	public double[] stimulate(double input[]) {

		//make sure the input array equals the input count of the brain
		if (input.length != inputCount)
			throw new IllegalArgumentException("Input count does not equal");

		double layerIn[] = input;
		for (int i = 0; i < layers.length; i++) {
			Layer layer = layers[i];
			layerIn = layer.stimulate(layerIn);
		}
		return layerIn;
	}

	/**
	 * Gets the amount of input neurons. This method should be used to
	 * determine to size of the input array when stimulating the brain.
	 * 
	 * @return the expected input count of the brain
	 */
	public int getInputCount() { return inputCount; }

	/**
	 * The output count of the brain. The returned array's length of the
	 * stimulate method will be equal to this method.
	 * 
	 * @return the output count
	 */
	public int getOuputCount() { return outputCount; }

	/**
	 * Return the total amount of weights used by the brain.
	 * 
	 * @return the weight count of the brain
	 */
	public int getWeightCount() { return weightCount; }

	/**
	 * Determines the amount of weights a brain would use with the given
	 * neuron layout.
	 * 
	 * @param neuronLayout the neuron layout in question
	 * @return the amount of weights a brain would use with the given neuron
	 * 			layout
	 */
	public static int getWeightCount(int[] neuronLayout) {
		int count = 0;
		for (int i = 0; i < neuronLayout.length - 1; i++) {
			count += (neuronLayout[i] + 1) * neuronLayout[i + 1];
		}
		return count;
	}

}
