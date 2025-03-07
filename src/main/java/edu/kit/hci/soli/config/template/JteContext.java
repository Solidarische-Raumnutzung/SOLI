package edu.kit.hci.soli.config.template;

import edu.kit.hci.soli.domain.User;
import edu.kit.hci.soli.dto.LoginStateModel;
import edu.kit.hci.soli.service.UserService;
import gg.jte.Content;
import gg.jte.support.LocalizationSupport;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.springframework.context.MessageSource;

import java.text.DateFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TimeZone;

public class JteContext implements LocalizationSupport {
    private final MessageSource messageSource;
    @Getter private final String hostname;
    @Getter private final Locale locale;
    private final DateFormatSymbols symbols;
    @Getter private final TimeZone timeZone;
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter timeFormatter;
    private final @Nullable UserService userService;

    public JteContext(MessageSource messageSource, String hostname, Locale locale, TimeZone timeZone, @Nullable UserService userService) {
        this.messageSource = messageSource;
        this.hostname = hostname;
        this.locale = locale;
        this.symbols = new DateFormatSymbols(locale == null ? Locale.getDefault() : locale);
        this.dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(locale)
                .withZone(timeZone.toZoneId());
        this.timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(locale)
                .withZone(timeZone.toZoneId());
        this.timeZone = timeZone;
        this.userService = userService;
    }

    @Override
    public String lookup(String key) {
        return messageSource.getMessage(key, null, locale);
    }

    @Override
    public Content localize(String key) {
        return asContent(messageSource.getMessage(key, null, locale));
    }

    @Override
    public Content localize(String key, Object... params) {
        return asContent(messageSource.getMessage(key, params, locale));
    }

    public Content localizeOrDefault(String key, String defaultValue, Object... params) {
        return asContent(messageSource.getMessage(key, params, defaultValue, locale));
    }

    private Content asContent(String value) {
        return output -> output.writeUserContent(value);
    }

    public Content empty() {
        return output -> {};
    }

    public int index(DayOfWeek day) {
        return day == DayOfWeek.SUNDAY ? 0 : day.getValue();
    }

    public String format(DayOfWeek day) {
        return symbols.getWeekdays()[index(day) + 1];
    }

    public String format(Month month) {
        return symbols.getMonths()[month.getValue() - 1];
    }

    public String format(LocalDateTime dateTime) {
        return dateTimeFormatter.format(dateTime);
    }

    public String format(LocalTime time) {
        return timeFormatter.format(time);
    }

    public PageSpec page(@PropertyKey(resourceBundle = "messages") String key) {
        return new PageSpec(lookup(key), "SOLI");
    }

    public String format(LoginStateModel login) {
        return switch (login.kind()) {
            case VISITOR -> lookup("user.visitor");
            case GUEST -> lookup("user.guest");
            case ADMIN -> lookup("user.admin");
            default -> format(login.user());
        };
    }

    public String format(User user) {
        if (user == null) return lookup("user"); // We don't know the user
        if (userService != null) {
            if (userService.isGuest(user)) return lookup("user.guest");
            else if (userService.isAdmin(user)) return lookup("user.admin");
        }
        return user.getUsername();
    }
}
