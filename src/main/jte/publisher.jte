@import edu.kit.hci.soli.domain.Room
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import java.util.List

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param List<Room> rooms

@template.layout(page = context.page("layout.publisher"), context = context, layout = layout, content = @`
    <style>
        h1 {
            font-size: 2.5em; /* Make the h1 much larger */
            font-weight: bold;
        }
        p {
            font-size: 1em;
        }
    </style>

    <div class="container">
        <h1>Impressum</h1>
        <p>${context.localize("layout.publisherInfo")}</p>
        <p>Description of project</p>
        <p>Link to hci webpage</p>
        <h1>Räume</h1>
        @template.util.room_list(context = context, layout = layout, csrf = csrf, rooms = rooms)
    </div>
`)
