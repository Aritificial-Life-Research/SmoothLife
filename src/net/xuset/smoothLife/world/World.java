package net.xuset.smoothLife.world;

import java.util.ArrayList;

import net.xuset.smoothLife.nnetwork.Brain;
import net.xuset.smoothLife.nnetwork.GenePool;
import net.xuset.smoothLife.world.WorldFactory.SpecieInfo;
import net.xuset.smoothLife.world.WorldFactory.WorldInfo;

/**
 * The world object is responsible for instantiating and holding all the
 * species.
 * 
 * @author xuset
 * @since 1.0
 */
public class World {

	private final ArrayList<Specie> species = new ArrayList<Specie>(2);
	private final int worldWidth, worldHeight;

	/**
	 * Create a new world based on the given world info.
	 * 
	 * @param worldInfo defines info about the world to create
	 */
	World(WorldInfo worldInfo) {
		worldWidth = worldInfo.worldWidth;
		worldHeight = worldInfo.worldHeight;

		BlobFinder blobFinder = new BlobFinder(species);
		SpecieInfo[] speciesInfo = worldInfo.speciesInfos;

		for (int i = 0; i < speciesInfo.length; i++) {
			SpecieInfo info = speciesInfo[i];
			int weightCount = Brain.getWeightCount(info.neuronLayout);
			GenePool genePool = new GenePool(weightCount);
			BlobSpawner spawner = new BlobSpawner(genePool, blobFinder, worldWidth,
					worldHeight, info.neuronLayout);
			species.add(new Specie(info.specieId, spawner, info.isPrey,
					info.initBlobCount));
		}
	}

	/**
	 * Get the total number of species.
	 * 
	 * @return the species count
	 */
	public int getSpeciesCount() {
		return species.size();
	}

	/**
	 * Return the species at the specified index.
	 * 
	 * @param index the index of the species to return. The index should be
	 * >= 0 and < getSpeciesCount().
	 * @return the species object at the specified index
	 */
	public Specie getSpecie(int index) {
		return species.get(index);
	}

	/**
	 * Get the width of the world.
	 * 
	 * @return the world's width
	 */
	public int getWidth() {
		return worldWidth;
	}

	/**
	 * Get the height of the world.
	 * 
	 * @return the world's height
	 */
	public int getHeight() {
		return worldHeight;
	}

	/**
	 * Updates the blobs.
	 * The preupdateBlobs method is called on the species then the updateBlobs
	 * method is called on the species.
	 */
	public void updateBlobs() {
		for (int i = 0; i < species.size(); i++) {
			Specie sp = species.get(i);
			sp.preupdateBlobs();
		}

		for (int i = 0; i < species.size(); i++) {
			Specie sp = species.get(i);
			sp.updateBlobs();
		}
	}
}
