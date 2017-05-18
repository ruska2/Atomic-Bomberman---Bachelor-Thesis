import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.example.robo.atomicbomberman.Bomb;
import com.example.robo.atomicbomberman.LoginUser;
import com.example.robo.atomicbomberman.RegistredUser;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;

public class ServerThread implements Runnable{
	
	Socket ss;

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
        try {
            makeOperation();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
	
	public void setSocket(Socket s) throws IOException, ClassNotFoundException{
		ss = s;
	}

	public void makeOperation() throws IOException,ClassNotFoundException {
        InputStream input = ss.getInputStream();

        ObjectInputStream objstream = new ObjectInputStream(input);
        Object obj = objstream.readObject();

        long UID = ObjectStreamClass.lookup(obj.getClass()).getSerialVersionUID();

        switch ((int) UID) {
            case 1 :
                Bomb bomb = (Bomb) obj;
                if(BombTicker.checkCorrectAdd(bomb)) {

                    new CheckBomb(bomb).checkCorrectness();
                }
                break;

            case 2 :
                final User user = (User) obj;
                if(user.getDatetime() == 0){
                    //LOOGOU
                    new LogoutChecker(user).checkMethod();
                }else {
                    //ADD-UPDATE
                    new CheckCorrectLatLong(user).checkMethod();
                    new AddToTrackTree(user).addToTrack();
                }
                break;

            case 3:
                final LoginUser loginUser = (LoginUser) obj;
                System.out.println(loginUser.getName() + ","+ loginUser.getPassword());

                if(loginUser.getDelete()){
                    new LoginControl(loginUser.getName(),loginUser.getPassword(),loginUser.getImei()).deleteUser();
                }
                else{
                    new LoginControl(loginUser.getName(),loginUser.getPassword(),loginUser.getImei()).checkCorrectUser();
                }
                break;

            case 4:
                RegistredUser ru = (RegistredUser) obj;
                new CheckRegistration(ru).checkCorretness();
                break;

            default: break;

        }

        /*ss.close();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/
    }


}
