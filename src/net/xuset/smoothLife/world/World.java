package net.xuset.smoothLife.world;

import java.util.ArrayList;

import net.xuset.smoothLife.nnetwork.Brain;
import net.xuset.smoothLife.nnetwork.GenePool;
import net.xuset.smoothLife.world.WorldFactory.SpecieInfo;
import net.xuset.smoothLife.world.WorldFactory.WorldInfo;

public class World {
	
	private final ArrayList<Specie> species = new ArrayList<Specie>(2);
	private final int worldWidth, worldHeight;
	
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
	
	public int getSpeciesCount() {
		return species.size();
	}
	
	public Specie getSpecie(int index) {
		return species.get(index);
	}
	
	public int getWidth() {
		return worldWidth;
	}
	
	public int getHeight() {
		return worldHeight;
	}
	
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
	
	/*static final class BlobCreateInfo {
		final double x, y, angle;
		final int age;
		final boolean useLocation;
		final Chromosome currentChromo, oldChromo;
		
		BlobCreateInfo(double x, double y, double angle, int age, boolean useLocation,
				Chromosome currentChromo, Chromosome oldChromo) {
			
			this.x = x;
			this.y = y;
			this.angle = angle;
			this.useLocation = useLocation;
			this.age = age;
			this.currentChromo = currentChromo;
			this.oldChromo = oldChromo;
		}
	}*/
	
	
}
