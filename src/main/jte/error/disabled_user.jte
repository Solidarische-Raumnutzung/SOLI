@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import java.util.Date

@param JteContext context
@param LayoutParams layout

@param String message
@param String error
@param Date timestamp

@template.layout(page = context.page("error"), context = context, layout = layout, content = @`
    <div class="hero-content text-center mx-auto">
        <div class="max-w-md">
            <h1 class="text-5xl font-bold">${context.localize("user.deactivated.title")}</h1>
            <p class="py-6">${context.localize("user.deactivated.message")}</p>
            <a class="btn btn-primary" href="/">${context.localize("index.go")}</a>
        </div>
    </div>
`)