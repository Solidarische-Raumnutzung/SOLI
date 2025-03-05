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
    <div class="container">
        <h1 class="text-4xl font-bold">${context.localize("layout.publisher")}</h1>
        <p>${context.localize("projectDescription")}</p>

        <br>

        <p>${context.localize("layout.publisherInfo")}</p>
        <a class="link link-primary " href="https://hci.iar.kit.edu/deutsch/index.php">HCI</a>

        <br><br>

        <h1 class="text-4xl font-bold">${context.localize("rooms")}</h1>
        @template.util.room_list(context = context, layout = layout, csrf = csrf, rooms = rooms)
    </div>
`)
