package net.xuset.smoothLife.nnetwork;

import java.util.Arrays;

/**
 * Creates chromsomes by utilizing the process of a genetic algorithm.
 * 
 * @author xuset
 * @since 1.0
 */
public final class GAlg {
	private final static double mutationRate = 0.1;
	private final static double mutationStep = 0.1;
	private final static double crossOverRate = 0.7;

	/**
	 * Creates a new chromosome while utilizing the given gene pool. The
	 * genetic algorithm is used to create the chromosome
	 * 
	 * @param pool the genepool to create the chromosome from.
	 * @return a new chromosome
	 */
	public Chromosome createNewChromo(GenePool pool) {
		double totalFitness = pool.getSummedFitness();
		Chromosome chromoA = select(pool, totalFitness);
		Chromosome chromoB = select(pool, totalFitness);

		double[] result = crossOver(chromoA.genes, chromoB.genes);
		mutate(result);
		return new Chromosome(result);
	}

	private double[] crossOver(double[] weightA, double[] weightB) {
		validateParents(weightA, weightB);

		double[] result = Arrays.copyOf(weightA, weightA.length);

		if (Math.random() > crossOverRate)
			return result;

		int breakup = (int) (result.length * Math.random());
		double half = Math.random();
		int start = half < 0.5 ? 0 : breakup;
		int end = half < 0.5 ? breakup + 1 : result.length;

		for (int i = start; i < end; i++)
			result[i] = weightB[i];

		return result;
	}

	private void mutate(double[] weights) {
		for (int i = 0; i < weights.length; i++) {
			if (Math.random() < mutationRate) {
				double randSign = Math.signum(Math.random() - Math.random());
				weights[i] += randSign * mutationStep;
			}
		}
	}

	private Chromosome select(GenePool pool, double totalFitness) {
		double fitness = (Math.random() * totalFitness);
		double summedFitness = 0.0;

		for (int i = 0; i < pool.getChromosomeCount(); i++) {
			Chromosome c = pool.getChromosome(i);
			summedFitness += c.getFitness();
			if (summedFitness >= fitness)
				return c;
		}
		return pool.getChromosome(pool.getChromosomeCount() - 1);

	}

	private void validateParents(double[] w1, double[] w2) {
		if (w1.length != w2.length)
			throw new RuntimeException("Population has mixed neural weight counts!");
	}
}
