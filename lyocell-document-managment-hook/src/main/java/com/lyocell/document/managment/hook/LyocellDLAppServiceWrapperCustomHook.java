package com.lyocell.document.managment.hook;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLSyncConstants;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLAppServiceWrapper;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.InvalidRepositoryException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.InvalidRepositoryIdException;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.RepositoryService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Karthik N
 */
@Component(
	immediate = true,
	property = {
	},
	service = ServiceWrapper.class
)
public class LyocellDLAppServiceWrapperCustomHook extends DLAppServiceWrapper {

	public LyocellDLAppServiceWrapperCustomHook( ) {
		super(null);
	}
	
	@Override
	public com.liferay.portal.kernel.repository.model.Folder moveFolder(
			long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		HttpServletRequest request=serviceContext.getRequest();
		String fromCopyURL=ParamUtil.getString(request, "fromCopyURL");
		if(!fromCopyURL.equalsIgnoreCase("true")) {
			return super.moveFolder(
					folderId, parentFolderId, serviceContext);
		}
		Repository toRepository=getFolderRepository(folderId, serviceContext.getScopeGroupId());
		Folder folder=null;
		serviceContext.setAddGuestPermissions(true);
		folder=super.copyFolder(toRepository.getRepositoryId(), folderId, parentFolderId, DLAppLocalServiceUtil.getFolder(folderId).getName(), DLAppLocalServiceUtil.getFolder(folderId).getDescription(), serviceContext);
		return folder;
	}
	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry moveFileEntry(
			long fileEntryId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		HttpServletRequest request=serviceContext.getRequest();
		String fromCopyURL=ParamUtil.getString(request, "fromCopyURL");
		if(_log.isInfoEnabled()) {
			_log.info("fromCopyURL: "+fromCopyURL);	
		}
		//default move file
		if(! fromCopyURL.equalsIgnoreCase("true")) {
			return super.moveFileEntry(
					fileEntryId, newFolderId, serviceContext);
		}
		Repository fromRepository = getFileEntryRepository(fileEntryId);
		Repository toRepository=getFolderRepository(newFolderId, serviceContext.getScopeGroupId());
		
		if (newFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder toFolder =toRepository.getFolder(newFolderId);
			if (toFolder.isMountPoint()) {
			toRepository = getRepository(toFolder.getRepositoryId());
			}
			}
		if(fromRepository.getRepositoryId()== toRepository.getRepositoryId()) {
			return copyFileWithinRepo(fileEntryId, newFolderId, serviceContext);
		}
		return null;
	}
	
	
	private FileEntry  copyFileWithinRepo(long fileEntryId, long newFolderId, ServiceContext serviceContext) throws PortalException {
		Repository fromRepository = getFileEntryRepository(fileEntryId);
		Repository toRepository = getFolderRepository(
		newFolderId, serviceContext.getScopeGroupId());
		FileEntry fileEntry=fromRepository.copyFileEntry(serviceContext.getUserId(),serviceContext.getScopeGroupId(), fileEntryId, newFolderId, serviceContext);
		FileEntry srcFileEntry=DLAppLocalServiceUtil.getFileEntry(fileEntryId);
		DLAppHelperLocalServiceUtil.updateFileEntry(serviceContext.getUserId(), fileEntry, srcFileEntry.getFileVersion(), fileEntry.getFileVersion(), serviceContext);
		
		Map<String, Serializable> workflowContext = new HashMap<String, Serializable>();
		workflowContext.put("event", DLSyncConstants.EVENT_ADD);
		
		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(fileEntry.getFileEntryId());
		if(_log.isInfoEnabled()) {
		_log.info("calling toRepository.updateFileEntry()");
		}
		toRepository.updateFileEntry(serviceContext.getUserId(),fileEntry.getFileEntryId(), dlFileEntry.getName(), fileEntry.getMimeType(), fileEntry.getTitle(), fileEntry.getDescription(), null, false, null, fileEntry.getSize(), serviceContext);
		if(_log.isInfoEnabled()) {
			_log.info("updated the file entry. returning...");
			}
		return fileEntry;
		
	}
	//Repo from FileEntry Id
	protected Repository getFileEntryRepository(long fileEntryId) throws PortalException{
		try {
			//return repositoryService.getRepository(dlAppService.getFileEntry(fileEntryId).getRepositoryId());
		return RepositoryProviderUtil.getFileEntryRepository(fileEntryId);
		} catch (InvalidRepositoryException e) {
			throw new NoSuchFileEntryException("No Such FileEntry: "+fileEntryId, e);
		}
	}
	//Repo from folderId or groupId
	protected Repository getFolderRepository(long folderId, long groupId) throws PortalException {
		Repository repository=null;
		if(folderId== DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			repository=getRepository(groupId);
		}else {
			repository= getFolderRepository(folderId);
		}
		return repository;
	}
	
	//Repo from groupId
	protected Repository getRepository(long repositoryId) throws PortalException {
		try {
			return RepositoryProviderUtil.getRepository(repositoryId);
		} catch (InvalidRepositoryIdException e) {
			throw new NoSuchGroupException("No Such Group Existing with key: "+repositoryId);
		}
	}
	// repo from Folder Id
	protected Repository getFolderRepository(long folderId) throws PortalException {
		try {
			//return repositoryService.getRepository(dlAppService.getFolder(folderId).getRepositoryId());
			return RepositoryProviderUtil.getFolderRepository(folderId);
		} catch (InvalidRepositoryIdException e) {
			throw new NoSuchFolderException("No Folder Exist with the key:" +folderId);
		}
	}

@Reference
private DLAppService dlAppService;
@Reference
private RepositoryService repositoryService;
private Log _log=LogFactoryUtil.getLog(LyocellDLAppServiceWrapperCustomHook.class);
}