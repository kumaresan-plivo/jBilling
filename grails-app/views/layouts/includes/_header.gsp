%{--
  JBILLING CONFIDENTIAL
  _____________________

  [2003] - [2012] Enterprise jBilling Software Ltd.
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Enterprise jBilling Software.
  The intellectual and technical concepts contained
  herein are proprietary to Enterprise jBilling Software
  and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden.
  --}%

<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; com.sapienter.jbilling.common.Constants; jbilling.SearchType; com.sapienter.jbilling.server.user.db.CompanyDTO; com.sapienter.jbilling.server.user.db.CompanyDAS; com.sapienter.jbilling.server.util.ColorConverter"%>

<%--
  Page header for all common jBilling layouts.

  This contains the jBilling top-navigation bar, search form and main navigation menu.

  @author Brian Cowdery
  @since  23-11-2010
--%>
<%
	def company = CompanyDTO.load(session['company_id'])
	def childEntities = CompanyDTO.findAllByParent(company)
%>
<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
	function clearPlaceHolder(src)
	{
		var str = src.placeholder;
		str = str != null ? str.replace("${message(code:'search.title')}", "") : str;
		$('#id').attr('placeholder', str); 
	}
	
	function setPlaceHolder()
	{
		var str = '${message(code:'search.title')}';
		$('#id').attr('placeholder', str);
	}
    function updateTips( t ) {
        tips.text( t ).addClass( "ui-state-error" );
    }
    
	$(document).ready(function() {
    	setImpersonationUser();
	});
    
    function checkLength( o, n, min, max ) {
        if ( o.val().length > max || o.val().length < min ) {
            o.addClass( "ui-state-error" );
            updateTips( "Length of " + n + " must be between " + min + " and " + max + "." );
            return false;
        } else {
            return true;
        }
    }

    $(function() 
    {
    	$( "#impersonation-dialogue" ).dialog({
    										autoOpen: ${flash.errorAuthToFail != null} ? true : false,
    										height: 200,
    										width: 	480,
    										modal: 	true
    	});

        $('#impersonation-dialogue').on('dialogclose', function(event) {
            $("#authEventFail").remove();
        });
    			
    	$( "#impersonate" ).click(function() 
    	{	
    		$( "#impersonation-dialogue" ).dialog( "open" );
    	});
    });

    function setImpersonationUser() {
		$.ajax({
            url: '${createLink(action: 'getUserByCompany', controller: 'user')}',
            type: 'POST',
            data: { entityId: $( "#impersonation-select" ).val() },
            success: function (result) {
				$('input[name = j_username]').val(result.name);
            }
        });
    }

  	//Following function added to fix #7338 - Tabs are not properly located on page in case of large number of tabs on screen.

    function redrawTabs() {
		var windowSize = Math.floor($(window).width() / 100);
		var safeDisplayTabsLength = windowSize - 1;
		var tabsLength = $('#navList li').length;
		var surplusTabs = tabsLength - safeDisplayTabsLength;
		
		if (surplusTabs > 0) {
			var $navigationTabs = $('#navList');
			var $dropDownTabs = "<li><a><span>+</span></a><ul>";
			var dropDownArray = [];
			var activeCount = 0;
			$($("#navList li").get().reverse()).each(function(index) {
				if (!$(this).hasClass("active")) {
					dropDownArray.push($(this).html());
					$(this).remove();
					if ((index - activeCount) >= (surplusTabs - 1)) {
						return false;
					}
				} else {
					activeCount++;
				}
			});

			for (var i = dropDownArray.length; i-- > 0;) {
				$dropDownTabs += '<li class="">' + dropDownArray[i] + '</li>';
			}
			$navigationTabs.append($dropDownTabs).append('</ul></li>');
		}
	}

	$(document).ready(function() {
		redrawTabs();
        if ('${params.showAssetUploadTemplate}') {
            $("#uploadAsset").click();
        }
        if(${params.controller=="customerEnrollment"}){
            $(".customer").addClass("active");
        }
        if(${params.controller=="ediFile"}){
            $(".edi").addClass("active");
        }
        if(${params.controller=="ediReport"}){
            $(".edi").addClass("active");
        }
	});
</script>

