import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil
import com.liferay.document.library.kernel.model.DLFileEntry
import com.liferay.document.library.kernel.model.DLFileVersion
import com.liferay.document.library.kernel.model.DLFileEntryType
import com.liferay.portal.kernel.dao.orm.QueryUtil
import com.liferay.portal.kernel.util.LocaleUtil

import java.util.Date

int start = QueryUtil.ALL_POS
int end = QueryUtil.ALL_POS

List<DLFileEntry> dlFileEntryList = DLFileEntryLocalServiceUtil.getFileEntries(start, end)

out.println("=== Document Library File Entries ===\n")

// Get the current date
Date currentDate = new Date()

for (DLFileEntry dlFileEntry : dlFileEntryList) {
    DLFileVersion dlFileVersion = dlFileEntry.getFileVersion()
    DLFileEntryType dlFileEntryType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(dlFileVersion.getFileEntryTypeId())

    Date creationDate = dlFileEntry.getCreateDate()
    long ageInMillis = currentDate.getTime() - creationDate.getTime()
    long ageInDays = ageInMillis / (1000 * 60 * 60 * 24) // Convert milliseconds to days

    out.println("File Entry ID: " + dlFileEntry.getFileEntryId())
    out.println("Title: " + dlFileEntry.getTitle())
    out.println("File Name: " + dlFileEntry.getFileName())
    out.println("File Size: " + dlFileEntry.getSize() + " bytes")
    out.println("MIME Type: " + dlFileEntry.getMimeType())
    out.println("Description: " + dlFileEntry.getDescription())
    out.println("User Name: " + dlFileEntry.getUserName())
    out.println("Created On: " + creationDate)
    out.println("Age of Document: " + ageInDays + " days")
    out.println("Modified On: " + dlFileEntry.getModifiedDate())
    out.println("File Entry Type: ID=" + dlFileEntryType.getFileEntryTypeId() + 
                ", Key=" + dlFileEntryType.getFileEntryTypeKey() + 
                ", Name=" + dlFileEntryType.getName(LocaleUtil.getSiteDefault()))
    out.println("=====================================\n")

    out.println("DLFileVersion Information:")
    out.println(" - File Version ID: " + dlFileVersion.getFileVersionId())
    out.println(" - Version: " + dlFileVersion.getVersion())
    out.println(" - Checksum: " + dlFileVersion.getChecksum())
    out.println(" - Status: " + dlFileVersion.getStatus())
    out.println(" - Status By: " + dlFileVersion.getStatusByUserName() + " on " + dlFileVersion.getStatusDate())
    out.println(" - Change Log: " + dlFileVersion.getChangeLog())
    out.println(" - Created By: " + dlFileVersion.getUserName())
    out.println(" - Created On: " + dlFileVersion.getCreateDate())
    out.println(" - Modified On: " + dlFileVersion.getModifiedDate())
    out.println("=====================================\n")
}

