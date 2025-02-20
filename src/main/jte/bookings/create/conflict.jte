@import edu.kit.hci.soli.domain.Booking
@import edu.kit.hci.soli.dto.BookingAttemptResult
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param Booking attemptedBooking
@param BookingAttemptResult.PossibleCooperation bookingResult

@template.layout(page = context.page("bookings.conflict.resolution"), context = context, layout = layout, content = @`
    <div class="mx-auto hero-content text-center">
        <div class="max-w-md">
            <h1 class="text-2xl font-bold mb-2">${context.localize("bookings.conflict.resolution")}</h1>

            <ul class="list bg-base-100 rounded-box shadow-md text-left">
                <li class="mb-4">${context.localize("bookings.conflict.recommend")}</li>

                @if (!bookingResult.contact().isEmpty())
                    <h2>${context.localize("bookings.conflict.contact")}</h2>
                    @for (Booking booking : bookingResult.contact())
                        <li class="list-row">
                            <div class="text-xs uppercase font-semibold opacity-60 list-col-grow">
                                ${context.format(booking.getStartDate())} - ${context.format(booking.getEndDate().toLocalTime())}
                            </div>
                            <p class="list-col-wrap">
                                ${booking.getDescription().isBlank() ? context.lookup("bookings.description.none") : booking.getDescription()}
                            </p>
                            <a class="btn btn-square btn-ghost" href="/${booking.getRoom().getId()}/bookings/${booking.getId()}" target="_blank" aria-label="${context.localize("bookings.view")}">
                                <i class="fa-solid fa-eye"></i>
                            </a>
                        </li>
                    @endfor
                @endif

                @if (!bookingResult.override().isEmpty())
                    <h2>${context.localize("bookings.conflict.delete")}</h2>
                    <p class="text-gray-500 text-sm">${context.localize("bookings.conflict.delete.message")}</p>
                    @for (Booking booking : bookingResult.override())
                        <li class="list-row">
                            <div class="text-xs uppercase font-semibold opacity-60 list-col-grow">
                                ${context.format(booking.getStartDate())} - ${context.format(booking.getEndDate().toLocalTime())}
                            </div>
                            <p class="list-col-wrap">
                                ${booking.getDescription().isBlank() ? context.lookup("bookings.description.none") : booking.getDescription()}
                            </p>
                            <a class="btn btn-square btn-ghost" href="/${booking.getRoom().getId()}/bookings/${booking.getId()}" target="_blank" aria-label="${context.localize("bookings.view")}">
                                <i class="fa-solid fa-eye"></i>
                            </a>
                        </li>
                    @endfor
                @endif

                @if (!bookingResult.cooperate().isEmpty())
                    <h2>${context.localize("bookings.conflict.cooperate")}</h2>
                    <p class="text-gray-500 text-sm">${context.localize("bookings.conflict.cooperate.message")}</p>
                    @for (Booking booking : bookingResult.cooperate())
                        <li class="list-row">
                            <div class="text-xs uppercase font-semibold opacity-60 list-col-grow">
                                ${context.format(booking.getStartDate())} - ${context.format(booking.getEndDate().toLocalTime())}
                            </div>
                            <p class="list-col-wrap">
                                ${booking.getDescription().isBlank() ? context.lookup("bookings.description.none") : booking.getDescription()}
                            </p>
                            <a class="btn btn-square btn-ghost" href="/${booking.getRoom().getId()}/bookings/${booking.getId()}" target="_blank" aria-label="${context.localize("bookings.view")}">
                                <i class="fa-solid fa-eye"></i>
                            </a>
                        </li>
                    @endfor
                @endif
            </ul>

            <div class="modal-action">
                <a class="btn" href="/">${context.localize("generic.cancel")}</a>
                @template.util.form(csrf = csrf, action = "/" + attemptedBooking.getRoom().getId() + "/bookings/new/conflict", method = POST, content = @`
                    <button class="btn btn-primary" type="submit" title="${context.localize("bookings.conflict.solve.tooltip")}">${context.localize("bookings.conflict.solve")}</button>
                `)
            </div>
        </div>
    </div>
`)
