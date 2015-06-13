package org.technikradio.jay_corp.user;

import org.technikradio.JayCorp.server.MetaSheet;

public class MetaReg {
	public static final String SHOULD_CHANGE_PASSWORD = "MustChangePassword";
	public static final String MUST_SET_EXTRA_DAYS = "MustSetDays";

	public static final void setDefaultMetaData(MetaSheet s) {
		s.setValue(SHOULD_CHANGE_PASSWORD, "true");
		s.setValue(MUST_SET_EXTRA_DAYS, "true");
	}
}
