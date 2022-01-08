package ProjSD.src.ST.STemp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;


public interface ServicesInterface extends Remote {

	public int getTemp(int tsp) throws RemoteException;

}