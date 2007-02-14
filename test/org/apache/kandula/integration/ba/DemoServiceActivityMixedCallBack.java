package org.apache.kandula.integration.ba;

import org.apache.kandula.ba.BusinessActivityCallBack;
import org.apache.kandula.ba.MixedBusinessActivity;

public class DemoServiceActivityMixedCallBack extends BusinessActivityCallBack {

	public DemoServiceActivityMixedCallBack() {
		super();
	}

	public void onComplete() {
		try {
			MixedBusinessActivity mixedBusinessActivity = ((MixedBusinessActivity)businessActivity);
			mixedBusinessActivity.addParticipantToCloseList("creditingBank");
			mixedBusinessActivity.addParticipantToCompensateList("debitingBank");
			mixedBusinessActivity.finalizeActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
