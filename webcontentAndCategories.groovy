// This script can create a given number of web contents with a category in Liferay 7.4
// Set how many web content you would like with WEBCONTENTS_TO_CREATE
// Set the structure id with WEBCONTENTS_STR_ID
// Set the ddm template key with WEBCONTENTS_TEMPLATE_KEY

import com.liferay.portal.kernel.service.UserLocalServiceUtil
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil
import com.liferay.asset.kernel.model.AssetCategory
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil
import com.liferay.portal.kernel.service.GroupLocalServiceUtil
import com.liferay.journal.model.JournalArticle
import com.liferay.journal.service.JournalArticleLocalServiceUtil
import com.liferay.portal.kernel.util.PortalUtil
import com.liferay.portal.kernel.service.ServiceContext

WEBCONTENTS_TO_CREATE = 10 // set how many web contents you would like to make
WEBCONTENTS_STR_ID = 44101 // set the structure id
WEBCONTENTS_TEMPLATE_KEY = "BASIC-WEB-CONTENT" // set the ddm template key

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
AssetCategory assetCategory
def assetVocabulary = AssetVocabularyLocalServiceUtil.addVocabulary(
        defaultUser.getUserId(),
        groupId,
        "Small Test Vocabulary",
        serviceContext
)
println("Created Vocabulary with Id: " + assetVocabulary.getVocabularyId());
// Create new category
assetCategory = AssetCategoryLocalServiceUtil.addCategory(
        defaultUser.getUserId(),
        groupId,
        "Small Test Category",
        assetVocabulary.getVocabularyId(),
        serviceContext
)
println("Created Category with Id: " + assetCategory.getCategoryId())
// Create new web content for category
def titleMap = new HashMap<Locale,String>()
titleMap.put(new Locale("en", "US"), "Test Web Content")
def descriptionMap = new HashMap<Locale,String>()
descriptionMap.put(new Locale("en", "US"), "Test Web Content Description")
(1..WEBCONTENTS_TO_CREATE).each {
    def journalArticle = JournalArticleLocalServiceUtil.addArticle(
            "",
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
            WEBCONTENTS_STR_ID,
            WEBCONTENTS_TEMPLATE_KEY,
            serviceContext
    )
    JournalArticleLocalServiceUtil.updateAsset(defaultUser.getUserId(), journalArticle as JournalArticle, [assetCategory.getCategoryId()] as long[], null as String[], null as long[], 0.0 as double)
}
println("Created " + WEBCONTENTS_TO_CREATE + " Web Content articles");
