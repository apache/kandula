package org.apache.kandula.integration.ba;

import org.apache.kandula.Status;
import org.apache.kandula.ba.BusinessActivityCallBack;
import org.apache.kandula.ba.MixedBusinessActivity;
import org.apache.kandula.coordinator.ba.BAParticipantInformation;

public class DemoServiceActivityCallBack extends BusinessActivityCallBack {

	public DemoServiceActivityCallBack() {
		super();
	}

	public void onComplete() {
		try {
			MixedBusinessActivity mixedBusinessActivity = (MixedBusinessActivity)businessActivity;
			//choose just one airline
			BAParticipantInformation emirates = mixedBusinessActivity.getParticipant("EmiratesAirlineBooking");
			BAParticipantInformation srilankan = mixedBusinessActivity.getParticipant("SriLankanAirlineBooking");
			BAParticipantInformation singapore = mixedBusinessActivity.getParticipant("SingaporeAirlineBooking");
			
			if (srilankan!=null && (srilankan.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(srilankan);
				mixedBusinessActivity.addParticipantToCompensateList(emirates);
				mixedBusinessActivity.addParticipantToCompensateList(singapore);
			}else if (emirates!=null && (emirates.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(emirates);
				mixedBusinessActivity.addParticipantToCompensateList(srilankan);
				mixedBusinessActivity.addParticipantToCompensateList(singapore);
			}else if (singapore!=null && (singapore.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(singapore);
				mixedBusinessActivity.addParticipantToCompensateList(emirates);
				mixedBusinessActivity.addParticipantToCompensateList(srilankan);
			}

			BAParticipantInformation taj = mixedBusinessActivity.getParticipant("TajHotelBooking");
			BAParticipantInformation hilton = mixedBusinessActivity.getParticipant("HiltonHotelBooking");
			
			if (taj!=null && (taj.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(taj);
				mixedBusinessActivity.addParticipantToCompensateList(hilton);
			}else if (hilton!=null && (hilton.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(hilton);
				mixedBusinessActivity.addParticipantToCompensateList(taj);
			}
			
			BAParticipantInformation hertz = mixedBusinessActivity.getParticipant("HertzCarBooking");
			BAParticipantInformation malkey = mixedBusinessActivity.getParticipant("MalkeyCarBooking");
			if (hertz!=null && (hertz.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(hertz);
				mixedBusinessActivity.addParticipantToCompensateList(malkey);
			}else if (malkey!=null && (malkey.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(malkey);
				mixedBusinessActivity.addParticipantToCompensateList(hertz);
			}
			
			BAParticipantInformation map = mixedBusinessActivity.getParticipant("MapOrder");
			if (map!=null && (map.getStatus()==Status.BACoordinatorStatus.STATUS_COMPLETED)){
				mixedBusinessActivity.addParticipantToCloseList(map);
			}else 
			{
				mixedBusinessActivity.addParticipantToCompensateList(map);
			}
			
			mixedBusinessActivity.finalizeActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*	public void onComplete() {
		try {
			((AtomicBusinessActivity)businessActivity).closeActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
