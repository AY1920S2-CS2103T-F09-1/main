package csdev.couponstash.model;

import csdev.couponstash.model.coupon.Coupon;

import javafx.collections.ObservableList;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Coupon> getPersonList();

}