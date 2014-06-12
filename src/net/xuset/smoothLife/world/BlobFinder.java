package net.xuset.smoothLife.world;

import java.util.ArrayList;



/**
 * Used to find all the blobs in the world.
 * 
 * @author xuset
 * @since 1.0
 */
public class BlobFinder {

	private enum TypePick { SIMILAR, UNSIMILAR, ALL }
	private final ArrayList<Specie> species;

	/**
	 * Instantiate the BlobFinder with the given array of species
	 * @param species the species the blob finder should search through
	 */
	BlobFinder(ArrayList<Specie> species) {
		this.species = species;
	}

	/**
	 * Gets the blob that is colliding with the current blob irregardless of
	 * what species it belongs it.
	 * 
	 * @param body the body to test collisions against
	 * @return the colliding body or null if the body is colliding with anything
	 */
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

	/**
	 * Gets the closest blob of the same species.
	 * 
	 * @param blob the blob to use
	 * @return the closest blob or null if no other blobs are in the specie
	 */
	public Blob getClosestSimilar(Blob blob) {
		return getClosest(blob, TypePick.SIMILAR);
	}

	/**
	 * Gets the closest blob of a different species.
	 * 
	 * @param blob the blob to use
	 * @return the closest blob or null if no other blobs are in other species
	 */
	public Blob getClosestUnSimilar(Blob blob) {
		return getClosest(blob, TypePick.UNSIMILAR);
	}

	/**
	 * Gets the closest blob irregardless of species.
	 * 
	 * @param blob the blob to use
	 * @return the closest blob or null if no other blobs are present.
	 */
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
