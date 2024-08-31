package labs.pm.optional.factory;

/**
 * @author hatzp
 **/
abstract class Car {

    String color;
    String engineSize;
    String style;
    String type;

    public abstract String getColor();
    public abstract String getEngineSize() ;
    public abstract String getStyle();
    public abstract String getType();



    @Override
    public String toString() {
        return "Car{" +
                "color='" + color + '\'' +
                ", engineSize='" + engineSize + '\'' +
                ", style='" + style + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
