import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;


public class Server implements Runnable{

    static Database db;
    static DataCleaner dc;

	public static void main(String[] args) throws IOException {

		// TODO Auto-generated method stub
        db = new Database();
		Server x = new Server();
		new Thread(x).start();
		dc = new DataCleaner();
		dc.start();

		new Thread(new QuestGenerator()).start();
		new Thread(new BonusGenerator()).start();
		new Thread(new HitBonusChecker()).start();

	}
	
	Server() throws IOException{}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		ServerSocket socketServer = null;
		try {
			socketServer = new ServerSocket(Constants.PORT);
		}catch (Exception e){}
			while(true){
				try{
					Socket ss = socketServer.accept();


					ServerThread x = new ServerThread();
					x.setSocket(ss);
                    new Thread(x).start();

				}catch(Exception e){
					e.printStackTrace();
				}
					
			}
	}
}
