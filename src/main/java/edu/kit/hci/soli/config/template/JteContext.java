package edu.kit.hci.soli.config.template;

import edu.kit.hci.soli.domain.User;
import edu.kit.hci.soli.dto.LoginStateModel;
import edu.kit.hci.soli.service.UserService;
import gg.jte.Content;
import gg.jte.support.LocalizationSupport;
import lombok.Getter;
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
    private final UserService userService;

    public JteContext(MessageSource messageSource, String hostname, Locale locale, TimeZone timeZone, UserService userService) {
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
        String result = messageSource.getMessage(key, null, locale);
        return output -> output.writeUserContent(result);
    }

    @Override
    public Content localize(String key, Object... params) {
        String result = messageSource.getMessage(key, params, locale);
        return output -> output.writeUserContent(result);
    }

    public Content localizeOrDefault(String key, String defaultValue, Object... params) {
        String result = messageSource.getMessage(key, params, defaultValue, locale);
        return output -> output.writeUserContent(result);
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

    public Content format(LoginStateModel login) {
        switch (login.kind()) {
            case VISITOR -> {return this.localize("user.visitor");}
            case GUEST -> {return this.localize("user.guest");}
            case ADMIN -> {return this.localize("user.admin");}
            default -> {return login.name();}
        }
    }

    public Content format(User user) {
        if (userService.isGuest(user)) {
            return this.localize("user.guest");
        } else if (userService.isAdmin(user)) {
            return this.localize("user.admin");
        } else {
            return user.getUsername();
        }
    }
}
