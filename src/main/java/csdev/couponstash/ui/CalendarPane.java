package csdev.couponstash.ui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Logger;

import csdev.couponstash.commons.core.LogsCenter;
import csdev.couponstash.commons.exceptions.IllegalValueException;
import csdev.couponstash.commons.util.DateUtil;
import csdev.couponstash.logic.Logic;
import csdev.couponstash.model.coupon.Coupon;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * The ui for the Calendar that is displayed at the right of the application.
 */
public class CalendarPane extends UiPart<Region> {
    private static final Logger logger = LogsCenter.getLogger(CalendarPane.class);
    private static final String FXML = "CalendarPane.fxml";

    private static final int MAX_NUMBER_OF_WEEKS_TO_SHOW_PER_MONTH = 6;
    private static final int NUMBER_OF_DAYS_IN_A_WEEK = 7;
    private static final String MAX_YEAR_MESSAGE = "You have reached the maximum calendar year.";
    private static final String MIN_YEAR_MESSAGE = "You have reached the minimum calendar year.";
    private static final DateTimeFormatter DATE_FORMATTER = DateUtil.DATE_TIME_FORMATTER_FOR_CALENDAR;
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateUtil.MONTH_YEAR_FORMATTER_FOR_CALENDAR;
    private ArrayList<DateCell> dateCells;
    private YearMonth currentYearMonth;
    private ObservableList<Coupon> coupons;
    private Logic logic;

    @FXML
    private Text calendarPaneHeader;

    @FXML
    private Text calendarHeader;

    @FXML
    private GridPane calendarGrid;

    /**
     * Creates a new CalendarPane.
     */
    public CalendarPane(Logic logic) {
        super(FXML);
        calendarPaneHeader.setText(LocalDate.now().format(DATE_FORMATTER));
        currentYearMonth = YearMonth.now();
        dateCells = new ArrayList<>();
        this.coupons = logic.getFilteredCouponList();
        this.logic = logic;
        coupons.addListener((ListChangeListener<? super Coupon>) change -> fillUpCalendar());
        initializeUi();
        fillUpCalendar();
    }

    /**
     * Initializes the calendar.
     */
    private void initializeUi() {
        for (int i = 0; i < MAX_NUMBER_OF_WEEKS_TO_SHOW_PER_MONTH; i++) {
            for (int j = 0; j < NUMBER_OF_DAYS_IN_A_WEEK; j++) {
                DateCell dateCell = new DateCell(logic);
                addDateCellToArray(dateCell);
                StackPane calendarDateStackPane = dateCell.getCalendarDateStackPane();
                calendarGrid.add(calendarDateStackPane, j, i);
            }
        }
    }

    /**
     * Adds the specified {@DateCell} to the ArrayList of {@DateCells}.
     */
    private void addDateCellToArray(DateCell dateCell) {
        dateCells.add(dateCell);
    }

    /**
     * Fills up the calendar with current month and year.
     */
    private void fillUpCalendar() {
        updateCalendarTitle();

        LocalDate date = getDateOfFirstMonday();
        for (DateCell dateCell : dateCells) {
            setDateCell(dateCell, date); //sets DateCell's date, text, coupons and circle
            dateCell.addChildren();
            date = date.plusDays(1);
        }
    }

    /**
     * Updates the calendar's title to the current month and year.
     */
    private void updateCalendarTitle() {
        String calendarTitleText = currentYearMonth.format(MONTH_YEAR_FORMATTER);
        calendarHeader.setText(calendarTitleText);
    }

    /**
     * Returns a {@code LocalDate} instance of the first Monday before or on the first day of the month.
     *
     * @return A {@code LocalDate} instance of the first Monday.
     */
    private LocalDate getDateOfFirstMonday() {
        LocalDate dateOfFirstMonday = currentYearMonth.atDay(1);
        while (getDayOfWeekInInt(dateOfFirstMonday.getDayOfWeek().toString()) != 0) {
            dateOfFirstMonday = dateOfFirstMonday.minusDays(1);
        }
        return dateOfFirstMonday;
    }

    /**
     * Sets the display date of the specified {@code DateCell} to the specified {@code LocalDate}.
     *
     * @param dateCell The specified {@code DateCell}.
     * @param date     The specified {@code LocalDate}.
     */
    private void setDateCell(DateCell dateCell, LocalDate date) {
        StackPane dateStackPane = dateCell.getCalendarDateStackPane();
        dateStackPane.getChildren().clear();

        setDateCellDate(dateCell, date); //set DateCell's date
        addExpiringCoupons(dateCell); //add coupons expiring on the date
        setDateCellText(dateCell, date);
        setDateCellCircle(dateCell, date); //set DateCell's circle's color
    }

    /**
     * Sets the {@LocalDate} of the {@DateCell}.
     *
     * @param dateCell The specified {@DateCell}.
     * @param date     The specified {@LocalDate}
     */
    private void setDateCellDate(DateCell dateCell, LocalDate date) {
        dateCell.setDate(date);
    }

    /**
     * Adds all coupons expiring on the specified {@code LocalDate} to the list in the specified {@code DateCell}.
     *
     * @param dateCell The specified {@DateCell}.
     */
    private void addExpiringCoupons(DateCell dateCell) {
        dateCell.clearCoupons();
        LocalDate date = dateCell.getDate();
        for (Coupon coupon : coupons) {
            LocalDate couponExpiryDate = coupon.getExpiryDate().getDate();
            if (date.isEqual(couponExpiryDate)) {
                dateCell.addCoupon(coupon);
            }
        }
    }

