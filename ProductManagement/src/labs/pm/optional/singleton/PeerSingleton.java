package labs.pm.optional.singleton;

/**
 * @author hatzp
 **/
public final class PeerSingleton {

    //a static member
    private static final String[] hostNames = new String[5];
    //static initialization of the single PeerSingleton instance
    private static final PeerSingleton instance = new PeerSingleton();

    //static block
    static {
        hostNames[0]="192.168.1.2";
        hostNames[1]="192.168.1.3";
        hostNames[2]="192.168.1.3";
        hostNames[3]="192.168.1.4";
        hostNames[4]="192.168.1.5";
    }

    private PeerSingleton(){}

    public static PeerSingleton getInstance(){return instance;}

    public String[] getHostNames(){return hostNames;}
}
