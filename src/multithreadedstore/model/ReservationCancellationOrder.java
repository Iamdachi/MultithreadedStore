package multithreadedstore.model;

public class ReservationCancellationOrder extends Order {

    @Override
    public boolean isReservationCancellationOrder() {
        return true;
    }
}
