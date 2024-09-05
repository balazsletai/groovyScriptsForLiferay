import com.liferay.asset.kernel.model.AssetCategory
import com.liferay.asset.kernel.model.AssetVocabulary
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil
import com.liferay.portal.kernel.log.Log
import com.liferay.portal.kernel.log.LogFactoryUtil
import com.liferay.portal.kernel.service.ServiceContext
import com.liferay.portal.kernel.service.ServiceContextThreadLocal

class CreateAssetCategories {

    private static Log _log = LogFactoryUtil.getLog(CreateAssetCategories.class)

    void createCategories(long vocabularyId, int numberOfCategories) {
        try {
            ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext()

            AssetVocabulary assetVocabulary = AssetVocabularyLocalServiceUtil.getAssetVocabulary(vocabularyId)

            for (int i = 1; i <= numberOfCategories; i++) {
                String categoryName = "Category " + i

                AssetCategory assetCategory = AssetCategoryLocalServiceUtil.addCategory(
                        serviceContext.getUserId(),
                        serviceContext.getScopeGroupId(),
                        categoryName,
                        vocabularyId,
                        serviceContext
                )

                if (_log.isInfoEnabled()) {
                    _log.info("Created category: " + assetCategory.getName())
                }
            }

            _log.info("Successfully created " + numberOfCategories + " categories in vocabulary " + assetVocabulary.getName())
        } catch (Exception e) {
            _log.error("Error while creating asset categories", e)
        }
    }
}

long vocabularyId = 33286L  // Set your vocabulary ID here
int numberOfCategories = 20000 // Set the number of categories

out.println("Running groovy script to create " + numberOfCategories + " categories in vocabulary with ID: " + vocabularyId)

CreateAssetCategories createAssetCategories = new CreateAssetCategories()

createAssetCategories.createCategories(vocabularyId, numberOfCategories)

out.println("Script finished running. Please check the Liferay logs for details")