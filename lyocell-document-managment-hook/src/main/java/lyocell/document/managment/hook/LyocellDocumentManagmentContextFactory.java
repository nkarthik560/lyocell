package lyocell.document.managment.hook;

import com.liferay.document.library.display.context.DLDisplayContextFactory;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Karthik N
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = DLDisplayContextFactory.class
)
public class LyocellDocumentManagmentContextFactory implements DLDisplayContextFactory {

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
			DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext, HttpServletRequest request,
			HttpServletResponse response, DLFileEntryType dlFileEntryType) {
		// TODO Auto-generated method stub
		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
			DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext, HttpServletRequest request,
			HttpServletResponse response, FileEntry fileEntry) {
		// TODO Auto-generated method stub
		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
			DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext, HttpServletRequest request,
			HttpServletResponse response, FileShortcut fileShortcut) {
		// TODO Auto-generated method stub
		return parentDLViewFileVersionDisplayContext;
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
			DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext, HttpServletRequest request,
			HttpServletResponse response, FileVersion fileVersion) {
		// TODO Auto-generated method stub
		System.out.println("LyocellDocumentManagmentContextFactory");
		return new LyocellDocumentManagmentDispalyContext(parentDLViewFileVersionDisplayContext,request,response,fileVersion);
	}

	
	// TODO enter required service methods

}