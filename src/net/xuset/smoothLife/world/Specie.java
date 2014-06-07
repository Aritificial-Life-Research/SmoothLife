package net.xuset.smoothLife.world;

import java.util.ArrayList;
import java.util.List;

import net.xuset.smoothLife.nnetwork.GenePool;


public class Specie {
	private static final int ticksPerSpawn = 50;
	
	private final List<Blob> deadBlobs = new ArrayList<Blob>();
	private final List<Blob> liveBlobs = new ArrayList<Blob>();
	private final BlobSpawner blobSpawner;
	private final long specieId;
	private final boolean isPrey;
	
	private int lastSpawnTick = 0;
	
	Specie(long specieId, BlobSpawner blobSpawner, boolean isPrey, int initBlobCount) {
		this.specieId = specieId;
		this.blobSpawner = blobSpawner;
		this.isPrey = isPrey;
		
		for (int i = 0; i < initBlobCount; i++)
			liveBlobs.add(blobSpawner.create(isPrey, specieId));
	}
	
	public boolean isPrey() {
		return isPrey;
	}
	
	public int getBlobCount() {
		return liveBlobs.size();
	}
	
	public Blob getBlob(int index) {
		return liveBlobs.get(index);
	}
	
	public double getSummedFitness() {
		return blobSpawner.getSummedFitness();
	}
	
	List<Blob> getAllBlobs() {
		List<Blob> list = new ArrayList<Blob>();
		list.addAll(liveBlobs);
		list.addAll(deadBlobs);
		return list;
	}
	
	GenePool cloneGenePool() {
		return blobSpawner.cloneGenePool();
	}
	
	int[] cloneNeuronLayout() {
		return blobSpawner.cloneNeuronLayout();
	}
	
	long getSpecieId() {
		return specieId;
	}
	
	void preupdateBlobs() {
		for (int i = 0; i < liveBlobs.size(); i++) {
			Blob b = liveBlobs.get(i);
			b.preupdate();
			
			if (b.getEnergy() <= 0.0) {
				deadBlobs.add(b);
				liveBlobs.remove(i);
				i--;
			}
		}
	}
	
	void updateBlobs() {
		for (int i = 0; i < liveBlobs.size(); i++) {
			Blob b = liveBlobs.get(i);
			b.update();
		}
		
		if (lastSpawnTick == ticksPerSpawn) {
			spawnBlob();
			lastSpawnTick = 0;
		}
		
		lastSpawnTick++;
	}
	
	private void spawnBlob() {
		if (deadBlobs.isEmpty())
			return;
		
		Blob b = deadBlobs.remove(0);
		blobSpawner.respawn(b);
		liveBlobs.add(b);
	}
}
