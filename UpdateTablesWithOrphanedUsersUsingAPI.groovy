import com.liferay.petra.executor.PortalExecutorManager
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal
import com.liferay.portal.kernel.service.PersistedModelLocalService
import com.liferay.portal.kernel.model.UserTable
import com.liferay.portal.kernel.module.util.SystemBundleUtil
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil
import com.liferay.portal.kernel.service.UserLocalServiceUtil
import com.liferay.portal.kernel.util.PortalClassLoaderUtil
import com.liferay.portal.kernel.util.StringUtil

def portalClassLoader = PortalClassLoaderUtil.getClassLoader()
def bundleContext = SystemBundleUtil.getBundleContext()

def portalExecutorManager = bundleContext.getService(bundleContext.getServiceReferences(PortalExecutorManager.class, null)[0])

def userIdQuery = DSLQueryFactoryUtil.select(UserTable.INSTANCE.userId).from(UserTable.INSTANCE)

def fixOrphanedUsers = { serviceAccountUserId, sr ->
  String modelName = sr.getProperty("model.class.name")

  def clazz = null

  try {
    clazz = sr.getBundle().loadClass(modelName + "Table")
  }
  catch (e) {
    return
  }

  def instance = null

  try {
    instance = clazz.getDeclaredField("INSTANCE").get(clazz)
    instance.userId
  }
  catch (e) {
    return
  }

  def service = bundleContext.getService(sr)

  def dslQuery = DSLQueryFactoryUtil.selectDistinct(instance.userId).from(instance).where(instance.userId.notIn(userIdQuery))
  def orphanedUserIds = service.dslQuery(dslQuery)

  orphanedUserIds.each({ userId ->
    if (userId == 0) {
      return
    }

    def actionableDynamicQuery = service.getActionableDynamicQuery()
    actionableDynamicQuery.setAddCriteriaMethod({ dynamicQuery ->
      dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", userId))
    })
    actionableDynamicQuery.setPerformActionMethod({ result ->
      result.setUserId(serviceAccountUserId)
      result.persist()
    })

    System.out.println("${modelName} had ${actionableDynamicQuery.performCount()} entries with orphaned userId=${userId}")

    actionableDynamicQuery.performActions()
  })
}

def updateTables = {
  def indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly()

  try {
    IndexStatusManagerThreadLocal.setIndexReadOnly(true)

    def persistedModels = bundleContext.getServiceReferences(PersistedModelLocalService.class, null)

    CompanyLocalServiceUtil.forEachCompanyId({ companyId ->
      def serviceAccountUserId = UserLocalServiceUtil.addDefaultServiceAccountUser(companyId).getUserId()

      persistedModels.each({
        try {
          fixOrphanedUsers(serviceAccountUserId, it)
        }
        catch (e) {
          e.printStackTrace()
        }
      })
    })
  }
  finally {
    IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly)
  }
}

def executorService = portalExecutorManager.getPortalExecutor("UpdateTablesWithOrphanedUsersUsingAPI.groovy", false)

if (executorService == null) {
  executorService = portalExecutorManager.getPortalExecutor("UpdateTablesWithOrphanedUsersUsingAPI.groovy", true)
  executorService.execute(updateTables)
  println("Updating tables with orphaned users in the background")
}
else if (executorService.getActiveCount() == 0) {
  println("Finished updating tables with orphaned users in the background")
}
else {
  println("Still updating tables with orphaned users in the background")
}