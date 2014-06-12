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

/**
 * This class simulates the World object by constantly calling
 * World#updateBlobs(). When a client (WorldClientViewer) connects to the
 * server, the state of the World object is serialized using the
 * WorldSerializer. The serialized state is then sent to the newly connected
 * client. This class is useful if you want multiple clients to view the same
 * world or if you want to run the CPU intensive part of simulating the world
 * on a different machine.
 * 
 * @author xuset
 * @since 1.0
 * @see WorldClientViewer
 */
public class WorldServer {
	private static final String helpOutput =
			"Must provide only a port number as an argument! (exclude the brackets)\n" +
					"   java -jar programName.jar [port]\n";

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

	/**
	 * Entry point into the program.
	 * This method creates a server and a world. The server is started on
	 * a port given by the args argument. When a WorldClientViewer connects to
	 * the same port, WorldServer serializes the World object's state and
	 * sends it to client. The client then recreates the world.
	 * 
	 * If the port number is not in the args argument, the method returns
	 * without creating the server or world.
	 * 
	 * @param args the command line args that should contain the port
	 * @throws IOException if an error occurs trying to listen to the port
	 */
	public static void main(String[] args) throws IOException {
		int port = getPortFromArgs(args);
		if (port == -1)
			return;

		TcpServer server = new TcpServer(3L, port);
		World world = new WorldFactory().createNewWorld();
		new WorldServer(world, server);
	}

	/**
	 * Creates a new WorldServer for the given world and listens to hub
	 * for requests to serialize and transmit the world's state. This
	 * 
	 * @param world the world to transmit the state of to clients
	 * @param hub the hub to listen to for state requests by clients
	 */
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
