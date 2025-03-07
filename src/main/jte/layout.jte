@import edu.kit.hci.soli.domain.ShareRoomType
@import edu.kit.hci.soli.dto.LayoutParams
@import edu.kit.hci.soli.dto.LoginStateModel
@import edu.kit.hci.soli.config.template.JteContext
@import edu.kit.hci.soli.config.template.PageSpec
@import gg.jte.Content
@import static org.springframework.http.HttpMethod.*

@param PageSpec page
@param LayoutParams layout
@param JteContext context

@param Content content
@param boolean screenHeight = false

<!DOCTYPE html>
<html lang="${context.localize("code")}" data-theme="light">
<head>
    @if(page.description() != null)
        <meta name="description" content="${page.description()}">
    @endif
    <title>${page.title()}</title>
    <meta name="viewport" content="width=device-width"/>
    <meta charset="UTF-8">

    <link rel="icon" href="/favicon.svg">
    <link rel="mask-icon" href="/mask-icon.svg" color="#000000">
    <link rel="apple-touch-icon" href="/favicon_180x180.png">
    <link rel="manifest" href="/manifest.json">

    <link rel="icon" type="image/x-type" sizes="16x16" href="/favicon.ico">

    <!-- Note that the stable 5.0 release breaks our use of the button design and messes with our layout -->
    <!-- Since this is purely for styling, we can safely use this beta version instead -->
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daisyui@5.0.0-beta.1/daisyui.css" />
    <!-- This version of tailwind is incredibly old, but appears to be the latest one that can be used in any (sane) manner. -->
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css"/>
    <link rel="stylesheet" type="text/css" href="/soli.css">

    <noscript>
        <style>
            .needs-js {
                display: none;
            }
        </style>
    </noscript>
</head>

