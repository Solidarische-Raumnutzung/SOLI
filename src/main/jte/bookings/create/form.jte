@import edu.kit.hci.soli.domain.Priority
@import edu.kit.hci.soli.domain.ShareRoomType
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.security.web.csrf.CsrfToken
@import static org.springframework.http.HttpMethod.*
@import java.time.LocalDateTime
@import java.time.LocalTime

@param JteContext context
@param LayoutParams layout
@param CsrfToken csrf

@param LocalDateTime start
@param LocalTime end
@param ShareRoomType cooperative

@param LocalDateTime minimumStart
@param LocalDateTime maximumStart
@param LocalTime minimumEnd
@param LocalTime maximumEnd

@template.layout(page = context.page("bookings.new"), context = context, layout = layout, content = @`
    <fieldset class="fieldset w-80 mx-auto bg-base-200 border border-base-300 p-4 rounded-box">
        <legend class="fieldset-legend">${context.localize("bookings.new")}</legend>

        @template.util.form(csrf = csrf, action = "/" + layout.getRoom().getId() + "/bookings/new", method = POST, content = @`
            <label class="fieldset-legend" for="start">${context.localize("bookings.start")} </label>
            <input id="start" name="start" type="datetime-local" class="input validator"
                   value="${start.toString()}"
                   min="${minimumStart.toString()}"
                   max="${maximumStart.toString()}"
                   step="${60 * 15}"
                   required
                   onchange="validateEndTime()"
                   title="${context.localize("bookings.start.tooltip")}"
            />
        <div class="validator-hint">${context.localize("bookings.start.hint")}</div>

            <label class="fieldset-legend" for="end">${context.localize("bookings.end")} </label>
            <input id="end" name="end" type="time" class="input validator"
                   value="${end.toString()}"
                   min="${minimumEnd.toString()}"
                   max="${maximumEnd.toString()}"
                   step="${60 * 15}"
                   required
                   title="${context.localize("bookings.end.tooltip")}"
            />
        <div class="validator-hint">${context.localize("bookings.end.hint")}</div>

            <script>
                const dows = [];
                @for (var dow : layout.getRoom().getOpeningHours().entrySet())
                    dows[${context.index(dow.getKey())}] = ${dow.getValue().getEnd().toSecondOfDay() * 1000};
                @endfor
                function validateEndTime() {
                    const start = new Date(document.getElementById("start").value);
                    const endEl = document.getElementById('end');
                    endEl.min = new Date(start.getTime() + 15*60000).toLocaleTimeString([], {hour: "2-digit", minute: "2-digit"});
                    const endMax1 = start.getTime() + 4 * 60 * 60 * 1000;
                    start.setHours(0, 0, 0, 0);
                    const endMax2 = start.getTime() + dows[start.getDay()];
                    endEl.max = new Date(Math.min(endMax1, endMax2)).toLocaleTimeString([], {hour: "2-digit", minute: "2-digit"});
                    const val = new Date("1970-01-01T" + endEl.value);
                    if (val < new Date("1970-01-01T" + endEl.min)) endEl.value = endEl.min;
                    if (val > new Date("1970-01-01T" + endEl.max)) endEl.value = endEl.max;
                }
                validateEndTime()
            </script>

            <br><br>

        <div class="block" title="${context.localize("bookings.description.tooltip")}">
            <textarea id="description" name="description" class="input validator textarea textarea-bordered"
                      placeholder="${context.localize("bookings.description")}"
                      maxlength="1024"
            ></textarea>
            <label class="label">
                <span class="label-text-alt">${context.localize("bookings.optional")}</span>
            </label>
        </div>

            <br><br>

            <div class="block" title="${context.localize("bookings.priority.tooltip")}">
                <label class="fieldset-legend" for="priority">${context.localize("bookings.priority")}</label>
                <select name="priority" class="select select-bordered" required>
                    @for(Priority priority : Priority.values())
                        <option
                                value="${priority}"
                                selected="${priority == Priority.LOWEST}"
                        >${context.localize("bookings.priority." + priority)}</option>
                    @endfor
                </select>
            </div>

            <br>

            <div title="${context.localize("bookings.share.tooltip")}">
                <label class="fieldset-legend" for="cooperative">${context.localize("bookings.share")}</label>
                @for(ShareRoomType shareRoomType : ShareRoomType.values())
                    <label class="fieldset-label">
                        <input type="radio" name="cooperative" class="radio"
                               value="${shareRoomType}"
                               checked="${shareRoomType == cooperative}"
                        />
                        ${context.localize("bookings.share." + shareRoomType)}
                    </label>
                @endfor
            </div>

            <div class="divider"></div>
            <p class="text-gray-500 text-sm">${context.localize("bookings.who_can_see_answer")}</p>
            <br>

            <input type="submit" class="input btn btn-primary" value="OK">
        `)
    </fieldset>
`)
