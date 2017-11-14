package com.liferay.docs.guestbook.service;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class EntryLocalServiceTracker extends ServiceTracker<EntryLocalService, EntryLocalService> {

	public EntryLocalServiceTracker(BundleContext bundleContext) {
		super(bundleContext, EntryLocalService.class, null);
	}

}
