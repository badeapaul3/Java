package labs.pm.optional.factory;

/**
 * @author hatzp
 **/
public class TestCarFactory {
    public static void main(String[] args) {
        Car rover = CarFactory.getCar("Blue","5000 V8", "SUV", "RangeRover");
        Car rolls = CarFactory.getCar("Black", "6000 V10","Sedan","rollsroyce");
        System.out.println("Factory produced car type: " + rover);
        System.out.println("Factory produced car type: " + rolls);
    }
}
