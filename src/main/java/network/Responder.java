package network;

import java.net.ServerSocket;
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
	public List<Table> getTables(ServerSocket selfServer) {
		List<Table> tables = new ArrayList<>();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tables;
	}

}
