@import edu.kit.hci.soli.config.template.JteContext
@import org.springframework.data.domain.Page

@param JteContext context
@param Page<?> content
@param String baseUrl

<div class="join grid grid-cols-3 mt-4 ">
    @if(content.hasPrevious())
        <a class="join-item btn btn-outline"
           href="${baseUrl}?page=${content.getNumber() - 1}&size=${content.getSize()}">${context.localize("pagination.previous")}</a>
    @else <span class="join-item btn btn-outline btn-disabled">${context.localize("pagination.previous")}</span>
    @endif
    <span class="join-item btn btn-outline">${context.localize("pagination.page", content.getNumber() + 1, content.getTotalPages())}</span>
    @if(content.hasNext())
        <a class="join-item btn btn-outline"
           href="${baseUrl}?page=${content.getNumber() + 1}&size=${content.getSize()}">${context.localize("pagination.next")}</a>
    @else <span class="join-item btn btn-outline btn-disabled">${context.localize("pagination.next")}</span>
    @endif
</div>