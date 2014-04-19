package net.xuset.smoothLife.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import net.xuset.smoothLife.world.Blob;
import net.xuset.smoothLife.world.Specie;
import net.xuset.smoothLife.world.World;

public class WorldDrawer {

	private static final Color foregroundColor = new Color(20, 40, 200);
	private static final Color backgroundColor = Color.white;
	
	public static void drawWorld(Graphics g, World world, double scale,
			int width, int height) {
		
		((Graphics2D) g).setRenderingHint(
				RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		
		g.setColor(foregroundColor);
		g.fillRect(0, 0,
				(int) (world.getWidth() * scale),
				(int) (world.getHeight() * scale));
		
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			Specie sp = world.getSpecie(i);
			
			for (int j = 0; j < sp.getBlobCount(); j++) {
				Blob b = sp.getBlob(j);
				
				b.draw(g, scale);
			}
		}
	}
	
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
}
