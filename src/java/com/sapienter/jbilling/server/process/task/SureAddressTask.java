package com.sapienter.jbilling.server.process.task;

import com.sapienter.jbilling.client.suretax.SuretaxClient;
import com.sapienter.jbilling.client.suretax.request.SureAddressRequest;
import com.sapienter.jbilling.client.suretax.response.SureAddressResponse;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDTO;
import com.sapienter.jbilling.server.customerEnrollment.helper.CustomerEnrollmentFileGenerationHelper;
import com.sapienter.jbilling.server.invoice.db.SuretaxTransactionLogDAS;
import com.sapienter.jbilling.server.metafields.MetaFieldType;
import com.sapienter.jbilling.server.metafields.db.CustomizedEntity;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.event.SureAddressEvent;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neeraj on 28/10/15.
 */
public class SureAddressTask extends PluggableTask implements IInternalEventsTask {

    public static final ParameterDescription SURETAX_REQUEST_URL =
            new ParameterDescription("Sure Address Request Url", true, ParameterDescription.Type.STR);
    public static final ParameterDescription CLIENT_NUMBER =
            new ParameterDescription("Client Number", true, ParameterDescription.Type.STR);
    public static final ParameterDescription VALIDATION_KEY =
            new ParameterDescription("Validation Key", true, ParameterDescription.Type.STR);

    {
        descriptions.add(SURETAX_REQUEST_URL);
        descriptions.add(CLIENT_NUMBER);
        descriptions.add(VALIDATION_KEY);

    }

    private static final Class<Event> events[] = new Class[]{
            SureAddressEvent.class
    };

    @Override
    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    IWebServicesSessionBean webServicesSessionSpringBean;
    SuretaxTransactionLogDAS suretaxTransactionLogDAS = null;

