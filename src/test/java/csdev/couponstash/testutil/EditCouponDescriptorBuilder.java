package csdev.couponstash.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import csdev.couponstash.logic.commands.EditCommand;
import csdev.couponstash.logic.commands.EditCommand.EditCouponDescriptor;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.tag.Tag;

/**
 * A utility class to help with building EditCouponDescriptor objects.
 */
public class EditCouponDescriptorBuilder {

    private EditCommand.EditCouponDescriptor descriptor;

    public EditCouponDescriptorBuilder() {
        descriptor = new EditCouponDescriptor();
    }

    public EditCouponDescriptorBuilder(EditCouponDescriptor descriptor) {
        this.descriptor = new EditCouponDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditCouponDescriptor} with fields containing {@code coupon}'s details
     */
    public EditCouponDescriptorBuilder(Coupon coupon) {
        descriptor = new EditCouponDescriptor();
        descriptor.setName(coupon.getName());
        descriptor.setPhone(coupon.getPhone());
        descriptor.setTags(coupon.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditCouponDescriptor} that we are building.
     */
    public EditCouponDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditCouponDescriptor} that we are building.
     */
    public EditCouponDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditCouponDescriptor}
     * that we are building.
     */
    public EditCouponDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditCouponDescriptor build() {
        return descriptor;
    }
}
