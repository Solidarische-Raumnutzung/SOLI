@import edu.kit.hci.soli.domain.Room
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import java.util.List

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param List<Room> rooms
@param boolean edit = false

@template.layout(page = context.page("rooms"), layout = layout, context = context, content = @`
    <div class="mx-auto">
        @template.util.room_list(context = context, layout = layout, csrf = csrf, rooms = rooms, edit = edit)
    </div>
`)
