/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.docs.guestbook.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.liferay.docs.guestbook.service.persistence.EntryUtil;
import com.liferay.docs.guestbook.wrappers.Entry;
import com.liferay.docs.guestbook.service.EntryLocalServiceTracker;
import com.liferay.docs.guestbook.service.EntryLocalService;

import com.liferay.faces.portal.context.LiferayPortletHelperUtil;


/**
 * @author  Vernon Singleton
 */
@ManagedBean(name = "entryBackingBean")
@RequestScoped
public class EntryBacking extends AbstractBacking {

	private Boolean hasAddPermission;

	private EntryLocalServiceTracker entryLocalServiceTracker;

	@ManagedProperty(name = "guestbookBacking", value = "#{guestbookBacking}")
	protected GuestbookBacking guestbookBacking;

	public void add() {
		Entry entry = new Entry(EntryUtil.create(0L));
		FacesContext facesContext = FacesContext.getCurrentInstance();
		entry.setGroupId(LiferayPortletHelperUtil.getScopeGroupId(facesContext));
		entry.setGuestbookId(guestbookBacking.getSelectedGuestbook().getGuestbookId());
		guestbookBacking.setSelectedEntry(entry);
		guestbookBacking.editEntry();
	}

	public void cancel() {
		guestbookBacking.select(guestbookBacking.getSelectedGuestbook());
	}

	public void delete(Entry entry) {

		try {
			EntryLocalService entryLocalService = entryLocalServiceTracker.getService();
			entryLocalService.deleteEntry(entry);
			addGlobalSuccessInfoMessage();
		}
		catch (Exception e) {
			addGlobalUnexpectedErrorMessage();
			logger.error(e);
		}

		guestbookBacking.select(guestbookBacking.getSelectedGuestbook());
	}

	public void edit(Entry entry) {
		guestbookBacking.setSelectedEntry(entry);
		guestbookBacking.editEntry();
	}

	@PostConstruct
	public void postConstruct() {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		entryLocalServiceTracker = new EntryLocalServiceTracker(bundleContext);
		entryLocalServiceTracker.open();
	}

	@PreDestroy
	public void preDestroy() {
		entryLocalServiceTracker.close();
	}

	public void save() {

		Entry entry = guestbookBacking.getSelectedEntry();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		entry.setCompanyId(LiferayPortletHelperUtil.getCompanyId(facesContext));
		entry.setUserId(LiferayPortletHelperUtil.getUserId(facesContext));

		try {
			EntryLocalService entryLocalService = entryLocalServiceTracker.getService();

			if (entry.getEntryId() == 0) {
				entryLocalService.addEntry(entry, LiferayPortletHelperUtil.getUserId(facesContext));
			}
			else {
				entryLocalService.updateEntry(entry);
			}

			addGlobalSuccessInfoMessage();
		}
		catch (Exception e) {
			addGlobalUnexpectedErrorMessage();
			logger.error(e);
		}

		guestbookBacking.select(guestbookBacking.getSelectedGuestbook());
	}

	public void setGuestbookBacking(GuestbookBacking guestbookBacking) {
		this.guestbookBacking = guestbookBacking;
	}

	public Boolean getHasAddPermission() {

		if (hasAddPermission == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			long scopeGroupId = LiferayPortletHelperUtil.getScopeGroupId(facesContext);
			hasAddPermission = LiferayPortletHelperUtil.getThemeDisplay(facesContext).getPermissionChecker().hasPermission(scopeGroupId,
					GuestbookBacking.MODEL, scopeGroupId, "ADD_ENTRY");
		}

		return hasAddPermission;
	}

	public void setHasAddPermission(Boolean hasAddPermission) {
		this.hasAddPermission = hasAddPermission;
	}

}
