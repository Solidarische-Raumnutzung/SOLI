@import edu.kit.hci.soli.config.template.JteContext
@import edu.kit.hci.soli.domain.Booking

@param JteContext context
@param String css

@param Booking booking

@template.mail.layout(title = context.lookup("mail.open_requests_cleared.heading"), context = context, css = css, content = @`
    <p>
        ${context.localize("mail.open_requests_cleared.message", booking.getUser().getUsername(), booking.getRoom().getId(), booking.getStartDate().toString())}
    </p>
    <p>
        ${context.localize("mail.open_requests_cleared.last_phrase")}
    </p>
    <p>
        ${context.localize("mail.ending")}
    </p>
    <p>
        <a class="btn btn-accent" href="${context.getHostname()}${booking.getRoom().getId()}/bookings/${booking.getId()}">
            ${context.localize("bookings.view")}
        </a>
    </p>
`)
