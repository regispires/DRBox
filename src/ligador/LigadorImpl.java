package ligador;

import helper.RemoteUtils;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


public class LigadorImpl extends UnicastRemoteObject implements Ligador  {
	private static final long serialVersionUID = 1L;
	private static LigadorImpl lg;
	
	Registry registry;
    Map<String, Service> servicos = new HashMap<String, Service>();
    
    
    protected LigadorImpl() throws RemoteException {
    }

    public static LigadorImpl getInstance() throws RemoteException{
    	if(lg == null)
    		lg = new LigadorImpl();
    	return lg;
    }
    
    @Override
    public void registry(String nome, Service obj) throws RemoteException {
    	System.out.println("Registrando serviço: " + nome + " - " + obj);
        servicos.put(nome, obj);
    }

    @Override
    public Service getService(String nome) throws RemoteException {
    	System.out.println("Obtendo serviço:" + nome);
        return this.servicos.get(nome);
    }

    public void start() {
        int port = 1099;
        RemoteUtils.localRegistry(port, this, RemoteUtils.LIGADOR);            
        System.out.println("Ligador started in port " + port);
    }

    public static void main(String[] args) {
        try {
            LigadorImpl.getInstance().start();
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
}
