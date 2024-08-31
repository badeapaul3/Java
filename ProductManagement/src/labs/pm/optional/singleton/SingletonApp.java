package labs.pm.optional.singleton;

/**
 * @author hatzp
 **/
public class SingletonApp {


    public static void main(String[] args) {
        PeerSingleton ps = PeerSingleton.getInstance();
        String[] hosts = ps.getHostNames();
        System.out.println(hosts[1]);

        PeerSingleton p2 = PeerSingleton.getInstance();
        String[] hosts2 = ps.getHostNames();
        System.out.println(hosts2[1]);


    }




}
