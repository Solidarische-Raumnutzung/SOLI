@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext

@param JteContext context
@param LayoutParams layout

@template.layout(page = context.page("layout.publisher"), context = context, layout = layout, content = @`
    <div class="container">
        <h1>${context.localize("layout.publisherInfo")}</h1>
        <h2>TBD.</h2>
    </div>
`)
