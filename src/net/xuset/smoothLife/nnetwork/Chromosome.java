package net.xuset.smoothLife.nnetwork;

import java.util.Arrays;

/**
 * The Chromosome is used by blobs to determine their size, color, and brains.
 * 
 * @author xuset
 * @since 1.0
 */
public final class Chromosome implements Cloneable {
	private static final int brainOffset = 4;

	/** The genes of the chromosome. */
	final double[] genes;

	private double fitness = 1.0;

	/**
	 * Create a new chromosome.
	 * 
	 * @param brainWeightCount the amount of doubles reserved for the use by the
	 * 		brain.
	 * @param randRange the range to randomly set the initial values of the
	 * 		chromosome to. Must be greater than or equal to zero.
	 * @throws IllegalArgumentException if randRange is < 0
	 */
	public Chromosome(int brainWeightCount, double randRange) {
		this(new double[brainOffset + brainWeightCount]);

		if (randRange < 0)
			throw new IllegalArgumentException("randRange must be greater than zero");

		for (int i = 0; i < genes.length; i++) {
			genes[i] = (Math.random() * randRange) - randRange / 2;
		}
	}

	/**
	 * Create a new chromosome with the given genes.
	 * The given genes are NOT copied so use with care.
	 * 
	 * @param genes the genes the chromosome should use
	 * @throws IllegalArgumentException if the supplied array is too small
	 * 		for the chromosome
	 */
	public Chromosome(double[] genes) {
		if (genes.length <= brainOffset)
			throw new IllegalArgumentException(
					"Array length must be greater than " + brainOffset);

		this.genes = genes;
	}

	/**
	 * Returns the fitness value of the chromosome.
	 * 
	 * @return the fitness of the chromosome
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Sets the fitness value of the chromosome.
	 * 
	 * @param newFitness the new fitness of the chromosome
	 */
	public void setFitness(double newFitness) {
		if (newFitness < 0)
			throw new IllegalArgumentException("newFitness must be greater than 0");
		fitness = newFitness;
	}

	/**
	 * Return the amount of doubles that are reserved for the brain.
	 * 
	 * @return the weight count for the brain
	 */
	public int getBrainWeightCount() {
		return genes.length - brainOffset;
	}

	/**
	 * Return a copy of the genes reserved for the brain.
	 * 
	 * @return the array of doubles used by the brain
	 */
	public double[] copyBrainGenes() {
		return Arrays.copyOfRange(genes, brainOffset, genes.length);
	}

	/**
	 * Return a copy of all the genes.
	 * 
	 * @return a copy of all the genes the chromosome has
	 */
	public double[] copyAllGenes() {
		return Arrays.copyOf(genes, genes.length);
	}

	/**
	 * Return the radius that is determined by a specific gene.
	 * 
	 * @return the radius determined by the chromosome
	 */
	public int getRadius() {
		return normalizeRange(genes[0], 10) + 8;
	}

	/**
	 * Return the red component of rgb determined by a specific gene.
	 * @return the red color determined by the chromosome
	 */
	public int getColorRed() {
		return normalizeRange(genes[1], 120);
	}

	/**
	 * Return the green component of rgb determined by a specific gene.
	 * @return the green color determined by the chromosome
	 */
	public int getColorGreen() {
		return normalizeRange(genes[2], 200);
	}

	/**
	 * Return the blue component of rgb determined by a specific gene.
	 * @return the blue color determined by the chromosome
	 */
	public int getColorBlue() {
		return normalizeRange(genes[3], 220) + 35;
	}

	@Override
	public Chromosome clone() {
		Chromosome chrm = new Chromosome(copyAllGenes());
		chrm.fitness = fitness;
		return chrm;
	}

	private int normalizeRange(double gene, int range) {
		return (int) Math.abs((gene * range) % range);
	}
}
