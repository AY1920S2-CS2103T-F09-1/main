package csdev.couponstash.testutil;

import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_NAME;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_PHONE;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import csdev.couponstash.logic.commands.AddCommand;
import csdev.couponstash.logic.commands.EditCommand.EditCouponDescriptor;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.tag.Tag;

/**
 * A utility class for Coupon.
 */
public class CouponUtil {

    /**
     * Returns an add command string for adding the {@code coupon}.
     */
    public static String getAddCommand(Coupon coupon) {
        return AddCommand.COMMAND_WORD + " " + getCouponDetails(coupon);
    }

    /**
     * Returns the part of command string for the given {@code coupon}'s details.
     */
    public static String getCouponDetails(Coupon coupon) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + coupon.getName().fullName + " ");
        sb.append(PREFIX_PHONE + coupon.getPhone().value + " ");
        coupon.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditCouponDescriptor}'s details.
     */
    public static String getEditCouponDescriptorDetails(EditCouponDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
