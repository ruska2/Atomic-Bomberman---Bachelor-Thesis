import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.example.robo.atomicbomberman.Bomb;
import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;

public class Server implements Runnable{

	static ServerSocket s1;
    static Database db;
    static DataCleaner dc;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
        db = new Database();
		Server x = new Server();
		new Thread(x).start();
		dc = new DataCleaner();
		dc.start();


	}
	
	Server() throws IOException{
	

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			s1 = new ServerSocket(Constants.PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			while(true){
				try{
					Socket ss = s1.accept();
					
					ServerThread x = new ServerThread();
					x.setSocket(ss);

					Thread xt = new Thread(x);
					xt.start();

				}catch(Exception e){
					e.printStackTrace();
				}
					
			}
			
		
	}
}
