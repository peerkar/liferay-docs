package com.liferay.docs.guestbook.service.permission;

import org.osgi.service.component.annotations.Component;

import com.liferay.docs.guestbook.constants.GuestbookPortletKeys;
        import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.service.GuestbookLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

		@Component(
			immediate = true, 
			property = {
				"model.class.name=" + GuestbookPermission.RESOURCE_NAME
			}
		)
        public class GuestbookPermission {

			public static final String RESOURCE_NAME =
				"com.liferay.docs.guestbook.model.Guestbook";

            public static void check(PermissionChecker permissionChecker,
                    long guestbookId, String actionId) throws PortalException,
                    SystemException {

                if (!contains(permissionChecker, guestbookId, actionId)) {
                    throw new PrincipalException();
                }
            }

            public static boolean contains(PermissionChecker permissionChecker,
                    long guestbookId, String actionId) throws PortalException,
                    SystemException {

                Guestbook guestbook = GuestbookLocalServiceUtil
                        .getGuestbook(guestbookId);

				return contains(permissionChecker, guestbook, actionId);

            }

			public static boolean contains(
				PermissionChecker permissionChecker, Guestbook guestbook,
				String actionId) {

				Boolean hasPermission = StagingPermissionUtil.hasPermission(
					permissionChecker, guestbook.getGroupId(),
					Guestbook.class.getName(), guestbook.getGuestbookId(),
					GuestbookPortletKeys.GUESTBOOK, actionId);

				if (hasPermission != null) {
					return hasPermission.booleanValue();
				}

                return permissionChecker
                        .hasPermission(guestbook.getGroupId(),
                                Guestbook.class.getName(), guestbook.getGuestbookId(),
                                actionId);

            }
        }