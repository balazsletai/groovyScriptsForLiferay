import com.liferay.portal.kernel.model.Group
import com.liferay.portal.kernel.model.Layout
import com.liferay.portal.kernel.model.LayoutConstants
import com.liferay.portal.kernel.service.GroupLocalServiceUtil
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil
import com.liferay.portal.kernel.service.ServiceContextFactory
import com.liferay.petra.string.StringPool

// Set how many pages you would like to create
// Create a site in Liferay and set the site name in this script, so it will add the pages to that site
// Set the base name so pages will get created with that name plus the number e.g. contentPage1, contentPage2
// Note: these will be created in draft mode because:
// Draft publishing for content pages is more complex and requires calling the OSGi service method publishDraft, 
// which is NOT accessible statically via Groovy scripting console due to classloader/module restrictions. 
// You cannot use PortalBeanLocatorUtil or ServiceTracker from the Groovy console.

int numPages = 10
String siteName = "test"
String pageNameBase = "contentPage"

try {
    def serviceContext = ServiceContextFactory.getInstance(actionRequest)
    long userId = serviceContext.getUserId()
    long companyId = serviceContext.getCompanyId()

    Group group = GroupLocalServiceUtil.fetchGroup(companyId, siteName)
    if (group == null) {
        println("Site with name '" + siteName + "' not found.")
        return
    }
    long groupId = group.getGroupId()

    for (int i = 1; i <= numPages; i++) {
        String pageName = pageNameBase + i

        Layout draftLayout = LayoutLocalServiceUtil.addLayout(
            null, userId, groupId, false, 0, pageName, StringPool.BLANK,
            StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false,
            StringPool.BLANK, serviceContext
        )

        draftLayout.setStatus(0) // 0 = Approved (published)
        LayoutLocalServiceUtil.updateLayout(draftLayout)

        println("Page " + i + " created: " + pageName)
    }

    println(numPages + " content pages were added to the " + siteName + " site.")
} catch (Exception e) {
    println("Script failed: " + e.getMessage())
}