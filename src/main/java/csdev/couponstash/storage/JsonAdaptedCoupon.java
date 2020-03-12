package csdev.couponstash.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import csdev.couponstash.commons.exceptions.IllegalValueException;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.ExpiryDate;

import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.Phone;
import csdev.couponstash.model.coupon.savings.Savings;
import csdev.couponstash.model.tag.Tag;


/**
 * Jackson-friendly version of {@link Coupon}.
 */
class JsonAdaptedCoupon {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Coupon's %s field is missing!";

    private final String name;
    private final String phone;

    private final String expiryDate;
    private final JsonAdaptedSavings savings;

    private final List<JsonAdaptedTag> tagged = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedCoupon} with the given coupon details.
     */
    @JsonCreator

    public JsonAdaptedCoupon(@JsonProperty("name") String name,
                             @JsonProperty("phone") String phone,
                             @JsonProperty("savings") JsonAdaptedSavings savings,
                             @JsonProperty("tagged") List<JsonAdaptedTag> tagged, 
                             @JsonProperty("expiry date") String expiryDate) {
        this.name = name;
        this.phone = phone;
        this.savings = savings;
        this.expiryDate = expiryDate;

        if (tagged != null) {
            this.tagged.addAll(tagged);
        }
    }

    /**
     * Converts a given {@code Coupon} into this class for Jackson use.
     */
    public JsonAdaptedCoupon(Coupon source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        expiryDate = source.getExpiryDate().value;

        savings = new JsonAdaptedSavings(source.getSavings());
      
        tagged.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted coupon object into the model's {@code Coupon} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted coupon.
     */
    public Coupon toModelType() throws IllegalValueException {
        final List<Tag> couponTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tagged) {
            couponTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);


        if (expiryDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    ExpiryDate.class.getSimpleName()));
        }
        if (!ExpiryDate.isValidExpiryDate(expiryDate)) {
            throw new IllegalValueException(ExpiryDate.MESSAGE_CONSTRAINTS);
        }
        final ExpiryDate modelExpiryDate = new ExpiryDate(expiryDate);

        final Set<Tag> modelTags = new HashSet<>(couponTags);
        if (savings == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Savings.class.getSimpleName()));
        }
        final Savings modelSavings = savings.toModelType();

        return new Coupon(modelName, modelPhone, modelSavings, modelTags, modelExpiryDate);
    }

}
