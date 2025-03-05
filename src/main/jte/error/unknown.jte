@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext

@param JteContext context
@param LayoutParams layout

@param String message = null
@param String error = null

@template.layout(page = context.page("error"), context = context, layout = layout, content = @`
    <div class="hero-content text-center mx-auto">
        <div class="max-w-md">
            <h1 class="text-5xl font-bold">${context.localize("error.unknown.title")}</h1>
            <p class="py-6">${context.localize("error.unknown.message")}</p>
            <details class="collapse collapse-plus bg-base-200">
                <summary class="collapse-title font-medium">${context.localize("error.unknown.details")}</summary>
                <div class="collapse-content">
                    <p>${error == null ? message != null ? message : context.lookup("error.unknown.fallback") : error}</p>
                </div>
            </details>
            <a class="btn btn-primary" href="/">${context.localize("index.go")}</a>
        </div>
    </div>
`)