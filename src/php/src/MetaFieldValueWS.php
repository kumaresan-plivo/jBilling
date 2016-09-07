<?php
/**
 * jbilling-php-api
 * Copyright (C) 2007-2009  Make A Byte, inc
 * http://www.makeabyte.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @package com.makeabyte.contrib.jbilling.php
 */

/**
  * MetaFieldValueWS
  * @author Shweta Gupta
  * @version 1.0
  * @copyright Make A Byte, inc
  * @package com.makeabyte.contrib.jbilling.php
  */

class MetaFieldValueWS {

      var $fieldName;                         
      var $groupId;                                            
      var $mandatory;                             
      var $disabled;      
      var $dataType;
      var $defaultValue;
      var $displayOrder;
      var $id;
      var $stringValue;
      var $dateValue;
      var $booleanValue;
      var $decimalValue;
      var $integerValue;
      var $listValue;

      /**
       * The MetaFieldValueWS constructor
       * 
       * @access public
       */
      public function MetaFieldValueWS() {}

      public function setFieldName( $fieldName ) {
	     $this->fieldName = $fieldName;
      }

      public function getFieldName( ) {
      	 return $this->fieldName;
      }

      public function setGroupId( $groupId ) {
	     $this->groupId = $groupId;
      }

      public function getGroupId( ) {
     	 return $this->groupId;
      }

      public function setDisabled( $disabled ) {
	     $this->disabled = $disabled;
      }

      public function getDisabled( ) {
       	 return $this->disabled;
      }

      public function setMandatory( $mandatory ) {
         $this->mandatory = $mandatory;
      }

      public function getMandatory( ) {
      	 return $this->mandatory;
      }
 
      public function setDataType( $dataType ) {
	     $this->dataType = $dataType;
      }

      public function getDataType( ) {
      	 return $this->dataType;
      }

      public function setDefaultValue( $defaultValue ) {
         $this->defaultValue = $defaultValue;
      }

      public function getDefaultValue( ) {
      	 return $this->defaultValue;
      }

      public function setDisplayOrder( $displayOrder ) {
	     $this->displayOrder = $displayOrder;
      }
 
      public function getDisplayOrder( ) {
      	 return $this->displayOrder;
      }

      public function setId( $id ) {
	     $this->id = $id;
      }

      public function getId( ) {
       	 return $this->id;
      }

      public function setStringValue( $value ) {
	     $this->stringValue = $value;
      }

      public function getStringValue( ) {
         return $this->stringValue;
      }

      public function setDateValue( $value ) {
      	 $this->dateValue = $value;
      }

      public function getDateValue( ) {
      	 return $this->dateValue;
      }

      public function setBooleanValue( $value ) {
      	 $this->booleanValue = $value;
      }

      public function getBooleanValue( ) {
      	 return $this->booleanValue;
      }

      public function setDecimalValue( $value ) {
         $this->decimalValue = $value;
      }

      public function getDecimalValue( ) {
     	 return $this->decimalValue;
      }

      public function setIntegerValue( $value ) {
         $this->integerValue = $value;
      }

      public function getIntegerValue( ) {
     	 return $this->integerValue;
      }

      public function setListValue( $value ) {
         $this->listValue = $value;
      }

      public function getListValue( ) {
         return $this->listValue;
      }
}
?>
