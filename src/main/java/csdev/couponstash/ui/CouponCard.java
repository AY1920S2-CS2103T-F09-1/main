package csdev.couponstash.ui;

import java.util.Comparator;

import csdev.couponstash.model.coupon.Coupon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Coupon}.
 */
public class CouponCard extends UiPart<Region> {

    private static final String FXML = "CouponListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on CouponStash level 4</a>
     */

    public final Coupon coupon;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label savings;
    @FXML
    private Label expiryDate;
    @FXML
    private Label usage;
    @FXML
    private Label limit;
    @FXML
    private FlowPane tags;

    public CouponCard(Coupon coupon, int displayedIndex) {
        super(FXML);
        this.coupon = coupon;
        id.setText(displayedIndex + ". ");
        name.setText(coupon.getName().fullName);
        phone.setText(coupon.getPhone().value);
        savings.setText(coupon.getSavings().toString());
        expiryDate.setText(coupon.getExpiryDate().value);
        usage.setText(coupon.getUsage().toString());
        limit.setText(coupon.getLimit().toString());
        coupon.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CouponCard)) {
            return false;
        }

        // state check
        CouponCard card = (CouponCard) other;
        return id.getText().equals(card.id.getText())
                && coupon.equals(card.coupon);
    }
}
