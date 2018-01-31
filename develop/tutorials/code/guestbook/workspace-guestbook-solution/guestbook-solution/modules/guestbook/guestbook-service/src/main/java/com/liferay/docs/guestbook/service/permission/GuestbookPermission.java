package com.liferay.docs.guestbook.service.permission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.docs.guestbook.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

		@Component(
			immediate = true, 
			property = {
				"model.class.name=" + GuestbookPermission.RESOURCE_NAME
			}
		)
        public class GuestbookPermission implements BaseModelPermissionChecker {

			public static final String RESOURCE_NAME =
				"com.liferay.docs.guestbook.model.Guestbook";

            public static void check(PermissionChecker permissionChecker,
                    long guestbookId, String actionId) throws PortalException,
                    SystemException {

                if (!contains(permissionChecker, guestbookId, actionId)) {
                    throw new PrincipalException();
                }
            }

			@Override
			public void checkBaseModel(
					PermissionChecker permissionChecker, long groupId, long primaryKey,
					String actionId)
				throws PortalException {

				check(permissionChecker, primaryKey, actionId);
			}

            public static boolean contains(PermissionChecker permissionChecker,
                    long guestbookId, String actionId) throws PortalException,
                    SystemException {

                Guestbook guestbook = _guestbookLocalService.getGuestbook(guestbookId);

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

			@Reference(unbind = "-")
			protected void setGuestbookLocalService(
				GuestbookLocalService guestbookLocalService) {

				_guestbookLocalService = guestbookLocalService;
			}

			private static GuestbookLocalService _guestbookLocalService;
        }