package net.xuset.smoothLife.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.xuset.smoothLife.world.World;
import net.xuset.smoothLife.world.WorldFactory;

public class WorldViewer {
	private final Canvas canvas = new Canvas();
	private final World world;
	private final UiController uiController;
	
	private BufferStrategy drawStrategy = null;
	private long ticks = 0L;
	
	private volatile boolean exitLoop = false;
	
	protected static JFrame createAndPackWindow(World world, WorldViewer viewer,
			UiController controller) {
		
		JFrame frame = new JFrame("SmoothLife");
		frame.setPreferredSize(new Dimension(world.getWidth(), world.getHeight()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(viewer.getCanvas(), BorderLayout.CENTER);
		frame.getContentPane().add(controller, BorderLayout.SOUTH);
		
		frame.pack();
		frame.setVisible(true);
		return frame;
	}
	
	public static void main(String[] args) throws IOException {
		World world = new WorldFactory().createNewWorld();
		UiController uiController = new UiController();
		WorldViewer viewer = new WorldViewer(world, uiController);
		
		createAndPackWindow(world, viewer, uiController);
		viewer.loop();
	}
	
	public WorldViewer(World world) {
		this(world, new UiController());
	}
	
	public WorldViewer(World world, UiController uiController) {
		this.world = world;
		this.uiController = uiController;
		
		canvas.setIgnoreRepaint(true);
	}
	
	public void loop() {
		exitLoop = false;
	
		while (!exitLoop) {
			onUpdate();
		}
	}
	
	public void updateWorld() {
		world.updateBlobs();
		ticks++;
	}
	
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
	
	public void exitLoop() {
		exitLoop = true;
	}
	
	protected Canvas getCanvas() {
		return canvas;
	}
	
	protected void onUpdate() {
		final long msPerUpdate = 10L;
		long startTime = System.currentTimeMillis();
		
		updateWorld();
		
		if (uiController.shouldDraw()) {
			drawWorld(1.0);
			
			long pause = msPerUpdate - System.currentTimeMillis() - startTime;
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
	
	protected static class UiController extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final String txtStartDraw = "Start drawing";
		private static final String txtStopDraw = "Stop drawing";
		
		private final JButton btnNoDraw;
		private boolean shouldDraw = true;
		
		public UiController() {
			btnNoDraw = new JButton();
			btnNoDraw.addActionListener(new BtnNoDrawAction());
			setNoDrawText();
			add(btnNoDraw);
		}
		
		private void setNoDrawText() {
			btnNoDraw.setText(shouldDraw ? txtStopDraw : txtStartDraw);
		}
		
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
