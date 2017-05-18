import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

import com.example.robo.atomicbomberman.Bomb;
import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;

import static sun.misc.Version.print;

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
			while(true){
				try{

				    ServerSocket socketServer = new ServerSocket(Constants.PORT);
					Socket ss = socketServer.accept();
					
					ServerThread x = new ServerThread();
					x.setSocket(ss);
                    new Thread(x).start();

					socketServer.close();

				}catch(Exception e){
					e.printStackTrace();
				}
					
			}
			
		
	}
}
