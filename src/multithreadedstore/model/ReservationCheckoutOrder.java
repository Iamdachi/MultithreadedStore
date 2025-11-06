package multithreadedstore.model;

/**
 * Represents an order that will check out / finalize the previously made reservation.
 */
public class ReservationCheckoutOrder extends Order {

    /** Returns true to indicate this is a reservation checkout order. */
    @Override
    public boolean isReservationCheckoutOrder() {
        return true;
    }
}

