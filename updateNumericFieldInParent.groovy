import com.liferay.portal.kernel.log.LogFactoryUtil
import com.liferay.portal.kernel.log.Log
import com.liferay.portal.kernel.service.ServiceContext
import com.liferay.object.model.ObjectEntry
import com.liferay.object.service.ObjectEntryLocalServiceUtil
import java.io.Serializable

Log log = LogFactoryUtil.getLog(this.class)

try {
    log.info("Starting Parent numeric field update on Child creation...")

    // Extract values from entryDTO["properties"]
    Map<String, Object> properties = (Map<String, Object>) entryDTO.get("properties")
    if (properties == null) {
        log.error("No properties found in entryDTO!")
        return
    }

    // Convert Parent ID to long
    long parentEntryId = Long.parseLong((String) properties.get("r_relation_c_parentId"))

    log.info("Extracted Parent ID: " + parentEntryId)

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

    log.info("Parent entry found. Fetching current numeric field value...")

    // Define the numeric field key (replace with the actual field name)
    String parentNumericFieldKey = "numberofchildren"  // Replace with the actual numeric field name

    // Get the current value and ensure it's a number
    Object currentValue = parentEntry.getValues().get(parentNumericFieldKey)
    int newValue = 0

    if (currentValue instanceof Number) {
        int currentIntValue = ((Number) currentValue).intValue()

        // Check for overflow before incrementing
        if (currentIntValue < Integer.MAX_VALUE) {
            newValue = currentIntValue + 1
        } else {
            log.warn("Integer field overflow: current value is already at max.")
            newValue = Integer.MAX_VALUE // Set to max value to avoid overflow
        }
    } else {
        newValue = 1 // Start at 1 if the field is null or non-numeric
    }

    log.info("Current Value: " + currentValue + ", New Value: " + newValue)

    // Prepare updated values
    Map<String, Serializable> values = new HashMap<>()
    values.put(parentNumericFieldKey, newValue)

    // Get user ID from `currentUserId`
    long userId = (long) currentUserId

    // Update the Parent object
    ServiceContext serviceContext = new ServiceContext()
    ObjectEntryLocalServiceUtil.updateObjectEntry(userId, parentEntryId, values, serviceContext)

    log.info("Parent numeric field '" + parentNumericFieldKey + "' updated successfully!")

} catch (Exception e) {
    log.error("Script failed: " + e.getMessage(), e)
}