<body>
<div class="flex flex-row flex-wrap ">
    <div class="sticky w-full">
        <div class="drawer lg:drawer-open">
            <input id="my-drawer-2" type="checkbox" class="drawer-toggle"/>
            <div class="drawer-content w-full md:w-full">
                !{var headerColor = layout.getCurrentHighestBooking() == null ? "success" : switch (layout.getCurrentHighestBooking().getShareRoomType()) {
                    case YES, ON_REQUEST -> "warning";
                    case NO -> "error";
                };}
                <div class="navbar bg-${headerColor} text-primary-content">
                    <div class="navbar-start">
                        <div class="drawer-content flex flex-col items-center text-center justify-center">
                            <label for="my-drawer-2" class="btn btn-square btn-ghost drawer-button lg:hidden">
                                <i class="fa-solid fa-bars text-${headerColor}-content"></i>
                            </label>
                        </div>
                    </div>
                    <div class="navbar-center bg-${headerColor} text-${headerColor}-content font-semibold">
                        @if (layout.getRoom() == null)
                            ${context.localize("room.status.none")}
                        @elseif (layout.getCurrentHighestBooking() == null)
                            ${context.localize("room.status.open")}
                        @else
                            ${context.localize("room.status.booked")}
                        @endif
                    </div>
                    <div class="navbar-end">
                        @if (layout.getCurrentHighestBooking() != null)
                            <div class="badge badge-neutral m-3" aria-label="${context.localize("bookings.priority")}" title="${context.localize("bookings.priority.tooltip")}">
                                p-${context.localize("bookings.priority." + layout.getCurrentHighestBooking().getPriority())}</div>
                        @endif

                        !{var currentHighestBooking = layout.getCurrentHighestBooking();}
                        @if (currentHighestBooking != null)
                            !{var bookingShareType = currentHighestBooking.getShareRoomType();}
                            <label class="label" aria-label="${context.localize("bookings.collaboration." + bookingShareType)}"
                                   title="${context.localize("bookings.collaboration." + bookingShareType + ".tooltip")}">
                                @if (bookingShareType == ShareRoomType.YES)
                                    <i class="fa-solid fa-users"></i>
                                @elseif (bookingShareType == ShareRoomType.NO)
                                    <i class="fa-solid fa-users-slash"></i>
                                @else
                                    <i class="fa-solid fa-question"></i>
                                    <i class="fa-solid fa-users"></i>
                                @endif
                            </label>
                        @endif
                    </div>
                </div>
                <div class="p-4 ${screenHeight ? "soli-content-height" : ""}">
                    <div class="card bg-base-100 w-full shadow-xl p-4 ${screenHeight ? "h-full" : ""}">
                        <div class="card-body h-full w-full">
                            ${content}
                        </div>
                    </div>
                </div>
            </div>
            <div class="drawer-side shadow-xl">
                <label for="my-drawer-2" aria-label="close sidebar" class="drawer-overlay"></label>
                <div class="flex flex-col min-h-screen bg-base-200">
                    <ul class="menu bg-base-200 text-base-content w-80 p-4 flex-grow">
                        <div class="flex flex-row gap-3">
                            <li>
                                <div class="basis-1/4">
                                    <a title="${context.localize("index")}" class="avatar avatar-placeholder w-20" href="/">
                                        <img src="/favicon.svg" alt="SOLI">
                                    </a>
                                </div>
                            </li>
                            <div class="basis-1/4">
                                <div class="relative h-20 w-20">
                                    <div class="absolute inset-x-0 bottom-0 h-16">
                                        <label>
                                            ${context.localize("layout.welcome", context.format(layout.getLogin()))}
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="basis-1/2">
                                <div class="relative h-20 w-20">
                                    <div class="absolute inset-x-0 bottom-0 h-16">
                                        @if (layout.getLogin().kind() == LoginStateModel.Kind.VISITOR)
                                            <a href="/login" title="${context.localize("login.login")}" class="btn btn-outline btn-primary">
                                                <i class="fa-solid fa-right-to-bracket"></i>
                                            </a>
                                        @else
                                            @template.util.form(csrf = layout.getLogin().csrfToken(), action = "/logout", method = POST, content = @`
                                                <button class="btn btn-outline btn-primary" title="${context.localize("login.logout")}" type="submit">
                                                    <i class="fa-solid fa-right-from-bracket"></i>
                                                </button>
                                            `)
                                        @endif
                                    </div>
                                </div>
                            </div>
                        </div>

                        @if(layout.getRoom() != null)
                            <div class="divider"></div>
                            @if (layout.isMultipleRooms())
                                <li><a href="/"><i class="fa-solid fa-folder-tree"></i> ${context.localize("layout.current_room", layout.getRoom().getName())}</a></li>
                            @else
                                <li class="pointer-events-none"><label><i class="fa-solid fa-circle-info"></i> ${context.localize("layout.current_room", layout.getRoom().getName())}</label></li>
                            @endif
                        @endif

                        @if(layout.getLogin().kind() == LoginStateModel.Kind.ADMIN)
                            <div class="divider"></div>

                            <li><a href="/admin/users"><i class="fa-solid fa-list"></i> ${context.localize("administration.user")}</a></li>
                            <li><a href="/admin/statistics"><i class="fa-solid fa-chart-line"></i> ${context.localize("administration.statistics")}</a></li>
                            <li><a href="/admin/rooms"><i class="fa-solid fa-house"></i> ${context.localize("rooms")}</a></li>
                            @if(layout.getRoom() != null)
                                <li><a href="/admin/opening-hours/${layout.getRoom().getId()}"><i class="fa-regular fa-clock"></i> ${context.localize("administration.opening_hours")}</a></li>
                            @endif
                        @endif

                        <div class="divider"></div>

                        @if (layout.getRoom() != null)
                            <li><a href="/${layout.getRoom().getId()}"><i class="fa-solid fa-calendar-days"></i> ${context.localize("calendar")}</a></li>
                            <li><a href="/${layout.getRoom().getId()}/bookings"><i class="fa-solid fa-list"></i> ${context.localize("bookings.my_bookings")}</a>
                            </li>

                            <div class="divider"></div>

                            <li><a class="btn btn-outline btn-primary" href="/${layout.getRoom().getId()}/bookings/new"><i class="fa-solid fa-plus"></i> ${context.localize("bookings.new")}</a></li>
                            @if(layout.getCurrentBookingOfUser() != null)
                                @template.util.form(csrf = layout.getLogin().csrfToken(), action = "/" + layout.getRoom().getId() + "/bookings/" + layout.getCurrentBookingOfUser().getId(), method = DELETE, content = @`
                                    <li>
                                        <button type="submit" class="btn btn-outline btn-error mt-2">
                                            <i class="fas fa-trash-alt"></i>${context.localize("bookings.check_out")}
                                        </button>
                                    </li>
                                `)
                            @endif
                        @endif
                    </ul>

                    <footer class="footer sm:footer-horizontal bg-base-300 text-base-content p-10 w-full">
                        <nav>
                            <h6 class="footer-title">${context.localize("layout.legal")}</h6>
                            <a href="https://www.gnu.org/licenses/gpl-3.0.html" class="link link-hover">${context.localize("layout.license")}</a>
                            <a href="https://www.kit.edu/privacypolicy.php" class="link link-hover">${context.localize("layout.privacy")}</a>
                            <a href="https://www.comp.kit.edu/downloads/Code%20of%20Conduct_KIT_for%20external%20use.pdf" class="link link-hover">${context.localize("layout.conduct")}</a>
                        </nav>
                        <nav>
                            <h6 class="footer-title">${context.localize("layout.about")}</h6>
                            <a href="https://github.com/Solidarische-Raumnutzung/SOLI" class="link link-hover">${context.localize("layout.source")}</a>
                            <a href="https://hci.iar.kit.edu/" class="link link-hover">HCI</a>
                            <a href="/publisher" class="link link-hover">${context.localize("layout.publisher")}</a>
                        </nav>
                    </footer>
                </div>
            </div>
        </div>
    </div>
</div>

<link rel="stylesheet" type="text/css"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.1/css/all.min.css"/>
</body>
</html>
