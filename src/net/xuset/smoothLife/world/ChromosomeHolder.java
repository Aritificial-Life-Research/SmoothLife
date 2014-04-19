package net.xuset.smoothLife.world;

import net.xuset.smoothLife.nnetwork.Chromosome;
import net.xuset.smoothLife.nnetwork.GenePool;
import net.xuset.smoothLife.nnetwork.GenePool.PoolItemKey;

class ChromosomeHolder{
	private final GenePool genePool;
	private final PoolItemKey key;
	private Chromosome chromosome;
	
	ChromosomeHolder(GenePool genePool, PoolItemKey key, Chromosome chromosome) {
		this.genePool = genePool;
		this.key = key;
		this.chromosome = chromosome;
	}
	
	Chromosome getChromosome() {
		return chromosome;
	}
	
	void replaceChromosome(Chromosome newChromosome) {
		genePool.setChromosome(key, chromosome);
		chromosome = newChromosome;
	}
	
	Chromosome cloneChromoInGenePool() {
		return genePool.getChromosome(key).clone();
	}
}
