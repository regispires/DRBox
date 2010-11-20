package ligador;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Ligador extends Remote {
    public Service getService(String nome) throws RemoteException;
	public void registry(String nome, Service obj) throws RemoteException;
}
