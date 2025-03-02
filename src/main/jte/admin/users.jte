@import edu.kit.hci.soli.domain.User
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.data.domain.Page
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*
@import java.util.UUID

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param Page<User> users
@param boolean guestsEnabled

@template.layout(page = context.page("administration.user"), context = context, layout = layout, content = @`
    <div class="overflow-x-auto">
        @if (guestsEnabled)
            !{var id = "modal_" + UUID.randomUUID().toString().replace("-", "");}
            @template.util.modal.open(message = context.localize("administration.user.guests.disable"), style = "btn btn-error", id = id, alternate = "/admin/users/disable-guests", content = @`
                @template.admin.confirmation.disable_guests(context = context, csrf = csrf, id = id)
            `)
        @else
            @template.util.form(csrf = csrf, cssClass = "", action = "/admin/users/enable-guests", method = PUT, content = @`
                <input class="btn btn-info" type="submit" value="${context.localize("administration.user.guests.enable")}">
            `)
        @endif
        @if (users.isEmpty())
            <div class="card">
                <div class="card-body">
                    <p class="text-center">${context.localize("administration.user.empty")}</p>
                </div>
            </div>
        @else
            <table class="table table-zebra">
                <thead>
                <tr>
                    <th></th>
                    <th>${context.localize("administration.user.username")}</th>
                    <th>${context.localize("administration.user.email")}</th>
                    <th>${context.localize("administration.user.user_id")}</th>
                    <th>${context.localize("administration.user.is_disabled")}</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                @for (User user : users.getContent())
                    <tr>
                        <td>${user.getId()}</td>
                        <td>${user.getUsername()}</td>
                        <td>${user.getEmail()}</td>
                        <td>${user.getUserId()}</td>
                        @if(user.isDisabled())
                            <td>${context.localize("generic.true")}</td>
                        @else
                            <td>${context.localize("generic.false")}</td>
                        @endif
                        <td>
                            @if (user.isDisabled())
                                @template.util.form(csrf = csrf, action = "/admin/users/" + user.getId() + "/reactivate", method = PUT, content = @`
                                    <button type="submit" class="btn btn-ghost">${context.localize("administration.user.reactivate")}</button>
                                `)
                            @else
                                !{var id = "modal_" + UUID.randomUUID().toString().replace("-", "");}
                                @template.util.modal.open(message = context.localize("administration.user.deactivate"), id = id, alternate = "/admin/users/" + user.getId() + "/deactivate", content = @`
                                    @template.admin.confirmation.deactivate_user(context = context, csrf = csrf, id = id, user = user)
                                `)
                            @endif
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
            <div>
                @template.util.pagination(context = context, content = users, baseUrl = "/admin/users")
            </div>
        @endif
    </div>
`)
