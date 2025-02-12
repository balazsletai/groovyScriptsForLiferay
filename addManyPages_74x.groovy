import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.util.PropsValues;

// Create a site named as you set for siteName below
// Set the numPages to the desired number of pages below
// Run the script from Control Panel > Server administration > Script tab

int numPages = 10;
String siteName = "test";

try {
    ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);

    long userId = serviceContext.getUserId();
    long companyId = serviceContext.getCompanyId();

    Group group = GroupLocalServiceUtil.fetchGroup(companyId, siteName);
    if (group == null) {
        println("Site with name '" + siteName + "' not found.");
        return;
    }

    long groupId = group.getGroupId();
    String nameBase = "page";
    String title = StringPool.BLANK;
    String description = StringPool.BLANK;
    String friendlyURL = StringPool.BLANK;

    Layout layout = null;
    String layoutTemplateId = PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID;

    for (int i = 1; i <= numPages; i++) {
        layout = LayoutLocalServiceUtil.addLayout(
            null, userId, groupId, false, 0, nameBase + i, title,
            description, LayoutConstants.TYPE_PORTLET, false, friendlyURL,
            serviceContext);

        LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
        layoutTypePortlet.setLayoutTemplateId(userId, layoutTemplateId);

        LayoutLocalServiceUtil.updateLayout(
            groupId, false, layout.getLayoutId(),
            layout.getTypeSettings());
    }

    println(numPages + " pages were added to the " + siteName + " site.");
} catch (Exception e) {
    println("Script failed: " + e.getMessage());
}
