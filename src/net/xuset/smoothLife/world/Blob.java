package net.xuset.smoothLife.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.xuset.smoothLife.nnetwork.Brain;
import net.xuset.smoothLife.nnetwork.Chromosome;

/**
 *  All blobs start out with a certain amount of energy. The blobs can
 *  execute predefined actions in its update method with the goal of
 *  getting more energy. Predator blobs can attack blobs marked as prey
 *  to steal their energy. So it is the goal of the predator blobs to
 *  find the prey blobs, and it is the goal of the prey blobs to escape
 *  from the predator blobs. Predator blobs can only attack members of
 *  other species that are marked as prey.
 * 
 *  With each update blobs loose a small amount of energy marked as the
 *  'cost to live'. When a blob's energy drops to zero or below, it is
 *  considered dead and will remain dead until it's reset method is called.
 * 
 *  When the update method is called the blob's age is incremented. This
 *  is used by the genetic algorithm to determine which blobs are the most
 *  fit to reproduce.
 * 
 *  Each blob is given are chromosome. This chromosome is used to construct
 *  a brain for the blob. The blob's brain is used to determine what
 *  actions to make based on the environment. The chromosome is also used
 *  to determine the blob's size, and color.
 * 
 *  @author xuset
 *  @since 1.0
 */
public class Blob {

	private static final double attackEnergyDelta = -3.0, attackPayoff = -1.0;
	private static final double attackRange = 7;
	private static final double groupRange = 10;
	private static final double moveForwardDelta = 0.3, turnDelta = 0.1;
	private static final double costToLive = -1.0;
	private static final double initEnergy = 1000.0;

	private final List<BlobActions> actionBuffer =
			new ArrayList<BlobActions>(BlobActions.values().length);
	private final Body body = new Body();
	private final Brain brain;
	private final long specieId;
	private final BlobFinder blobFinder;
	private final boolean isPrey;
	private final ChromosomeHolder chromoHolder;

	private int age;
	private double energy;
	private boolean wasAttacked = false;

	/**
	 * Instantiate a new blob.
	 * @param neuronLayout integer array that is used to create the brain
	 * @param specieId the id of the specie this blob belongs to
	 * @param blobFinder the object used to find blobs in the world
	 * @param isPrey indicates if this blob is prey or a predator
	 * @param chromosomeHolder object used to hold the chromosomes of the blob
	 */
	Blob(int[] neuronLayout, long specieId, BlobFinder blobFinder, boolean isPrey,
			ChromosomeHolder chromosomeHolder) {

		brain = new Brain(neuronLayout);
		this.specieId = specieId;
		this.blobFinder = blobFinder;
		this.isPrey = isPrey;
		this.chromoHolder = chromosomeHolder;

		reset(0, 0, chromoHolder.getChromosome().clone());
		body.setMoveCoefficient(isPrey ? 0.5 : 2.0);
	}

	/**
	 * Returns the body object of the blob.
	 * 
	 * @return the body object of the blob
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * Returns the amount of energy the blob has. When the energy drops to
	 * zero or below, the blob is considered dead.
	 * @return the energy the blob has
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the amount of update ticks the blob has lived through without
	 * dying.
	 * 
	 * @return the update tick count the blob has lived through
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Gets the rgb color of the blob.
	 * 
	 * @return a Color object representing the color of the blob
	 */
	public Color getColor() {
		int r = chromoHolder.getChromosome().getColorRed();
		int g = chromoHolder.getChromosome().getColorGreen();
		int b = chromoHolder.getChromosome().getColorBlue();
		return new Color(r, g, b);
	}

	/**
	 * Indicates if the blob was attacked during the last update tick.
	 * 
	 * @return true if the blob was attacked, false otherwise
	 */
	public boolean wasAttacked() {
		return wasAttacked;
	}

	/**
	 * Indicates if this blob is prey or a predator.
	 * 
	 * @return true if this blob is prey, false if this blob is a predator.
	 */
	public boolean isPrey() {
		return isPrey;
	}

