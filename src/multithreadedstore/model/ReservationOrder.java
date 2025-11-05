package multithreadedstore.model;

/**
 * Represents a reservation made by a customer.
 */
public class ReservationOrder extends Order {

    /**
     * Indicates that this object represents a reservation.
     */
    @Override
    public boolean isReservationOrder() {
        return true;
    }
}
