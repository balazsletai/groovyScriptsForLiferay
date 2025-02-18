import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil
import com.liferay.portal.kernel.repository.model.FileEntry
import com.liferay.portal.kernel.service.ServiceContext
import com.liferay.portal.kernel.service.UserLocalServiceUtil
import com.liferay.portal.kernel.util.ContentTypes
import com.liferay.portal.kernel.util.PortalUtil
import java.io.File

// Get companyId
long companyId = PortalUtil.getDefaultCompanyId()

// Get userId (default admin)
long userId = UserLocalServiceUtil.getDefaultUserId(companyId)

// Get groupId (default Liferay site)
long groupId = companyId
long repositoryId = PLACE YOUR REPOSITORY ID HERE
long folderId = 0  // Root folder

// Updated file path
String filePath = "PATH TO YOUR FILE/test.xlsx"
File file = new File(filePath)

if (!file.exists()) {
    out.println("File not found: " + filePath)
    return
}

String fileName = file.getName()
String mimeType = ContentTypes.APPLICATION_VND_MS_EXCEL
String title = "Test Excel Upload"
String description = "Test upload via DLAppLocalServiceUtil"

// Create ServiceContext
ServiceContext serviceContext = new ServiceContext()

try {
    // Upload file
    FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
        userId, repositoryId, folderId, fileName, mimeType,
        title, description, "", file, serviceContext
    )

    out.println("File uploaded successfully! File Entry ID: " + fileEntry.getFileEntryId())
} catch (Exception e) {
    out.println("Error uploading file: " + e.getMessage())
    e.printStackTrace(out)
}