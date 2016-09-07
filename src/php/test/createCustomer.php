<?php
require_once( "../src/JbillingAPIFactory.php" );
$api = jbillingAPIFactory::getAPI( 
  "http://localhost:8080/jbilling/services/api?wsdl",
	"admin;1", "123qwe", JBILLINGAPI_TYPE_CXF );

$UserWS = new UserWS();
$ContactWS = new ContactWS();
$CreditCardDTO = new CreditCardDTO();
$MetaField1 = new MetaFieldValueWS();

// USER CREATION PROCESS
// Define jBilling user properties
$UserWS->setId( 0 );
$UserWS->setUserName( "Php_Test_Customer1" );
$UserWS->setPassword( "secret123" );
$UserWS->setLanguageId( 1 ); // English
$UserWS->setMainRoleId( 5 ); // Customer
$UserWS->setRole( "Customer" );
$UserWS->setStatusId( 1 ); // Active
$UserWS->setSubscriberStatusId( 1 ); // Pre-paid
$UserWS->setAccountTypeId( 1 ); //Basic

// Define contact data for the user created
$ContactWS->setFirstName( "Test" );
$ContactWS->setLastName( "PhpCustomer" );
$ContactWS->setPhoneNumber( "123-456-7890" );
$ContactWS->setEmail( "test@test.com" );
$ContactWS->setAddress1( "123 Anywhere St" );
$ContactWS->setCity( "Some City" );
$ContactWS->setStateProvince( "Some State" );
$ContactWS->setPostalCode( "12345" );
$ContactWS->setFieldNames(array());

// Apply contact object to user contact property
$UserWS->setContact( $ContactWS );

// Define credit card data for the user being created
$CreditCardDTO->setName( "PHP Testing" );
$CreditCardDTO->setNumber( "4012888888881881" );
$CreditCardDTO->setSecurityCode( 123 );
$CreditCardDTO->setType( 2 ); // Visa
// Define date as ISO 8601 format
$CreditCardDTO->setExpiry( date("c", strtotime( "now" ) ) );

// Add the credit card to the user credit card property
$UserWS->setCreditCard( $CreditCardDTO );

// Define meta fields
$MetaField1->setFieldName( "contact.email" );
$MetaField1->setStringValue( "test@gmail.com" );
$MetaField1->setGroupId( 1 );
$MetaField1->setDisabled( false );
$MetaField1->setMandatory( false );

// Add meta fields to user
$UserWS->setMetaFields( array( $MetaField1) );


// CREATE THE USER 
try {
    print_r("Initializing customer creation..");
    $result = $api->createUser( $UserWS );
    print_r( $result ) ;
    print_r("Customer created.");
}
catch( JbillingAPIFactory $e){
    print_r( "Exception : "+$e );
}
?>
