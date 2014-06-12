package net.xuset.smoothLife.main;

import java.io.IOException;
import java.net.Socket;

import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.tcp.TcpCon;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.smoothLife.main.WorldViewer.UiController;
import net.xuset.smoothLife.world.World;
import net.xuset.smoothLife.world.WorldFactory;

/**
 * Connects to a WorldServer on a given port. Once connected the server's
 * world's state is received and the the world is recreated on the client.
 * The world is then simulated on the client.
 * 
 * Using the server/client approach is good if you want to put the CPU intensive
 * part of simulating the world on a different machine. It is also good if you
 * plan on running the simulation for a long time.
 * 
 * @author xuset
 * @since 1.0
 * @see WorldServer
 *
 */
public class WorldClientViewer {

	private static final String helpOutput =
			"To connect to running server use the ip and port args. (withougt braces)\n" +
					"     java -jar programName.jar [ip] [port]\n";


	private static InetCon createConnectionFromArgs(String[] args) throws IOException {
		if (args.length == 2) {
			String ip = args[0];
			int port;
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				System.err.println(helpOutput);
				return null;
			}
			return new TcpCon(new Socket(ip, port), 3L);
		}

		System.out.println(helpOutput);
		return null;
	}

	/**
	 * Entry point into the program. An IP address and port are taken from
	 * the args argument and is used to connect to a WorldServer. The world
	 * received from the WorldServer is then simulated with a GUI window drawing
	 * the world.
	 * 
	 * If the args argument is missing the IP address or port number, the
	 * method returns without doing anything.
	 * 
	 * @param args the command line arguments. The array should contain an IP
	 * 		address then a port number
	 * @throws IOException If there is an error connecting to the server
	 */
	public static void main(String[] args) throws IOException {
		InetCon con = createConnectionFromArgs(args);
		if (con == null)
			return;

		MarkupMsg worldMsg = null;

		while (worldMsg == null) {
			if (con.isMsgAvailable()) {
				worldMsg = con.pollNextMsg();
				System.out.println("Received network update");
			} else if (!con.isConnected()){
				System.err.println("Connection closed");
				return;
			} else {
				System.out.println("Waiting to receive network update");
				try { Thread.sleep(500); } catch (InterruptedException ex) { }
			}
		}

		con.close();

		World world = new WorldFactory().createNewWorld(worldMsg);
		UiController controller = new WorldViewer.UiController();
		WorldViewer viewer = new WorldViewer(world, controller);
		WorldViewer.createAndPackWindow(world, viewer);
		viewer.loop();
	}
}
