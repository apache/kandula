package org.apache.kandula.initiator;

import org.apache.kandula.Status;

public class CompletionCallback {

	private InitiatorContext initiatorTransaction;
	
	private int status = Status.CoordinatorStatus.STATUS_NONE;
	
	private boolean complete = false;
	
	private Exception e;

	public CompletionCallback(InitiatorContext initiatorTransaction) {
		super();
		this.initiatorTransaction = initiatorTransaction;
	}

	public boolean isComplete() {
		return complete;
	}
	
	public int getResult() throws Exception
	{
		if (complete)
		{
			if (e!=null)
			{
				throw e;
			}else
			{
				return status;
			}
		}
		return -1;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public void setError(Exception e) {
		this.e = e;
	}
	public void setStatus(int status)
	{
		this.status = status;
		initiatorTransaction.setStatus(status);
	}
}
