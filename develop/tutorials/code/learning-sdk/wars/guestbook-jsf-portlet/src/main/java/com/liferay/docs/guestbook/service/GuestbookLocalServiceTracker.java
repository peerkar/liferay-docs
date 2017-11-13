package com.liferay.docs.guestbook.service;

import org.osgi.framework.BundleContext;

import org.osgi.util.tracker.ServiceTracker;

public class GuestbookLocalServiceTracker extends ServiceTracker<GuestbookLocalService, GuestbookLocalService> {

	public GuestbookLocalServiceTracker(BundleContext bundleContext) {
		super(bundleContext, GuestbookLocalService.class, null);
	}
	
}
