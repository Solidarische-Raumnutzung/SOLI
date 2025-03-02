@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*

@param JteContext context
@param CsrfToken csrf
@param LayoutParams layout

@param String error
@param String message
@param boolean guestEnabled

@template.layout(page = context.page("login.login"), context = context, layout = layout, content = @`
    <fieldset class="fieldset w-80 mx-auto bg-base-200 border border-base-300 p-4 rounded-box">
        <legend class="fieldset-legend">${context.localize("login.login")}</legend>
        @if (error != null)
            <div role="alert" class="alert alert-error">
                <i class="fas fa-xmark"></i>
                <span>${context.localizeOrDefault(error, error)}</span>
            </div>
        @endif
        @if (message != null)
            <div role="alert" class="alert alert-info">
                <i class="fas fa-circle-info"></i>
                <span>${context.localizeOrDefault(message, message)}</span>
            </div>
        @endif
        <a class="input btn btn-primary" href="/oauth2/authorization/kit" title="${context.localize("login.kit.description")}">${context.localize("login.kit")}</a>
        @if(guestEnabled)
            <a class="input btn btn-secondary" href="/login/guest" title="${context.localize("login.guest.description")}">${context.localize("login.guest")}</a>
        @endif
        <br>
        <details class="collapse collapse-plus">
            <summary class="collapse-title font-medium">${context.localize("login.admin")}</summary>
            @template.util.form(csrf = csrf, cssClass = "collapse-content fieldset", action = "/login", method = POST, content = @`
                <input id="username" name="username" type="hidden" value="admin"/>
                <label class="fieldset-label" for="password">${context.localize("login.password")}</label>
                <input id="password" name="password" type="password" placeholder="${context.localize("login.password")}" class="input" required/>

                <label class="fieldset-legend text-xs items-center">
                    <input id="remember-me" name="remember-me" type="checkbox" class="toggle" checked/>
                    ${context.localize("login.remember")}
                </label>

                <input class="input btn btn-secondary" type="submit" value="${context.localize("login.login")}">
            `)
        </details>
    </fieldset>
`)
