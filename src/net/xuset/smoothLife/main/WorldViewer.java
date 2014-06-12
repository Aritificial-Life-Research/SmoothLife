package net.xuset.smoothLife.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.xuset.smoothLife.world.World;
import net.xuset.smoothLife.world.WorldFactory;

/**
 * Defines one of the entry points into the program. This class is responsible
 * for creating the GUI windows and updating/drawing the world.
 * 
 * @author xuset
 * @since 1.0
 */
public class WorldViewer {
	private final Canvas canvas = new Canvas();
	private final World world;
	private final UiController uiController;

	private BufferStrategy drawStrategy = null;
	private long ticks = 0L;

	private volatile boolean exitLoop = false;

	/**
	 * Creates the gui window.
	 * @param world the world to create the window for
	 * @param viewer the viewer to add to the window
	 * @return the new jframe window
	 */
	protected static JFrame createAndPackWindow(World world, WorldViewer viewer) {

		JFrame frame = new JFrame("SmoothLife");
		frame.setPreferredSize(new Dimension(world.getWidth(), world.getHeight()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(viewer.getCanvas(), BorderLayout.CENTER);
		frame.getContentPane().add(viewer.uiController, BorderLayout.SOUTH);

		frame.pack();
		frame.setVisible(true);
		return frame;
	}

	/**
	 * Main entry point.
	 * Creates a world and gui window then loops forever by updating/drawing the
	 * world.
	 * 
	 * @param args this argument is not used
	 */
	public static void main(String[] args) {
		World world = new WorldFactory().createNewWorld();
		WorldViewer viewer = new WorldViewer(world);

		createAndPackWindow(world, viewer);
		viewer.loop();
	}

	/**
	 * Creates the world viewer based on the given world.
	 * 
	 * @param world the world to create the viewer for
	 */
	public WorldViewer(World world) {
		this(world, new UiController());
	}

	/**
	 * Creates the world viewer based on the given world.
	 * 
	 * @param world the world to create the viewer for
	 * @param uiController the controller used to control the world
	 */
	public WorldViewer(World world, UiController uiController) {
		this.world = world;
		this.uiController = uiController;

		canvas.setIgnoreRepaint(true);
	}

	/** This method loops until exitLoop is called. */
	public void loop() {
		exitLoop = false;

		while (!exitLoop) {
			onUpdate();
		}
	}

	/** Updates the world */
	public void updateWorld() {
		world.updateBlobs();
		ticks++;
	}

	/**
	 * Draws the world.
	 * 
	 * @param scale the scale to draw the world at. scale should be > 0
	 */
	public void drawWorld(double scale) {
		if (drawStrategy == null)
			createDrawStrategy();

		if (!canDraw())
			return;

		Graphics g = drawStrategy.getDrawGraphics();
		WorldDrawer.drawWorld(g, world, scale, canvas.getWidth(), canvas.getHeight());
		WorldDrawer.drawFitnessInfo(g, world, ticks);

		drawStrategy.show();
	}

	/**
	 * Sets the exit flag for loop method.
	 */
	public void exitLoop() {
		exitLoop = true;
	}

	/**
	 * Returns the canvas that is in use.
	 * @return the canvas used by the viewer
	 */
	protected Canvas getCanvas() {
		return canvas;
	}

	/**
	 * Called to handle the update.
	 * The world is updated then if it is time to draw the world, the world is
	 * drawn.
	 */
	protected void onUpdate() {
		final long msPerUpdate = 10L;
		long startTime = System.currentTimeMillis();

		updateWorld();

		if (uiController.shouldDraw()) {
			drawWorld(1.0);

			long pause = msPerUpdate - (System.currentTimeMillis() - startTime);
			if (pause > 0)
				try { Thread.sleep(pause); } catch (InterruptedException ex) { }
		}
	}

	private void createDrawStrategy() {
		canvas.createBufferStrategy(2);
		drawStrategy = canvas.getBufferStrategy();
		canvas.requestFocus();
	}

	private boolean canDraw() {
		return drawStrategy != null && canvas.getWidth() > 0 && canvas.getHeight() > 0;
	}

	/**
	 * UiController is used to provide a GUI controller for the world.
	 * Mainly it is used to turn drawing the world on and off.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	protected static class UiController extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final String txtStartDraw = "Start drawing";
		private static final String txtStopDraw = "Stop drawing";

		private final JButton btnNoDraw;
		private boolean shouldDraw = true;

		/**
		 * Create a new UiController instance that can be added to a GUI
		 * window.
		 */
		public UiController() {
			btnNoDraw = new JButton();
			btnNoDraw.addActionListener(new BtnNoDrawAction());
			setNoDrawText();
			add(btnNoDraw);
		}

		private void setNoDrawText() {
			btnNoDraw.setText(shouldDraw ? txtStopDraw : txtStartDraw);
		}

		/**
		 * Indicates if the WorldViewer should draw the world.
		 * @return true if the WorldViewer should draw the world, false
		 * 		otherwise.
		 */
		public boolean shouldDraw() {
			return shouldDraw;
		}

		private class BtnNoDrawAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				shouldDraw = !shouldDraw;
				setNoDrawText();
			}
		}
	}

}
