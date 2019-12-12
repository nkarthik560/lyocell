package lyocell.document.managment.hook;

import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.UUID;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


public class LyocellDocumentManagmentDispalyContext extends BaseDLViewFileVersionDisplayContext  {

	public LyocellDocumentManagmentDispalyContext( DLViewFileVersionDisplayContext parentDLDisplayContext,
			HttpServletRequest request, HttpServletResponse response, FileVersion fileVersion) {
	 super(SIMPLE_EDIT_UUID, parentDLDisplayContext, request, response, fileVersion);
	 ThemeDisplay themeDisplay=(ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
	 PortletURL portletUrl =  PortletURLFactoryUtil.create(request, themeDisplay.getPortletDisplay().getId(), themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
	 portletUrl.setParameter("mvcRenderCommandName", "/document_library/view_file_entry");
	 portletUrl.setParameter("redirect", HttpUtil.removeParameter(PortalUtil.getCurrentURL(request),  "_"+themeDisplay.getPpid()+ "ajax"));
	 portletUrl.setParameter("fileEntryId", String.valueOf(fileVersion.getFileEntryId()));
	 this._infoURL=portletUrl.toString();
	 PortletURL copyUrl =  PortletURLFactoryUtil.create(request, themeDisplay.getPortletDisplay().getId(), themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
	 copyUrl.setParameter("mvcRenderCommandName", "/document_library/move_entry ");
	 copyUrl.setParameter("fromCopyURL", "true");
	 copyUrl.setParameter("redirect", HttpUtil.removeParameter(PortalUtil.getCurrentURL(request),  "_"+themeDisplay.getPpid()+ "ajax"));
	 copyUrl.setParameter("fileEntryId", String.valueOf(fileVersion.getFileEntryId()));
	 this._copyURL=copyUrl.toString();
	// System.out.println("constructor of LyocellDocumentManagmentDispalyContext"+this._copyURL);
	}
	public Menu getMenu() throws PortalException {
		System.out.println("getMenu of dispalyy context");
		Menu menu = super.getMenu();
		URLMenuItem urlMenuItem = new URLMenuItem();
		urlMenuItem.setMethod(HttpMethods.GET);
		urlMenuItem.setKey("com.liferay.document.library.display.context.DLUIItemKeys#Info");
		urlMenuItem.setLabel("Info");
		urlMenuItem.setURL(this._infoURL);

		
		menu.getMenuItems().add(urlMenuItem);
		/*
		 * for(MenuItem menuItem: menu.getMenuItems()) {
		 * System.out.println("key"+menuItem.getKey()); System.out.println("label:"
		 * +menuItem.getLabel()); if(Validator.isNotNull(menuItem)&&
		 * Validator.isNotNull(menuItem.getLabel())&&menuItem.getLabel().
		 * equalsIgnoreCase("move")) { URLMenuItem urlmenu=(URLMenuItem)menuItem;
		 * System.out.println("move URL::"+urlmenu.getURL());
		 * this._copyURL=urlmenu.getURL(); } }
		 */
		
		URLMenuItem copyMenuItem = new URLMenuItem();
		copyMenuItem.setMethod(HttpMethods.GET);
		copyMenuItem.setKey("com.liferay.document.library.display.context.DLUIItemKeys#copy");
		copyMenuItem.setLabel("copy");
		copyMenuItem.setURL(this._copyURL);
		menu.getMenuItems().add(copyMenuItem);
		
		return menu;
	}

	private static final Log _log = LogFactoryUtil.getLog(
			LyocellDocumentManagmentDispalyContext.class);
	private  String _infoURL;
	private String _copyURL;
	private static final UUID SIMPLE_EDIT_UUID = UUID.fromString("7B61EA79-83AE-4FFD-A77A-1D47E06EBBE10");
	
}