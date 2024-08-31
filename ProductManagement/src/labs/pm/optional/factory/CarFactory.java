package labs.pm.optional.factory;

/**
 * @author hatzp
 **/
public class CarFactory {

    public static Car getCar(String color, String engSize, String style, String type){
        if("RangeRover".equalsIgnoreCase(type)) return new RangeRover(color,engSize,style,type);
        else if ("RollsRoyce".equalsIgnoreCase(type)) return new RollsRoyce(color,engSize,style,type);

        return null;
    }
}
