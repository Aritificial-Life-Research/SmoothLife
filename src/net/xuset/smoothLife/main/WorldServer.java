package net.xuset.smoothLife.main;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetHub;
import net.xuset.objectIO.connections.sockets.ServerEventListener;
import net.xuset.objectIO.connections.sockets.tcp.TcpServer;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.smoothLife.world.Specie;
import net.xuset.smoothLife.world.World;
import net.xuset.smoothLife.world.WorldFactory;
import net.xuset.smoothLife.world.WorldSerializer;

public class WorldServer {
	private static final String helpOutput =
			"Must provide only a port number as an argument! (exclude the brackets)\n" +
			"   java -jar programName [port]\n";

	private final World world;
	private final InetHub<?> hub;
	private final Queue<InetCon> connsToUpdate = new ConcurrentLinkedQueue<InetCon>();
	
	private long ticks = 0L;
	private volatile boolean exitLoop = false;
	
	private static int getPortFromArgs(String[] args) {
		if (args.length != 1) {
			System.err.println(helpOutput);
			return -1;
		}
		
		try {
			int port = Integer.parseInt(args[0]);
			return port;
		} catch (NumberFormatException ex) {
			System.err.println(helpOutput);
			return -1;
		}
	}
	
	public static void main(String[] args) throws IOException {
		int port = getPortFromArgs(args);
		if (port == -1)
			return;
		
		TcpServer server = new TcpServer(3L, port);
		World world = new WorldFactory().createNewWorld();
		new WorldServer(world, server);
	}
	
	public WorldServer(World world, InetHub<?> hub) {
		this.world = world;
		this.hub = hub;
		hub.watchEvents(new ServerListener());
		loop();
	}
	
	private void loop() {
		exitLoop = false;
		
		while (!hub.isShutdown() && !exitLoop) {
			world.updateBlobs();
			ticks++;
			if (ticks % 100000 == 0)
				outputFitness();
			updateNetwork();
		}
	}
	
	private void updateNetwork() {
		if (connsToUpdate.isEmpty())
			return;
		
		MarkupMsg worldMsg = WorldSerializer.serializeWorld(world);
		while(!connsToUpdate.isEmpty()) {
			InetCon con = connsToUpdate.poll();
			
			if (!con.isConnected())
				continue;
			
			con.sendMsg(worldMsg);
			con.flush();
			con.close();
		}
	}
	
	private void outputFitness() {
		System.out.println("Ticks=" + ticks);
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			Specie sp = world.getSpecie(i);
			double total = sp.getSummedFitness();
			double average = total / sp.getBlobCount();
			System.out.println("    Specie " + i + " (" +
					(sp.isPrey() ? "prey" : "predator") + "). Average fitness = " +
					average);
		}
	}
	
	private class ServerListener implements ServerEventListener {

		@Override
		public void onAdd(InetCon con) {
			System.out.println("Received new connection");
			connsToUpdate.add(con);
		}

		@Override public void onRemove(InetCon con) { /* Do nothing */ }
		@Override public void onLastRemove() { /* Do nothing */ }
		@Override public void onShutdown() { /* Do nothing */ }
	}
}
