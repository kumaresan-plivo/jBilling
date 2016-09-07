/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package jbilling

import com.sapienter.jbilling.common.Util
import com.sapienter.jbilling.server.metafields.MetaFieldExternalHelper
import com.sapienter.jbilling.server.metafields.MetaFieldHelper
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy
import com.sapienter.jbilling.server.util.Constants
import com.sapienter.jbilling.server.metafields.MetaFieldBL
import com.sapienter.jbilling.server.metafields.db.MetaField
import com.sapienter.jbilling.server.user.db.AccountTypeDTO
import com.sapienter.jbilling.server.user.db.CompanyDTO

/**
* JBillingDisplayTagLib
*
* @author Vikas Bodani
* @since 03/09/11
*/

class JBillingDisplayTagLib {

    
    def showProperCase = { attrs, body -> 
        
        StringBuffer sb= new StringBuffer("")
        
        String str= attrs.value
        if (str) {
            sb.append(str.charAt(0).toUpperCase())
            if (str.length() > 1) 
                sb.append(str.substring(1))
        }
        
        out << sb.toString()
    }
    
    /**
     * Prints the phone number is a nice format
     */
    def phoneNumber = { attrs, body ->
        
        def countryCode= attrs.countryCode
        def areaCode= attrs.areaCode 
        def number= attrs.number
        
        StringBuffer sb= new StringBuffer("");
        
        if (countryCode) {
            sb.append(countryCode).append("-")
        }
        
        if (areaCode) {
            sb.append(areaCode).append("-")
        }
        if (number) {
            
            if (number.length() > 4) {
                char[] nums= number.getChars()
                
                int i=0;
                for(char c: nums) {
                   //check if this value is a number between 0 and 9
                   if (c < 58 && c > 47 ) {
                       if (i<3) {
                           sb.append(c)
                           i++
                       } else if (i == 3) {
                           sb.append("-").append(c)
                           i++
                       } else {
                           sb.append(c)
                           i++
                       }
                   }
                }
            } else {
                sb.append(number)
            }
        }
        
        out << sb.toString()
    }

    def isSubsProd = { attrs, body ->
        for (def type : attrs?.plan.itemTypes) {
            if (type.orderLineTypeId == Constants.ORDER_LINE_TYPE_SUBSCRIPTION.intValue()) {
                out << "true"
                break
            }
        }
    }

   def hasAssetProduct = { attrs, body ->
       for (def planItem : attrs?.plan.plans.asList()?.first().planItems) {
           def item = planItem?.item
           def bundle = planItem?.bundle
           if (item.assetManagementEnabled == 1 && bundle?.quantity.compareTo(BigDecimal.ZERO) > 0) {
               out << item.id
               break
           }
       }
    }

    def accountTypeMetaFields = { attrs, body ->
        def filter=attrs.filter
        out << "<select style='float:left;' name=${filter.field}.fieldKeyData>"
        AccountTypeDTO.findAllByCompany(new CompanyDTO(session['company_id'])).each {
            Map<Integer, List<MetaField>> mfList = MetaFieldExternalHelper.getAvailableAccountTypeFieldsMap(it.id)

            out << "<option value>--${it.description}</option>"
            mfList.each { Integer key, List value ->
                value.each {
                    out << " <option value=${it.id} ${filter?.fieldKeyData?.equals(it?.id?.toString())?'selected=\"selected\"':''}>${it.name}</option>"
                }
            }
        }
        out << '</select>'
    }

    def formatPriceForDisplay = { attrs, body ->
        def outputString= '0.0000'

        if ( attrs.price ) {
            def price= attrs.price

            outputString= Util.formatRateForDisplay(price.rate)

            if ( price.type == PriceModelStrategy.LINE_PERCENTAGE ) {
                outputString= '%' + outputString
            } else {
                outputString= price.currency?.symbol + outputString
            }
        } else {
            // no price, show 0.0000
        }
        out << outputString
    }


}