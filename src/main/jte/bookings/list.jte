@import edu.kit.hci.soli.domain.Booking
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.data.domain.Page
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param Page<Booking> bookings

@template.layout(page = context.page("bookings.my_bookings"), context = context, layout = layout, content = @`
    <div class="overflow-x-auto">
        @if(bookings.isEmpty())
            <div class="card">
                <div class="card-body">
                    <p class="text-center">${context.localize("bookings.none")}</p>
                </div>
            </div>
        @else
            <table class="table table-zebra">
                <thead>
                <tr>
                    <th></th>
                    <th>${context.localize("bookings.description")}</th>
                    <th>${context.localize("bookings.start")}</th>
                    <th>${context.localize("bookings.end")}</th>
                    <th>${context.localize("bookings.share")}</th>
                    <th>${context.localize("bookings.priority")}</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                @for(Booking booking : bookings.getContent())
                    <tr>
                        <td>${booking.getId()} ${booking.getOpenRequests().isEmpty() ? context.empty() : context.localize("bookings.outstanding", booking.getOpenRequests().size())}</td>
                        <td>${booking.getDescription()}</td>
                        <td>${context.format(booking.getStartDate())}</td>
                        <td>${context.format(booking.getEndDate())}</td>
                        <td>${context.localize("bookings.share." + booking.getShareRoomType())}</td>
                        <td>${context.localize("bookings.priority." + booking.getPriority())}</td>
                        <td>
                            @template.util.form(csrf = csrf, action = "/" + booking.getRoom().getId() + "/bookings/" + booking.getId(), method = DELETE, content = @`
                                <button type="submit" class="btn btn-ghost" title="${context.localize("bookings.delete.tooltip")}">
                                    <i class="fas fa-trash-alt"></i>
                                    ${context.localize("bookings.delete")}
                                </button>
                            `)
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
            <div>
                @template.util.pagination(context = context, content = bookings, baseUrl = "/" + layout.getRoom().getId() + "/bookings")
            </div>
        @endif
    </div>
`)