<style>
    .active ul li a { background: none !important; box-shadow: none !important; border: none !important;}
    .active:hover ul li a { background: none !important;}
    .active ul li:hover a{ background: #ffffff !important;}
    .active ul li:hover a{ background: #ffffff !important;}
    .active ul li a:active { background: #ffffff !important;}
</style>


<g:render template="/layouts/includes/uiColorStyle"/>

<!-- header -->
<div id="header">
    <h1><a href="${resource(dir:'')}">jBilling</a></h1>
    <div class="search">
        <g:form controller="search" name="search-form">
            <fieldset>
                <input type="image" class="btn" src="${resource(dir:'images', file:'icon-search.gif')}" onclick="$('#search-form').submit()" />
                <div class="input-bg">
                    <g:textField name="id" placeholder="${cmd?.id ?: message(code:'search.title')}" class="default" onclick="clearPlaceHolder(this);" onkeydown="setPlaceHolder();" />
                    <a href="#" class="open"></a>
                    <div class="popup">
                        <div class="top-bg">
                            <div class="btm-bg">
                                <sec:access url="/customer/list">
                                    <div class="input-row">
                                        <g:radio id="customers" name="type" value="CUSTOMERS" checked="${!cmd || cmd?.type?.toString() == 'CUSTOMERS'}"/>
                                        <label for="customers"><g:message code="search.option.customers"/></label>
                                    </div>
                                </sec:access>
                                <sec:access url="/order/list">
                                    <div class="input-row">
                                        <g:radio id="orders" name="type" value="ORDERS" checked="${cmd?.type?.toString() == 'ORDERS'}"/>
                                        <label for="orders"><g:message code="search.option.orders"/></label>
                                    </div>
                                </sec:access>
                                <sec:access url="/invoice/list">
                                    <div class="input-row">
                                        <g:radio id="invoices" name="type" value="INVOICES" checked="${cmd?.type?.toString() == 'INVOICES'}"/>
                                        <label for="invoices"><g:message code="search.option.invoices"/></label>
                                    </div>
                                </sec:access>
                                <sec:access url="/payment/list">
                                    <div class="input-row">
                                        <g:radio id="payments" name="type" value="PAYMENTS" checked="${cmd?.type?.toString() == 'PAYMENTS'}"/>
                                        <label for="payments"><g:message code="search.option.payments"/></label>
                                    </div>
                                </sec:access>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>

    <ul class="top-nav">
        <sec:ifSwitched>
        	<g:set var="switchedUserOriginalUsername" value="${SpringSecurityUtils.switchedUserOriginalUsername}"/>
        	<g:if test="${switchedUserOriginalUsername?.substring(switchedUserOriginalUsername.indexOf(';') + 1, switchedUserOriginalUsername.length()).equals(session['company_id'].toString())}">
            	<li>
                	<g:message code="switch.user.working.as"/> <sec:loggedInUserInfo field="plainUsername"/>
            	</li>
            	<li>
                	<a href="${request.contextPath}/j_spring_security_exit_user">
                    	<g:set var="plainUsername" value="${switchedUserOriginalUsername?.substring(0, switchedUserOriginalUsername.indexOf(';'))}"/>
                    	<g:message code="switch.user.resume.session.as"/> ${plainUsername}
               		</a>
            	</li>
           	</g:if>
        </sec:ifSwitched>

        <sec:ifNotSwitched>
        	<li><%=company.getDescription()%></li>
            <li><g:message code="topnav.greeting"/> <sec:loggedInUserInfo field="plainUsername"/></li>
        </sec:ifNotSwitched>
        
        <g:if test="${CompanyDTO.get(session['company_id'])?.parent == null || (childEntities != null && childEntities.size() > 0)}">
        	<sec:ifSwitched>
        		<g:set var="switchedUserOriginalUsername" value="${SpringSecurityUtils.switchedUserOriginalUsername}"/>
        		<g:if test="${!switchedUserOriginalUsername?.substring(switchedUserOriginalUsername.indexOf(';') + 1, switchedUserOriginalUsername.length()).equals(session['company_id'].toString())}">
					<li>
						<li>
                			<g:message code="switch.user.working.as"/> <sec:loggedInUserInfo field="plainUsername"/>
            			</li>
            		
						<a href="${request.contextPath}/j_spring_security_exit_user" class="dissimulate">
        					${CompanyDTO.get(session['company_id'].toInteger())?.description}
        				</a>
        			</li>
        		</g:if>
        	</sec:ifSwitched>
        	<sec:ifNotSwitched>
				<li>
					<a id="impersonate"><g:message code="topnav.link.impersonate"/></a>
        		</li>
        	</sec:ifNotSwitched>
        </g:if>
        <g:else>
        	<sec:ifSwitched>
        		<g:set var="switchedUserOriginalUsername" value="${SpringSecurityUtils.switchedUserOriginalUsername}"/>
        		<g:if test="${!switchedUserOriginalUsername?.substring(switchedUserOriginalUsername.indexOf(';') + 1, switchedUserOriginalUsername.length()).equals(session['company_id'].toString())}">
					<li>
						<li>
                			<g:message code="switch.user.working.as"/> <sec:loggedInUserInfo field="plainUsername"/>
            			</li>
            		
						<a href="${request.contextPath}/j_spring_security_exit_user" class="dissimulate">
        					${CompanyDTO.get(session['company_id'].toInteger())?.description}
        				</a>
        			</li>
        		</g:if>
        	</sec:ifSwitched>
        </g:else>
        
        <sec:ifAnyGranted roles="MY_ACCOUNT_160,MY_ACCOUNT_161,MY_ACCOUNT_162,ROLE_SUPER_USER">
        <li>
            <g:link controller="myAccount" class="account">
                <g:message code="topnav.link.account"/>
            </g:link>
        </li>
        </sec:ifAnyGranted>

        <li>
            <a href="http://www.jbilling.com/professional-services/training" class="training">
                <g:message code="topnav.link.training"/>
            </a>
        </li>
        <li>
            <a href="${resource(dir:'manual', file: 'index.html')}" class="help">
                <g:message code="topnav.link.help"/>
            </a>
        </li>
        <li>
            <g:link controller='logout' class="logout">
                <g:message code="topnav.link.logout"/>
            </g:link>
        </li>
    </ul>

    <div id="navigation">
        %{
            def hiddenTabs = []
        }%
        <%-- select the current menu item based on the controller name --%>
        <ul id="navList">
            <g:each in="${session['user_tabs'].tabConfigurationTabs.findAll{it.tab.parentTab == null}}" var="tabConfig">
                <jB:userCanAccessTab tab="${tabConfig.tab}">
                    <g:if test="${tabConfig.visible && !tabConfig?.tab?.parentTab}">
                        <g:if test="${tabConfig?.tab?.parentTab==null}">
                            <g:set var="childTabs" value="${session['user_tabs'].tabConfigurationTabs.tab.findAll{it?.parentTab?.id==tabConfig?.tab?.id}}"/>

                            <li class="${(controllerName == tabConfig.tab.controllerName || controllerName in childTabs?.controllerName)? 'active' : ''}">
                                <g:if test="${childTabs && childTabs.size() > 1}">
                                    <a><span><g:message code="${tabConfig.tab.messageCode}"/></span></a>
                                    <ul>
                                        <g:each in="${childTabs}" var="childTab">
                                            <jB:userCanAccessTab tab="${childTab}">
                                                <li class="${controllerName == childTab.controllerName ? 'active' : ''}">
                                                    <g:link controller="${childTab.controllerName}">
                                                        <span><g:message code="${childTab.messageCode}"/></span>
                                                    </g:link>
                                                </li>
                                            </jB:userCanAccessTab>
                                        </g:each>
                                    </ul>
                                </g:if>
                                <g:else>
                                    <g:link controller="${tabConfig.tab.controllerName}">
                                        <span><g:message code="${tabConfig.tab.messageCode}"/></span>
                                    </g:link>
                                </g:else>
                            </li>
                        </g:if>
                    </g:if>
                    <g:else>
                        %{
                            hiddenTabs.add(tabConfig.tab)
                        }%
                    </g:else>
                </jB:userCanAccessTab>
            </g:each>
            <g:if test="${hiddenTabs.size() > 0}">
                    <li><a><span>+</span></a>
                        <ul>
                            <g:each var="tab" in="${hiddenTabs}">
                                <li>
                                    <g:link controller="${tab.controllerName}"><span><g:message code="${tab.messageCode}"/></span><em></em></g:link>
                                </li>
                            </g:each>
                        </ul>
                   </li>
            </g:if>
        </ul>
    </div>
    <div id="impersonation-dialogue" title="Impersonate">
    	
    	<g:if test = "${childEntities.size() > 0}">
    		<form id="impersonation-form" action="${request.contextPath}/j_spring_security_switch_user" method="POST">
    			<g:hiddenField id="security_username"  name="j_username"/>
   				<div id="impersonation-text">Please select a child entity to impersonate:</div>
                <g:if test="${flash.errorAuthToFail}">
                    <div id="authEventFail" class="msg-box error">
                        <g:message code="${flash.errorAuthToFail}"/>
                    </div>
                </g:if>

   				<div>
			 		<g:select id="impersonation-select" name="entityId"
                          from="${childEntities}"
                          optionKey="id"
                          optionValue="${{it?.description}}"
                          value="${entityId}"
                          onChange="setImpersonationUser();"
                    />
         		</div>
         		<div>
         			<a id="impersonation-button" onclick="$('#impersonation-form').submit()" class="submit select"><span><g:message code="button.select"/></span></a>
         		</div>
         	</form>
         </g:if>
         <g:else>
         	<strong>This Company does not have any child company!</strong>
         </g:else>
	</div>
</div>
