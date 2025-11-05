package multithreadedstore.model;

/**
 * Represents a reservation made by a customer.
 */
public class ReservationCheckoutOrder extends Order {

    @Override
    public boolean isReservationCheckoutOrder() {
        return true;
    }
}

