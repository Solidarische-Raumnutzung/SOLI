@import edu.kit.hci.soli.domain.Booking
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param Booking booking
@param boolean allowEditing
@param boolean showRequestButton

@template.layout(page = context.page("bookings.view.page"), context = context, layout = layout, content = @`
    <div class="card bg-base-200 border border-base-300 w-96 mx-auto">
        <div class="card-body items-center text-center">
            @template.bookings.single(context = context, booking = booking, csrf = csrf, allowEditing = allowEditing, showRequestButton = showRequestButton)
        </div>
    </div>
`)