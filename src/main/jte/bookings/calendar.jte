@import edu.kit.hci.soli.domain.Priority
@import edu.kit.hci.soli.domain.TimeTuple
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.config.template.JteContext
@import java.time.DayOfWeek
@import java.util.Comparator

@param JteContext context
@param LayoutParams layout

@template.layout(page = context.page("calendar"), screenHeight = true, context = context, layout = layout, content = @`
    <script src="https://cdn.jsdelivr.net/npm/@fullcalendar/core@6.1.15/index.global.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@fullcalendar/interaction@6.1.15/index.global.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@fullcalendar/daygrid@6.1.15/index.global.min.js"></script> <%-- dependency of timegrid --%>
    <script src="https://cdn.jsdelivr.net/npm/@fullcalendar/timegrid@6.1.15/index.global.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/ical.js@1.5.0/build/ical.min.js"></script> <%-- dependency of fc/icalendar --%>
    <script src="https://cdn.jsdelivr.net/npm/@fullcalendar/icalendar@6.1.15/index.global.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@fullcalendar/core@6.1.15/locales/de.global.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const priorityNames = {
                @for (var priority : Priority.values())
                    ${priority}: "${context.localize("bookings.priority." + priority)}",
                @endfor
            }
            const calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
                timeZone: '${context.getTimeZone().getID()}',
                initialView: window.matchMedia("(width < 64rem)").matches ? 'timeGridDay' : 'timeGridWeek',
                nowIndicator: true,
                weekends: false,
                firstDay: 6, // The week starts on Saturday (show next week on weekends)
                allDaySlot: false,
                displayEventTime: false,
                eventContent: function(arg) {
                    let div = document.createElement('div');
                    let collaboration_icon = document.createElement('i');
                    div.className += 'fc-event-title fc-sticky';
                    div.appendChild(collaboration_icon);

                    let arrayOfDomNodes = [ div ];

                    if (arg.event.extendedProps.shareRoomType === 'YES') {
                        collaboration_icon.className += 'fa-solid fa-users';
                        collaboration_icon.title = "${context.localize("bookings.collaboration.YES.tooltip")}";
                        collaboration_icon.ariaLabel = "${context.localize("bookings.collaboration.YES")}";
                    } else if (arg.event.extendedProps.shareRoomType === 'NO') {
                        collaboration_icon.className += 'fa-solid fa-users-slash';
                        collaboration_icon.title = "${context.localize("bookings.collaboration.NO.tooltip")}";
                        collaboration_icon.ariaLabel = "${context.localize("bookings.collaboration.NO")}";
                    } else {
                        collaboration_icon.className += 'fa-solid fa-question';
                        collaboration_icon.title = "${context.localize("bookings.collaboration.ON_REQUEST.tooltip")}";
                        collaboration_icon.ariaLabel = "${context.localize("bookings.collaboration.ON_REQUEST")}";
                        let collaboration_icon2 = document.createElement('i');
                        collaboration_icon2.className += 'fa-solid fa-users';
                        div.appendChild(collaboration_icon2);
                    }

                    if (arg.event.extendedProps.favorite) {
                        let favorite_icon = document.createElement('i');
                        favorite_icon.className += ' fa-solid fa-star absolute top-1 right-1';
                        favorite_icon.ariaLabel = "${context.localize("bookings.favorite")}";
                        favorite_icon.title = "${context.localize("bookings.favorite.tooltip")}";
                        div.appendChild(favorite_icon);
                    }

                    let badge = document.createElement('div');
                    div.appendChild(badge);

                    badge.className += 'badge ml-2 badge-sm';
                    badge.textContent = priorityNames[arg.event.extendedProps.priority];
                    badge.title = "${context.localize("bookings.priority.tooltip2")}";

                    return { domNodes: arrayOfDomNodes }
                }
                ,
                locale: "${context.getLocale().getLanguage()}",
                height: "100%",
                slotLabelFormat: {
                    hour: '2-digit',
                    minute: '2-digit',
                    omitZeroMinute: false
                },
                dayHeaderFormat: {
                    weekday: 'short',
                    day: '2-digit',
                    month: '2-digit',
                    omitCommas: true
                },
                headerToolbar: {
                    left: 'prev,next today',
                    center: 'title',
                    right: window.matchMedia("(width < 64rem)").matches ? null : 'timeGridWeek,timeGridDay'
                },
                themeSystem: 'bootstrap5',
                selectable: true,
                select: function (info) {
                    window.location.href = "/${layout.getRoom().getId()}/bookings/new"
                        + "?start=" + encodeURIComponent(info.start.toISOString())
                        + "&end=" + encodeURIComponent(info.end.toISOString());
                },
                selectOverlap: function (event) {
                    return !event.classNames.includes('calendar-event-no');
                },
                selectAllow: function (selectInfo) {
                    // Keep these in sync with BookingsController!
                    let now = (new Date().getTime() + ${context.getTimeZone().getRawOffset()});
                    now -= now % (15 * 60 * 1000);
                    return selectInfo.start >= new Date(now + 15 * 60 * 1000)
                        && selectInfo.end < new Date(selectInfo.start.getTime() + 4 * 60 * 60 * 1000)
                        && selectInfo.end < new Date(now + 14 * 24 * 60 * 60 * 1000);
                },
                selectConstraint: 'businessHours',
                slotDuration: '00:15:00',
                slotMinTime: '${layout.getRoom().getOpeningHours().values().stream().map(TimeTuple::getStart).min(Comparator.naturalOrder()).orElseThrow().toString()}',
                slotMaxTime: '${layout.getRoom().getOpeningHours().values().stream().map(TimeTuple::getEnd).max(Comparator.naturalOrder()).orElseThrow().toString()}',
                eventSources: [
                    '/api/${layout.getRoom().getId()}/events',
                    {
                        url: '/api/holidays.ics',
                        format: 'ics',
                        display: 'background',
                        className: 'calendar-event-holiday'
                    }
                ],
                businessHours: [
                    <%-- To future readers: a REST endpoint to generate this would be nicer and I know that FullCalendar supports this. --%>
                    <%-- Unfortunately, using that appears to require some form of premium subscription, so this will have to do. --%>
                    @for (var day : DayOfWeek.values())
                    @if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
                    !{var tuple = layout.getRoom().getOpeningHours().get(day);}
                    {
                        daysOfWeek: [${day.getValue()}],
                        startTime: '${tuple.getStart().toString()}',
                        endTime: '${tuple.getEnd().toString()}'
                    },
                    @endif
                    @endfor
                ]
            });

            calendar.render();
            setInterval(() => {
                calendar.refetchEvents();
            }, 1000 * 60 * 5);
        });
    </script>

    <div id="calendar" class="w-full"></div>

    <noscript class="w-full">
        <h1 class="text-center">
            ${context.localize("calendar.no_js")}
        </h1>
    </noscript>
`)
