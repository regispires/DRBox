package ligador;
import java.io.Serializable;


public class Service implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;
	
	public Service(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
	    return this.host + ":" + this.port;
	}

}
