package multithreadedstore.model;

/**
 * Represents a reservation order.
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
