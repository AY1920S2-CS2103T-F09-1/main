package csdev.couponstash.logic;

import static csdev.couponstash.commons.core.Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX;
import static csdev.couponstash.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static csdev.couponstash.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static csdev.couponstash.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static csdev.couponstash.testutil.Assert.assertThrows;
import static csdev.couponstash.testutil.TypicalCoupons.AMY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import csdev.couponstash.logic.commands.AddCommand;
import csdev.couponstash.logic.commands.CommandResult;
import csdev.couponstash.logic.commands.ListCommand;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.logic.parser.exceptions.ParseException;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;
import csdev.couponstash.model.ReadOnlyCouponStash;
import csdev.couponstash.model.UserPrefs;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.storage.JsonCouponStashStorage;
import csdev.couponstash.storage.JsonUserPrefsStorage;
import csdev.couponstash.storage.StorageManager;
import csdev.couponstash.testutil.CouponBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonCouponStashStorage couponStashStorage =
                new JsonCouponStashStorage(temporaryFolder.resolve("couponStash.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(couponStashStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        // Setup LogicManager with JsonCouponStashIoExceptionThrowingStub
        JsonCouponStashStorage couponStashStorage =
                new JsonCouponStashIoExceptionThrowingStub(temporaryFolder.resolve("ioExceptionAddressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ioExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(couponStashStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        // Execute add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY;
        Coupon expectedCoupon = new CouponBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addCoupon(expectedCoupon);
        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE + DUMMY_IO_EXCEPTION;
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void getFilteredCouponList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredCouponList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getCouponStash(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class JsonCouponStashIoExceptionThrowingStub extends JsonCouponStashStorage {
        private JsonCouponStashIoExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveCouponStash(ReadOnlyCouponStash couponStash, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }
}
