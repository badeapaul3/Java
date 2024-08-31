package labs.pm.optional.factory;

/**
 * @author hatzp
 **/
public class RangeRover extends Car{

    public RangeRover(String color, String engsize, String style, String type) {
        this.color = color;
        this.engineSize = engsize;
        this.style = style;
        this.type = type;
    }


    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public String getEngineSize() {
        return this.engineSize;
    }

    @Override
    public String getStyle() {
        return this.style;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
