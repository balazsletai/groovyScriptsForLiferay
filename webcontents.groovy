// This script can create a given number of web contents in Liferay 7.3
// Set how many web content you would like with WEBCONTENTS_TO_CREATE
// Set the structure id with WEBCONTENTS_STR_ID
// Set the ddm template key with WEBCONTENTS_TEMPLATE_KEY

import com.liferay.portal.kernel.service.UserLocalServiceUtil
import com.liferay.portal.kernel.service.GroupLocalServiceUtil
import com.liferay.journal.model.JournalArticle
import com.liferay.journal.service.JournalArticleLocalServiceUtil
import com.liferay.portal.kernel.util.PortalUtil
import com.liferay.portal.kernel.service.ServiceContext

// set how many web contents you would like to make
WEBCONTENTS_TO_CREATE = 10
// set the structure id (for basic web content it is BASIC-WEB-CONTENT)
WEBCONTENTS_STR_KEY = 37951
// set the ddm template key (for basic web content it is BASIC-WEB-CONTENT)
WEBCONTENTS_TEMPLATE_KEY = 37960

def serviceContext = new ServiceContext()
def defaultCompanyId = PortalUtil.getDefaultCompanyId()
println("Company Id: " + defaultCompanyId)
def defaultUser = UserLocalServiceUtil.getDefaultUser(defaultCompanyId)
println("Default User Id: " + defaultUser.getUserId())
def companyGroup = GroupLocalServiceUtil.getCompanyGroup(defaultCompanyId)
serviceContext.setScopeGroupId(companyGroup.getGroupId())
def groupId = 0
for (group in GroupLocalServiceUtil.getCompanyGroups(defaultCompanyId, 0, GroupLocalServiceUtil.getCompanyGroupsCount(defaultCompanyId))) {
    if (group.getTypeLabel() == "open") {
        groupId = group.getGroupId()
        println("Group: " + group.getDescriptiveName() + ", Id: " + group.getGroupId())
        break;
    }
}
println("Used Group Id: " + groupId)

// Create new web content
def titleMap = new HashMap<Locale,String>()
titleMap.put(new Locale("en", "US"), "Test Web Content")
def descriptionMap = new HashMap<Locale,String>()

descriptionMap.put(new Locale("en", "US"), "Test Web Content Description")
(1..WEBCONTENTS_TO_CREATE).each {
    def journalArticle = JournalArticleLocalServiceUtil.addArticle(
            defaultUser.getUserId(),
            groupId,
            0,
            titleMap,
            descriptionMap,
            """<?xml version="1.0"?>
    <root available-locales="en_US" default-locale="en_US">
    <dynamic-element name="content" type="text_area" index-type="text">
    <dynamic-content language-id="en_US"><![CDATA[]]></dynamic-content>
    </dynamic-element>
    </root>""",
            WEBCONTENTS_STR_KEY.toString(),
            WEBCONTENTS_TEMPLATE_KEY.toString(),
            serviceContext
    )
}
println("Created " + WEBCONTENTS_TO_CREATE + " Web Content articles");
