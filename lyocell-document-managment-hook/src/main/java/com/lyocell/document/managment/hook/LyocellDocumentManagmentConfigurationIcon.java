/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lyocell.document.managment.hook;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=com_liferay_document_library_web_portlet_DLAdminPortlet",
		"path=/document_library/view_file_entry",
		
		
	},
	service = PortletConfigurationIcon.class
)
public class LyocellDocumentManagmentConfigurationIcon extends BasePortletConfigurationIcon {

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public String getMessage(PortletRequest portletRequest) {
		return "Info";
	}
	
	public String getURL(PortletRequest portletRequest, PortletResponse portletResponse) {
		HttpServletRequest servletRequest = portal.getHttpServletRequest(portletRequest);
		FileEntry fileEntry = retrieveFile(servletRequest);
	LiferayPortletResponse liferayPortletResponse=	portal.getLiferayPortletResponse(portletResponse);
	ThemeDisplay themeDisplay=(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	PortletURL portletUrl =  PortletURLFactoryUtil.create(portletRequest, themeDisplay.getPortletDisplay().getId(), themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
	portletUrl.setParameter("mvcRenderCommandName", "/document_library/view_file_entry");
	portletUrl.setParameter("redirect", HttpUtil.removeParameter(portal.getCurrentURL(portletRequest), liferayPortletResponse.getNamespace() + "ajax"));
	portletUrl.setParameter("fileEntryId", String.valueOf(fileEntry.getFileEntryId()));
	return portletUrl.toString();
	
	}

	private FileEntry retrieveFile(HttpServletRequest request) {
		try {
			long fileEntryId = ParamUtil.getLong(request, "fileEntryId");

			FileEntry fileEntry = null;

			if (fileEntryId > 0) {
				fileEntry = dlAppService.getFileEntry(fileEntryId);
			}

			if (fileEntry == null) {
				return null;
			}

			String cmd = ParamUtil.getString(request, Constants.CMD);

			if (fileEntry.isInTrash() && !cmd.equals(Constants.MOVE_FROM_TRASH)) {
				LOGGER.info("File entry is not supposed to be opened.");
				return null;
			}

			return fileEntry;
		} catch (PortalException e) {
			LOGGER.error("An exception ocurred while retrieving Url.", e);

			return null;
		}
	}
	@Reference private Portal portal;
	@Reference DLAppService dlAppService;
	private static final Log LOGGER = LogFactoryUtil.getLog(LyocellDocumentManagmentConfigurationIcon.class);

}