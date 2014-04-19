package net.xuset.smoothLife.world;

import java.util.ArrayList;



public class BlobFinder {
	private enum TypePick { SIMILAR, UNSIMILAR, ALL }
	private final ArrayList<Specie> species;
	
	public BlobFinder(ArrayList<Specie> species) {
		this.species = species;
	}
	
	public Blob getColliding(Body body) {
		for (int i = 0; i < species.size(); i++) {
			Specie sp = species.get(i);
			
			for (int j = 0; j < sp.getBlobCount(); j++) {
				Blob b = sp.getBlob(j);
				
				if (b.getBody() == body)
					continue;
				
				if (body.isColliding(b.getBody()))
					return b;
			}
		}
		
		return null;
	}
	
	public Blob getClosestSimilar(Blob blob) {
		return getClosest(blob, TypePick.SIMILAR);
	}
	
	public Blob getClosestUnSimilar(Blob blob) {
		return getClosest(blob, TypePick.UNSIMILAR);
	}
	
	public Blob getClosest(Blob blob) {
		return getClosest(blob, TypePick.ALL);
	}
	
	private Blob getClosest(Blob blob, TypePick typePick) {
		Blob closest = null;
		double closestDist = Double.MAX_VALUE;
		
		for (int i = 0; i < species.size(); i++) {
			Specie sp = species.get(i);
			
			if (
					( typePick == TypePick.UNSIMILAR &&
					sp.getSpecieId() == blob.getSpecieId() ) ||
					( typePick == TypePick.SIMILAR &&
					sp.getSpecieId() != blob.getSpecieId()) ) {
				
				continue;
			}
			
			for (int j = 0; j < sp.getBlobCount(); j++) {
				Blob b = sp.getBlob(j);
				
				if (b == blob)
					continue;
				
				double x1 = b.getBody().getX(), y1 = b.getBody().getY();
				double x2 = blob.getBody().getX(), y2 = blob.getBody().getY();
				double dx = x1 - x2, dy = y1 - y2;
				
				double dist = dx * dx + dy * dy;
				double radii = b.getBody().getRadius() + blob.getBody().getRadius();
				dist = dist - radii * radii;
				if (dist < closestDist) {
					closestDist = dist;
					closest = b;
				}
			}
		}
		
		return closest;
	}
}
