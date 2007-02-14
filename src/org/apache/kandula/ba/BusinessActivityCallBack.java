package org.apache.kandula.ba;

import org.apache.kandula.context.impl.BAActivityContext;

public abstract class BusinessActivityCallBack {
	
	protected BusinessActivity businessActivity;

	public abstract void onComplete();
	
	protected void setBusinessActivity(BusinessActivity businessActivity){
		this.businessActivity = businessActivity;
	}
}
