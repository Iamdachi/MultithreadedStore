package multithreadedstore.model;

/**
 * Represents an order that cancels a previously reserved product.
 */
public class ReservationCancellationOrder extends Order {

    /** Returns true to indicate this is a reservation cancellation order. */
    @Override
    public boolean isReservationCancellationOrder() {
        return true;
    }
}