	/**
	 * Returns an the actions the blob executed in the last update tick. The
	 * returned array is a copy and can be modified without affecting the
	 * Blob class.
	 * 
	 * @return an array of actions the blob executed
	 */
	public BlobActions[] getActions() {
		BlobActions[] buff = new BlobActions[actionBuffer.size()];
		return actionBuffer.toArray(buff);
	}

	/**
	 * Return the specie id of the specie this blob belongs to.
	 * 
	 * @return the specie id of the blob.
	 */
	long getSpecieId() {
		return specieId;
	}

	/**
	 * Resets the blob. The location, angle, age, energy, and chromosome are
	 * reset. After the reset, the blob will be 'brand new'.
	 * 
	 * @param x the new x location of the blob
	 * @param y the new y location of the blob
	 * @param newChromo the new chromosome for the blob
	 */
	void reset(double x, double y, Chromosome newChromo) {
		reset(x, y,
				Math.random() * 2 * Math.PI,               //angle
				initEnergy, 1,                             //energy, age
				newChromo);  //newChromo, oldChromo
	}

	/**
	 * Resets the blob. The location, angle, age, energy, and chromosome are
	 * reset. After the reset, the blob will be 'brand new'
	 * 
	 * @param x the new x location of the blob
	 * @param y the new y location of the blob
	 * @param angle the new angle of the blob
	 * @param newEnergy the new energy of the blob
	 * @param newAge the new age of the blob
	 * @param newChromo the new chromosome of the blob
	 */
	void reset(double x, double y, double angle, double newEnergy, int newAge,
			Chromosome newChromo) {

		chromoHolder.getChromosome().setFitness(age);

		this.age = newAge;
		this.energy = newEnergy;

		chromoHolder.replaceChromosome(newChromo);

		actionBuffer.clear();
		brain.putWeights(newChromo.copyBrainGenes());
		body.reset(x, y, newChromo.getRadius(), angle);
	}

	/**
	 * Method called on all blobs of all species before the update method is
	 * called.
	 */
	void preupdate() {
		wasAttacked = false;
	}

	/**
	 * Method called for the blob to do it's update tick.
	 * The blob executes it's actions, increases it's age, and other things
	 * during this call.
	 */
	void update() {
		age++;
		energy += costToLive;

		performActions();
	}

	/**
	 * Gets a copy of the current chromosome.
	 * 
	 * @return a clone of the current chromosome
	 */
	Chromosome cloneChromosome() {
		return chromoHolder.getChromosome().clone();
	}

	/**
	 * Gets a copy of the previous chromosome.
	 * 
	 * @return a clone of the previous chromosome
	 */
	Chromosome cloneOldChromosome() {
		return chromoHolder.cloneChromoInGenePool();
	}

	private void performActions() {
		actionBuffer.clear();

		Blob friend = blobFinder.getClosestSimilar(this);
		Blob enemy = blobFinder.getClosestUnSimilar(this);
		BrainInterface.stimulateActions(brain, actionBuffer, this, friend, enemy);

		for (int i = 0; i < actionBuffer.size(); i++) {
			switch(actionBuffer.get(i)) {
			case TURN_LEFT:
				actionTurn(turnDelta);
				break;
			case TURN_RIGHT:
				actionTurn(-turnDelta);
				break;
			case MOVE_FORWARD:
				actionMove();
				break;
			case SPECIAL_ACTION:
				actionSpecial();
				break;
			}
		}
	}

	private void actionTurn(double deltaAngle) {
		body.adjustAngle(deltaAngle);
	}

	private void actionMove() {
		body.moveForward(moveForwardDelta, blobFinder);
	}

	private void actionSpecial() {
		if (isPrey) {
			actionGroupHelp();
		} else {
			actionAttack();
		}
	}

	private void actionAttack() {
		Blob prey = blobFinder.getClosestUnSimilar(this);
		if (prey != null && body.isWithinRange(prey.body, attackRange)) {
			prey.wasAttacked = true;
			prey.energy += attackEnergyDelta;
			energy += attackEnergyDelta * attackPayoff;
		} else {
			//energy--;
		}
	}

	private void actionGroupHelp() {
		Blob friendly = blobFinder.getClosestSimilar(this);
		if (friendly != null && body.isWithinRange(friendly.body, groupRange))
			energy += -costToLive;
	}
}