    /**
     * Sets the formatted text of the specified {@DateCell}.
     *
     * @param dateCell The specified {@DateCell}.
     * @param date     The specified {@LocalDate}.
     */
    private void setDateCellText(DateCell dateCell, LocalDate date) {
        boolean visible = isVisible(date);

        Text dateText = new Text(String.format("%02d", date.getDayOfMonth()));
        if (visible) {
            if (dateIsInCurrentMonth(date)) {
                dateText.setFill(Paint.valueOf("#FFFFFF"));
            } else {
                dateText.setFill(Paint.valueOf("#4D4E4F"));
            }
        } else {
            dateText.setFill(Paint.valueOf("#000000"));
        }
        dateCell.setText(dateText);
    }

    /**
     * Returns true if the date is visible on the current Calendar.
     *
     * @param date Current date.
     * @return Boolean of whether the date is visible on the current Calendar.
     */
    private boolean isVisible(LocalDate date) {
        int numDaysToOverflow = getNumberOfDaysToOverflow();
        int dateOfMonth = date.getDayOfMonth();
        if (dateIsInNextMonth(date) && dateOfMonth > numDaysToOverflow) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the number of days in the next month allowed on the current Calendar.
     *
     * @return {@int} of the number of days in the next month allowed on the current Calender.
     */
    private int getNumberOfDaysToOverflow() {
        String lastDayOfTheMonth = (currentYearMonth.atDay(currentYearMonth.lengthOfMonth()))
                .getDayOfWeek().toString();
        return NUMBER_OF_DAYS_IN_A_WEEK - getDayOfWeekInInt(lastDayOfTheMonth) - 1;
    }

    /**
     * Sets the formatted circle of the specified {@DateCell}.
     *
     * @param dateCell The specified {@DateCell}.
     * @param date     The specified {@LocalDate}.
     */
    private void setDateCellCircle(DateCell dateCell, LocalDate date) {
        Circle circle = new Circle(15);
        StackPane.setAlignment(circle, Pos.CENTER);
        if (dateCell.getNumberOfCoupons() > 0 && dateIsInCurrentMonth(date)) {
            circle.setFill(Paint.valueOf("#02075D"));
        } else if (date.isEqual(LocalDate.now())) {
            circle.setFill(Paint.valueOf("#8D021F"));
        } else {
            circle = new Circle(0);
        }
        dateCell.setCircle(circle);
    }

    /**
     * Returns a boolean if the specified {@LocalDate} is in the current month.
     *
     * @param date The specified {@LocalDate}.
     * @return Boolean if the specified {@LocalDate} is in the current month.
     */
    private boolean dateIsInCurrentMonth(LocalDate date) {
        return date.getMonthValue() == currentYearMonth.getMonthValue();
    }

    /**
     * Returns a boolean if the specified {@LocalDate} is in the next month.
     *
     * @param date The specified {@LocalDate}.
     * @return Boolean if the specified {@LocalDate} is in the next month.
     */
    private boolean dateIsInNextMonth(LocalDate date) {
        return date.minusMonths(1).getMonthValue() == currentYearMonth.getMonthValue();
    }

    /**
     * Updates the calendar with next month's data.
     *
     * @throws IllegalValueException If the next month is after the maximum year.
     */
    public void updateCalendarWithNextMonth() throws IllegalValueException {
        if (DateUtil.isValidYear(currentYearMonth.plusMonths(1).getYear())) {
            closeAllDisplayedCouponWindows();
            currentYearMonth = currentYearMonth.plusMonths(1);
            fillUpCalendar();
            logger.info("Calendar showing next month.");
        } else {
            throw new IllegalValueException(MAX_YEAR_MESSAGE);
        }
    }

    /**
     * Updates the calendar with previous month's data.
     *
     * @throws IllegalValueException If the previous month is before the minimum year.
     */
    public void updateCalendarWithPreviousMonth() throws IllegalValueException {
        if (DateUtil.isValidYear(currentYearMonth.minusMonths(1).getYear())) {
            closeAllDisplayedCouponWindows();
            currentYearMonth = currentYearMonth.minusMonths(1);
            fillUpCalendar();
            logger.info("Calender showing previous month.");
        } else {
            throw new IllegalValueException(MIN_YEAR_MESSAGE);
        }
    }

    /**
     * Closes all displayed coupon windows.
     */
    private void closeAllDisplayedCouponWindows() {
        for (DateCell dateCell : dateCells) {
            dateCell.closeDisplayedCoupons();
        }
    }

    /**
     * Returns an integer corresponding to the specified day of {@String}.
     *
     * @param day The specified Day {@String}
     */
    private int getDayOfWeekInInt(String day) {
        int dayInInt;
        switch (day) {
        case "MONDAY":
            dayInInt = 0;
            break;
        case "TUESDAY":
            dayInInt = 1;
            break;
        case "WEDNESDAY":
            dayInInt = 2;
            break;
        case "THURSDAY":
            dayInInt = 3;
            break;
        case "FRIDAY":
            dayInInt = 4;
            break;
        case "SATURDAY":
            dayInInt = 5;
            break;
        case "SUNDAY":
            dayInInt = 6;
            break;
        default:
            dayInInt = 0;
            break;
        }
        return dayInInt;
    }

    /**
     * Changes the calendar's display to the previous month.
     */
    @FXML
    private void changeCalendarToPreviousMonth() {
        try {
            updateCalendarWithPreviousMonth();
        } catch (IllegalValueException e) {
            return;
        }
    }

    /**
     * Changes the calendar's display to the next month.
     */
    @FXML
    private void changeCalendarToNextMonth() {
        try {
            updateCalendarWithNextMonth();
        } catch (IllegalValueException e) {
            return;
        }
    }
}
