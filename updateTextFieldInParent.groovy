import com.liferay.portal.kernel.log.LogFactoryUtil
import com.liferay.portal.kernel.log.Log
import com.liferay.portal.kernel.service.ServiceContext
import com.liferay.object.model.ObjectEntry
import com.liferay.object.service.ObjectEntryLocalServiceUtil
import java.io.Serializable

Log log = LogFactoryUtil.getLog(this.class)

try {
    log.info("Starting Parent update on Child creation...")

    // Extract values from entryDTO["properties"]
    Map<String, Object> properties = (Map<String, Object>) entryDTO.get("properties")
    if (properties == null) {
        log.error("No properties found in entryDTO!")
        return
    }

    // Convert Parent ID to long
    long parentEntryId = Long.parseLong((String) properties.get("r_relation_c_parentId"))
    String childName = (String) properties.get("name")

    log.info("Extracted Parent ID: " + parentEntryId)
    log.info("Extracted Child Name: " + childName)

    if (parentEntryId <= 0) {
        log.error("Parent ID is invalid or missing!")
        return
    }

    // Fetch the Parent entry
    ObjectEntry parentEntry = ObjectEntryLocalServiceUtil.getObjectEntry(parentEntryId)

    if (parentEntry == null) {
        log.error("Parent entry not found for ID: " + parentEntryId)
        return
    }

    log.info("Listing all Parent object fields...")
    parentEntry.getValues().each { key, value ->
        log.info("Field: " + key + " -> Value: " + value)
    }

    log.info("Parent entry found. Updating 'sometext' field...")

    // Use the correct field key
    String parentNameFieldKey = "sometext"

    // Prepare updated values
    Map<String, Serializable> values = new HashMap<>()
    values.put(parentNameFieldKey, "Updated by Child: " + childName)

    // Get user ID from `currentUserId`
    long userId = (long) currentUserId

    // Update the Parent object
    ServiceContext serviceContext = new ServiceContext()
    ObjectEntryLocalServiceUtil.updateObjectEntry(userId, parentEntryId, values, serviceContext)

    log.info("Parent 'sometext' field update submitted successfully!")

} catch (Exception e) {
    log.error("Script failed: " + e.getMessage(), e)
}