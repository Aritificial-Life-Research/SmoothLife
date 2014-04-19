package net.xuset.smoothLife.main;

import java.io.IOException;
import java.net.Socket;

import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.tcp.TcpCon;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.smoothLife.main.WorldViewer.UiController;
import net.xuset.smoothLife.world.World;
import net.xuset.smoothLife.world.WorldFactory;

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
		WorldViewer viewer = new WorldViewer(world);
		UiController controller = new WorldViewer.UiController();
		WorldViewer.createAndPackWindow(world, viewer, controller);
		viewer.loop();
	}
}
