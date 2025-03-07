@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param Long id
@param String name
@param String description
@param String location

!{var title = id == null ? "rooms.new" : "rooms.edit";}
@template.layout(page = context.page(title), context = context, layout = layout, content = @`
    <fieldset class="fieldset w-80 mx-auto bg-base-200 border border-base-300 p-4 rounded-box">
        <legend class="fieldset-legend">${context.localize(title)}</legend>

        @template.util.form(csrf = csrf, action = "/admin/rooms", method = POST, content = @`
            <input type="hidden" name="target" value="${id}">

            <label class="fieldset-legend" for="name">${context.localize("rooms.name")}</label>
            <input class="input" id="name" name="name" type="text" placeholder="${context.localize("rooms.name")}" value="${name}" required/>

            <br><br>

            <label class="fieldset-legend" for="description">${context.localize("rooms.description")}</label>
            <textarea id="description" name="description" class="input textarea textarea-bordered"
                      placeholder="${context.localize("rooms.description")}"
                      title="${context.localize("rooms.description.tooltip")}"
            >${description}</textarea>

            <br><br>

            <label class="fieldset-legend" for="location">${context.localize("rooms.location")}</label>
            <input class="input" id="location" name="location" type="text" placeholder="${context.localize("rooms.location")}" value="${location}" required/>

            <br><br>

            <input type="submit" class="input btn btn-primary" value="${context.localize("rooms.save")}">
        `)
    </fieldset>
`)