    @Override
    public void process(Event event) throws PluggableTaskException {

        webServicesSessionSpringBean = Context.getBean(Context.Name.WEB_SERVICES_SESSION);
        suretaxTransactionLogDAS = new SuretaxTransactionLogDAS();
        SureAddressResponse sureAddressResponse = null;
        SureAddressRequest sureAddressRequest = null;

        String clientNumber = getParameter(CLIENT_NUMBER.getName(), "");
        String validationKey = getParameter(VALIDATION_KEY.getName(), "");
        String url = getParameter(SURETAX_REQUEST_URL.getName(), "");

        /* code for calculating the sure address */
        if (event instanceof SureAddressEvent) {
            SureAddressEvent sureAddressEvent = (SureAddressEvent) event;
            MetaFieldValue zipMetaFieldValue = null;
            MetaFieldValue addressMetaFieldValue = null;
            MetaFieldValue cityMetaFieldValue = null;
            MetaFieldValue stateMetaFieldValue = null;
            MetaFieldValue address2MetaFieldValue = null;
            CustomizedEntity customizedEntity = sureAddressEvent.getEntity();

            //code for getting metafield for address1, zipcode, city, state

            for (MetaFieldValue metaFieldValue : customizedEntity.getMetaFields()) {
                if (metaFieldValue.getField().getFieldUsage() != null && metaFieldValue.getField().getFieldUsage().equals(MetaFieldType.ADDRESS1)) {
                    addressMetaFieldValue = customizedEntity.getMetaField(metaFieldValue.getField().getName(), null);

                }
                if (metaFieldValue.getField().getFieldUsage() != null && metaFieldValue.getField().getFieldUsage().equals(MetaFieldType.POSTAL_CODE)) {
                    zipMetaFieldValue = customizedEntity.getMetaField(metaFieldValue.getField().getName(), null);

                }
                if (metaFieldValue.getField().getFieldUsage() != null && metaFieldValue.getField().getFieldUsage().equals(MetaFieldType.CITY)) {
                    cityMetaFieldValue = customizedEntity.getMetaField(metaFieldValue.getField().getName(), null);

                }
                if (metaFieldValue.getField().getFieldUsage() != null && metaFieldValue.getField().getFieldUsage().equals(MetaFieldType.STATE_PROVINCE)) {
                    stateMetaFieldValue = customizedEntity.getMetaField(metaFieldValue.getField().getName(), null);
                }

                if (metaFieldValue.getField().getFieldUsage() != null && metaFieldValue.getField().getFieldUsage().equals(MetaFieldType.ADDRESS2)) {
                    address2MetaFieldValue = customizedEntity.getMetaField(metaFieldValue.getField().getName(), null);
                }
            }

            sureAddressRequest=new SureAddressRequest();
            sureAddressRequest.clientNumber=clientNumber;
            sureAddressRequest.validationKey=validationKey;

            String address1=addressMetaFieldValue.getValue()!=null?(String)addressMetaFieldValue.getValue():"";
            String city= cityMetaFieldValue.getValue()!=null?(String)cityMetaFieldValue.getValue():"";
            String state=stateMetaFieldValue.getValue()!=null?(String)stateMetaFieldValue.getValue():"";

            List<String> errorMessages=new ArrayList<String>();
            if(address1.equals("")){
                errorMessages.add("Address1 is required");
            }
            if(city.equals("")){
                errorMessages.add("City is required");
            }
            if(state.equals("")){
                errorMessages.add("State is required");
            }

            if(errorMessages.size()>0){
                SessionInternalError sessionInternalError=new SessionInternalError();
                sessionInternalError.setErrorMessages(errorMessages.toArray(new String[errorMessages.size()]));
                throw sessionInternalError;
            }

            sureAddressRequest.address1=address1;
//            sureAddressRequest.address2=address2;
            sureAddressRequest.city=city;
            sureAddressRequest.state=state;

            String stateCode=null;
            if(!state.equals("")){
                stateCode=CustomerEnrollmentFileGenerationHelper.USState.getAbbreviationForState(state);
                sureAddressRequest.state=stateCode;
            }

            String zipcode=(String)zipMetaFieldValue.getValue();

            sureAddressResponse = new SuretaxClient().getAddressResponse(sureAddressRequest,
                    url);

            if(sureAddressResponse==null){
                throw new SessionInternalError("Not able to connect with SureAddress API", new String[]{"Not able to connect with SureAddress API"});
            }

            if (sureAddressResponse.getMessage() != null) {
                throw new SessionInternalError(sureAddressResponse.getMessage(), new String[] { "No search result found for given address '"+address1+"'and city '"+city+"' and state'"+stateCode+"'. Please provide valid address"});
            }

            if(!sureAddressResponse.getAddress1().toUpperCase().equals(address1.toUpperCase())){
                throw new SessionInternalError("Provided address '"+address1+"' is not equal to the searched address'"+sureAddressResponse.getAddress1()+"'", new String[]{"Address found '"+sureAddressResponse.getAddress1()+"' instead of '"+address1+"'"});
            }

            if(!sureAddressResponse.getCity().toUpperCase().equals(city.toUpperCase())){
                throw new SessionInternalError("Provided city ("+city+") is not equal to the searched city("+sureAddressResponse.getCity()+")", new String[]{"City found ("+sureAddressResponse.getCity()+") instead of("+city+")"});
            }

            if(!sureAddressResponse.getState().toUpperCase().equals(stateCode.toUpperCase())){
                throw new SessionInternalError("Provided state ("+stateCode+") is not equal to the searched state ("+sureAddressResponse.getState()+")", new String[]{"State found '"+sureAddressResponse.getState() +"' instead of '"+stateCode+"'"});
            }

            if(errorMessages.size()>0){
                SessionInternalError sessionInternalError=new SessionInternalError();
                sessionInternalError.setErrorMessages(errorMessages.toArray(new String[errorMessages.size()]));
                throw sessionInternalError;
            }

            String zipPlusFourCode=sureAddressResponse.getZipCode() + "" + sureAddressResponse.getZIPPlus4();
            zipMetaFieldValue.setValue(zipPlusFourCode);
            customizedEntity.setMetaField(zipMetaFieldValue, null);

            String zipPlus4Code=sureAddressResponse.getZipCode()+""+sureAddressResponse.getZIPPlus4();
            String message="";
            if(zipcode ==null){
                message="The valid zip code for provided address is "+zipPlus4Code;
            }

            if((zipcode!=null && !zipPlusFourCode.equals(zipcode))){
                 message="The valid zip code for provided address is "+zipPlus4Code+" instead of "+zipcode+". Please confirm";
            }

            if(customizedEntity instanceof CustomerEnrollmentDTO && !message.equals("")){
                CustomerEnrollmentDTO customerEnrollmentDTO=(CustomerEnrollmentDTO)customizedEntity;
                customerEnrollmentDTO.setMessage(message);
            }

        }
    }

}