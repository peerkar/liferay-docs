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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.liferay.docs.guestbook.service.persistence.EntryUtil;
import com.liferay.docs.guestbook.wrappers.Entry;
import com.liferay.docs.guestbook.service.EntryLocalServiceTracker;
import com.liferay.docs.guestbook.service.EntryLocalService;

import com.liferay.faces.portal.context.LiferayFacesContext;


/**
 * @author  Vernon Singleton
 */
@ManagedBean
@RequestScoped
public class EntryBacking extends AbstractBacking {

	private Boolean hasAddPermission;

	private EntryLocalServiceTracker entryLocalServiceTracker;
	
	public EntryLocalService getEntryLocalService() {
		EntryLocalService entryLocalService = entryLocalServiceTracker.getService();
	
		return entryLocalService;
	}

	@ManagedProperty(name = "guestbookBacking", value = "#{guestbookBacking}")
	protected GuestbookBacking guestbookBacking;

	public void add() {
		Entry entry = new Entry(EntryUtil.create(0L));
		LiferayFacesContext liferayFacesContext = LiferayFacesContext.getInstance();
		entry.setGroupId(liferayFacesContext.getScopeGroupId());
		entry.setGuestbookId(guestbookBacking.getSelectedGuestbook().getGuestbookId());
		guestbookBacking.setSelectedEntry(entry);
		guestbookBacking.editEntry();
	}

	public void cancel() {
		guestbookBacking.select(guestbookBacking.getSelectedGuestbook());
	}

	public void delete(Entry entry) {

		try {
			getEntryLocalService().deleteEntry(entry);
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
		LiferayFacesContext liferayFacesContext = LiferayFacesContext.getInstance();
		entry.setCompanyId(liferayFacesContext.getCompanyId());
		entry.setUserId(liferayFacesContext.getUserId());

		try {

			if (entry.getEntryId() == 0) {
				getEntryLocalService().addEntry(entry, liferayFacesContext.getUserId());
			}
			else {
				getEntryLocalService().updateEntry(entry);
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
			LiferayFacesContext liferayFacesContext = LiferayFacesContext.getInstance();
			long scopeGroupId = liferayFacesContext.getScopeGroupId();
			hasAddPermission = liferayFacesContext.getThemeDisplay().getPermissionChecker().hasPermission(scopeGroupId,
					GuestbookBacking.MODEL, scopeGroupId, "ADD_ENTRY");
		}

		return hasAddPermission;
	}

	public void setHasAddPermission(Boolean hasAddPermission) {
		this.hasAddPermission = hasAddPermission;
	}

}
