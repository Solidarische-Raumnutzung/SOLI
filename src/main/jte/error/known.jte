@import edu.kit.hci.soli.dto.KnownError
@import edu.kit.hci.soli.config.template.JteContext
@import edu.kit.hci.soli.dto.LayoutParams

@param JteContext context
@param LayoutParams layout

@param KnownError error

@template.layout(page = context.page(error.title), context = context, layout = layout, content = @`
    <div class="hero-content text-center mx-auto">
        <div class="max-w-md">
            <h1 class="text-5xl font-bold">${context.localize(error.title)}</h1>
            <p class="py-6">${context.localize(error.message)}</p>
            <a class="btn btn-primary" href="/">${context.localize("index.go")}</a>
        </div>
    </div>
`))