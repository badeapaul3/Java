package labs.pm.data;

import java.math.BigDecimal;

/**
 * @author hatzp
 **/
public class Drink extends Product{
    public Drink(int id, String name, BigDecimal price, Rating rating) {
        super(id, name, price, rating);
    }
}
