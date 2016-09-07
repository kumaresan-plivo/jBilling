<%@ page import="org.codehaus.groovy.grails.io.support.FileSystemResource; com.sapienter.jbilling.server.util.ColorConverter"%>

${ColorConverter.setBaseColor()}
<g:set var="COLOR_8EC549" value="${ColorConverter.convert(ColorConverter.DIFF_BASE)}"/>
<g:set var="COLOR_CCE4AD" value="${ColorConverter.convert(ColorConverter.DIFF_CCE4AD)}"/>
<g:set var="COLOR_A7CEAA" value="${ColorConverter.convert(ColorConverter.DIFF_A7CEAA)}"/>
<g:set var="COLOR_8DC449" value="${ColorConverter.convert(ColorConverter.DIFF_8DC449)}"/>
<g:set var="COLOR_8BC53D" value="${ColorConverter.convert(ColorConverter.DIFF_8BC53D)}"/>
<g:set var="COLOR_7BAA3F" value="${ColorConverter.convert(ColorConverter.DIFF_7BAA3F)}"/>
<g:set var="COLOR_3C9343" value="${ColorConverter.convert(ColorConverter.DIFF_3C9343)}"/>
<g:set var="COLOR_3C9242" value="${ColorConverter.convert(ColorConverter.DIFF_3C9242)}"/>
<g:set var="COLOR_4B8C3C" value="${ColorConverter.convert(ColorConverter.DIFF_4B8C3C)}"/>
<g:set var="COLOR_347F3A" value="${ColorConverter.convert(ColorConverter.DIFF_347F3A)}"/>
<g:set var="COLOR_008000" value="${ColorConverter.convert(ColorConverter.DIFF_008000)}"/>
<g:set var="COLOR_37672C" value="${ColorConverter.convert(ColorConverter.DIFF_37672C)}"/>
<g:set var="COLOR_35642B" value="${ColorConverter.convert(ColorConverter.DIFF_35642B)}"/>

<style type="text/css">
    /* Configurable UI Logo */
    #header h1 a {
        background: url(${logoLink(favicon:false)}) no-repeat;
        background-size: contain;
    }

    /* Configurable UI Colors */
    a {
      color: ${COLOR_8EC549};
    }

    a:focus, a:hover {
      color: ${COLOR_3C9343};
    }

    #header .top-nav li {
      color: ${COLOR_8EC549};
    }

    #header .top-nav li:nth-child(2) a {
      color: ${COLOR_008000};
    }

    #navigation ul li a:hover,
    #navigation ul li.active a,
    #navigation ul ul,
    #navigation ul li.active ul li a:hover,
    #navigation ul li.active ul li a:hover span,
    #navigation ul li.active ul li.active a:hover,
    #navigation ul li.active ul li.active a:hover span {
        color: ${COLOR_4B8C3C};
    }

    .heading {
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
      background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
      color: ${COLOR_37672C};
    }

    .heading a {
      color: ${COLOR_37672C};
    }

    .heading a:hover, .heading a:focus {
      color: ${COLOR_37672C};
    }

    .submit2, .submit, .submit3, .submit4 {
      background-color: ${COLOR_3C9242};
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_3C9242}), color-stop(100%, ${COLOR_347F3A}));
      background-image: -webkit-linear-gradient(top, ${COLOR_3C9242} 0%, ${COLOR_347F3A} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_3C9242} 0%, ${COLOR_347F3A} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_3C9242} 0%, ${COLOR_347F3A} 100%);
      background-image: linear-gradient(top, ${COLOR_3C9242} 0%, ${COLOR_347F3A} 100%);
      -webkit-box-shadow: ${COLOR_A7CEAA} 0 1px 0 inset;
      -moz-box-shadow: ${COLOR_A7CEAA} 0 1px 0 inset;
      box-shadow: ${COLOR_A7CEAA} 0 1px 0 inset;
      border: 1px solid ${COLOR_35642B};
    }

    .submit2:hover, .submit2:focus, .submit3:hover, .submit3:focus, .submit4:hover, .submit4:focus {
      color: ${COLOR_8EC549};
    }

    .submit:hover, .submit:focus {
      color: ${COLOR_8EC549};
    }

    .list li a:hover {
      color: ${COLOR_8EC549};
    }

    .box-cards-title {
      background-color: ${COLOR_8DC449};
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
      background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
    }

    .box-cards-title span {
      color: ${COLOR_37672C};
    }

    .table-box th, .innerHeader th {
      background-color: ${COLOR_8DC449};
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
      background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
      color: ${COLOR_37672C};
    }

    .table-box th a, .innerHeader th a {
      color: ${COLOR_37672C};
    }

    .sub-box thead tr:before, .sub-box thead tr:after {
      background-color: ${COLOR_8DC449};
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
      background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
    }

    .table-box table thead tr:before, .table-box table thead tr:after {
      background-color: ${COLOR_8DC449};
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
      background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
    }

    .table-area table thead td {
      background-color: ${COLOR_8DC449};
      background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
      background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
      border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
      color: ${COLOR_37672C};
    }

    .ui-dialog-title {
        color: ${COLOR_37672C};
    }

    #impersonation-text {
        color: black;
    }

    .ui-widget-header {
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='${COLOR_7BAA3F}', endColorstr='${COLOR_8DC449}');
        background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, ${COLOR_CCE4AD}), color-stop(3%, ${COLOR_8DC449}), color-stop(100%, ${COLOR_7BAA3F}));
        background-image: -webkit-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
        background-image: -moz-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
        background-image: -o-linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
        background-image: linear-gradient(top, ${COLOR_CCE4AD} 0%, ${COLOR_8DC449} 3%, ${COLOR_7BAA3F} 100%);
        border-color: ${COLOR_4B8C3C} ${COLOR_4B8C3C} ${COLOR_37672C} ${COLOR_4B8C3C};
        color: ${COLOR_37672C};
    }

    .ui-state-hover,
    .ui-widget-content .ui-state-hover,
    .ui-widget-header .ui-state-hover,
    .ui-state-focus,
    .ui-widget-content .ui-state-focus,
    .ui-widget-header .ui-state-focus {
        border: 1px solid ${COLOR_8BC53D};
        background: ${COLOR_CCE4AD};
        font-weight: normal;
        color: #FFF;
    }

    .ui-datepicker-prev:hover, .ui-datepicker-next:hover {
        background: ${COLOR_3C9242} !important;
    }

    a.ui-datepicker-prev {
        border-right: 0px !important;
        box-shadow: inset -1px 0 0 grey !important;
    }

    a.ui-datepicker-next {
        border-left: 0px !important;
        box-shadow: inset 1px 0 0 grey !important;
    }

    .ui-tabs .ui-tabs-nav li.ui-state-hover a {
        color:${COLOR_3C9242};
    }

    .ui-datepicker-calendar .ui-state-default {
        color: ${COLOR_8BC53D};
    }

    .ui-datepicker-calendar .ui-state-default:hover {
        color: white;
    }

    .ui-datepicker-calendar .ui-state-active {
        color: black;
    }

    .pager-box a {
        color: ${COLOR_8BC53D};
    }
</style>