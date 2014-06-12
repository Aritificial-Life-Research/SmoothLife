package net.xuset.smoothLife.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import net.xuset.smoothLife.world.Blob;
import net.xuset.smoothLife.world.BlobActions;
import net.xuset.smoothLife.world.Specie;
import net.xuset.smoothLife.world.World;

/**
 * Responsible for drawing the world using Graphics context.
 * 
 * @author xuset
 * @since 1.0
 * @see java.awt.Graphics
 *
 */
public class WorldDrawer {

	private static final Color foregroundColor = new Color(20, 40, 200);
	private static final Color backgroundColor = Color.white;

	/**
	 * Draws the world by drawing every blob in every species.
	 * 
	 * @param g the graphics context
	 * @param world the world to draw
	 * @param scale the scale at which to draw the world. Must be greater than 0
	 * @param width the width of the drawing window
	 * @param height the height of the drawing window
	 * @throws IllegalArgumentException if scale is less than or equal to 0
	 */
	public static void drawWorld(Graphics g, World world, double scale,
			int width, int height) {

		if (scale <= 0.0)
			throw new IllegalArgumentException("scale must be greater than 0");

		((Graphics2D) g).setRenderingHint(
				RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);

		g.setColor(foregroundColor);
		g.fillRect(0, 0,
				(int) (world.getWidth() * scale),
				(int) (world.getHeight() * scale));

		drawAllBlobs(g, scale, world);
	}

	/**
	 * Draws stats about the world. This should be called after drawWorld or
	 * not at all.
	 * 
	 * @param g the graphics context
	 * @param world the world to get the stats from
	 * @param ticks the amount of ticks the game has gone through
	 */
	public static void drawFitnessInfo(Graphics g, World world, long ticks) {

		g.setColor(Color.white);
		int textHeight = g.getFontMetrics().getHeight() + 5;

		if (ticks >= 0)
			g.drawString("Ticks: " + ticks, textHeight, textHeight);

		int yOffset = 2 * textHeight;
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			Specie sp = world.getSpecie(i);
			double total = sp.getSummedFitness();
			double average = total / sp.getBlobCount();
			g.drawString("Specie: " +
					(sp.isPrey() ? "prey" : "predator"), textHeight, yOffset);
			yOffset += textHeight;
			g.drawString("   Total fitness: " + total, textHeight, yOffset);
			yOffset += textHeight;
			g.drawString("   Average fitness: " + average, textHeight, yOffset);
			yOffset += textHeight;
		}
	}

	private static void drawAllBlobs(Graphics g, double scale, World world) {
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			Specie sp = world.getSpecie(i);

			for (int j = 0; j < sp.getBlobCount(); j++) {
				Blob b = sp.getBlob(j);

				drawBlob(g, scale, b);
			}
		}
	}

	private static void drawBlob(Graphics g, double scale, Blob b) {
		Color mainColor = b.getColor();
		Color borderColor = b.wasAttacked() ? mainColor.brighter() : mainColor.darker();
		double rb = b.getBody().getRadius() * scale; //radius border
		double rm = (b.getBody().getRadius() - 2) * scale; //radius main

		drawBlobAngleView(g, scale, b);
		drawCircle(g, scale, borderColor, b, rb);
		drawCircle(g, scale, mainColor, b, rm);
		drawInnerCircle(g, scale, b);
	}

	private static void drawBlobAngleView(Graphics g, double scale, Blob blob) {
		double radius = (7 + blob.getBody().getRadius());
		double viewAngle = Math.PI / 8;
		double x = blob.getBody().getX();
		double y = blob.getBody().getY();
		double angle = blob.getBody().getAngle();


		g.setColor(getBlobAngleViewColor(blob));

		g.fillArc(
				(int) (scale * (x - radius)), (int) (scale * (y - radius)),
				(int) (radius * 2), (int) (radius * 2),
				(int) (Math.toDegrees(angle - viewAngle)),
				(int) Math.toDegrees(2 * viewAngle));
	}

	private static void drawInnerCircle(Graphics g, double scale, Blob b) {
		Color color = getBlobInnerColor(b);
		double ri = b.getBody().getRadius() * 0.2 * scale;

		drawCircle(g, scale, color, b, ri);
	}

	private static void drawCircle(Graphics g, double scale, Color color,
			Blob blob, double r) {

		g.setColor(color);
		g.fillOval(
				(int) (scale * (blob.getBody().getX() - r)),
				(int) (scale * (blob.getBody().getY() - r)),
				(int) (2 * r),
				(int) (2 * r) );

	}

	private static Color getBlobAngleViewColor(Blob blob) {
		if (blob.isPrey())
			return Color.white;

		if (isContainingAction(blob, BlobActions.SPECIAL_ACTION))
			return Color.white.darker();
		else
			return Color.white;
	}

	private static final Color predatorInnerColor = new Color(100, 0, 0);
	private static final Color predatorInnerActiveColor = new Color(220, 0, 0);
	private static Color getBlobInnerColor(Blob blob) {
		if (blob.isPrey())
			return blob.getColor().brighter();

		if (isContainingAction(blob, BlobActions.SPECIAL_ACTION))
			return predatorInnerActiveColor;

		return predatorInnerColor;
	}

	private static boolean isContainingAction(Blob blob, BlobActions action) {
		BlobActions[] allActions = blob.getActions();
		for (BlobActions a : allActions) {
			if (a == action)
				return true;
		}

		return false;
	}
}
