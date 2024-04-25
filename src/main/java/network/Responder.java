package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import network.Router;
import network.Table;

public class Responder {

	private Router selfRouter;

	public Responder(Router selfRouter) {
		this.selfRouter = selfRouter;
	}

	// Responder responder = new Responder() ; responder.getTables();
	// responder.checkAndUpdate();

	/**
	 * 
	 * @param selfServer
	 * @return list of tables of the directly connected routers from "selfRouter"
	 */
	public List<Table> getTables() {
		Table table = null;
		List<Table> tables = new ArrayList<>();
		try {
			for(Socket connectedRouter : selfRouter.getHistoryOfConnections()) {
				if(connectedRouter.isClosed()) {
					continue;
				}
				ObjectOutputStream out = new ObjectOutputStream(connectedRouter.getOutputStream());
				out.flush();
				ObjectInputStream input = new ObjectInputStream(connectedRouter.getInputStream());
				//input.reset();
				table = (Table)input.readObject();
				tables.add(table);
				//input.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tables;
	}
}
