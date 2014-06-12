package net.xuset.smoothLife.world;

import net.xuset.smoothLife.nnetwork.Chromosome;
import net.xuset.smoothLife.nnetwork.GenePool;
import net.xuset.smoothLife.nnetwork.GenePool.PoolItemKey;

/**
 * Holds the chromosome for the blob.
 * When it comes time to swap out the blob's old chromsome for a new one,
 * this class handles swaping the chromosome out of the genepool.
 * 
 * @author xuset
 * @since 1.0
 */
class ChromosomeHolder{

	private final GenePool genePool;
	private final PoolItemKey key;
	private Chromosome chromosome;

	/**
	 * Instantiate a new chromosome holder
	 * @param genePool the genepool to the key belongs to
	 * @param key the key for the chromosome in the genepool
	 * @param chromosome current chromosome of the chromosome holder
	 */
	ChromosomeHolder(GenePool genePool, PoolItemKey key, Chromosome chromosome) {
		this.genePool = genePool;
		this.key = key;
		this.chromosome = chromosome;
	}

	/**
	 * Gets the current chromosome
	 * @return the current chromosome
	 */
	Chromosome getChromosome() {
		return chromosome;
	}

	/**
	 * Replace the current chromosome with the new one.
	 * This will place the current chromosome into the gene pool
	 * @param newChromosome the new chromosome
	 */
	void replaceChromosome(Chromosome newChromosome) {
		genePool.setChromosome(key, chromosome);
		chromosome = newChromosome;
	}

	/**
	 * Create and return a clone of the chromosome that is in the genepool
	 * @return a clone of the chromosome in the genepool.
	 */
	Chromosome cloneChromoInGenePool() {
		return genePool.getChromosome(key).clone();
	}
}
