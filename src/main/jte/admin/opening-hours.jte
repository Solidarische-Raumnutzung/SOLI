@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*
@import java.time.DayOfWeek

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@template.layout(page = context.page("administration.opening_hours"), context = context, layout = layout, content = @`
    <div class="overflow-x-auto">
        @template.util.form(csrf = csrf, action = "/admin/opening-hours/" + layout.getRoom().getId(), method = PUT, content = @`
            <table class="table table-zebra w-full">
                <thead>
                <tr>
                    <th>${context.localize("administration.opening_hours.day")}</th>
                    <th>${context.localize("administration.opening_hours.start_time")}</th>
                    <th>${context.localize("administration.opening_hours.closing_time")}</th>
                </tr>
                </thead>
                <tbody>
                @for (DayOfWeek day : DayOfWeek.values())
                    @if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
                        !{var tuple = layout.getRoom().getOpeningHours().get(day);}
                        !{var name = day.toString().toLowerCase();}
                        <tr>
                            <td>${context.format(day)}</td>
                            <td><input type="time" class="input validator w-full"
                                       aria-label="${context.localize("administration.opening_hours.start", context.format(day))}"
                                       name="${name}Start"
                                       value="${tuple.getStart().toString()}"
                                       min="00:00"
                                       max="23:45"
                                       step="${60 * 15}"
                                       required
                                       title="${context.localize("administration.opening_hours.start_time.tooltip")}"
                                ><div class="validator-hint">${context.localize("administration.opening_hours.start_time.hint")}</div>
                            </td>
                            <td><input type="time" class="input validator w-full"
                                       aria-label="${context.localize("administration.opening_hours.end", context.format(day))}"
                                       name="${name}End"
                                       value="${tuple.getEnd().toString()}"
                                       min="00:00"
                                       max="23:45"
                                       step="${60 * 15}"
                                       required
                                       title="${context.localize("administration.opening_hours.closing_time.tooltip")}"
                                ><div class="validator-hint">${context.localize("administration.opening_hours.closing_time.hint")}</div>
                            </td>
                            <input type="hidden" name="dayOfWeek" value="${day}">
                        </tr>
                    @endif
                @endfor
                </tbody>
            </table>
            <input type="submit" class="input btn btn-primary" value="${context.localize("administration.opening_hours.save")}">
        `)
    </div>
`)