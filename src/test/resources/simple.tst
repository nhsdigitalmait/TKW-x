SCRIPT TKWAutotestManager_v123
# Generated automatically on: 20170306122719395
# Merged Files:
# /mnt/encrypted/home/simonfarrow/Documents/TKW5.0Dev/TKW/contrib/TKWAutotestManager/tstp/WebServices/host/CO/CO_AMB_PR_XML.tstp

# NB this references the *internal* autotest simulator rules applied when listening for async messages not the rule set autotest applies which are referenced in the main properties file
SIMULATOR /mnt/encrypted/home/simonfarrow/Documents/TKW5.0Dev/TKW/config/ITK_Correspondence/simulator_config/test_tks_rule_config.txt

VALIDATOR /mnt/encrypted/home/simonfarrow/Documents/TKW5.0Dev/TKW/config/ITK_Correspondence/validator_config/validator.conf

STOP WHEN COMPLETE

BEGIN SCHEDULES
CO_AMB_PR_XML TESTS CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test1 CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test2 CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test3 CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test4 CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test5 CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test6
END SCHEDULES

BEGIN TESTS
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test1 SEND_TKW CO_AMB_PR_XML TO http://127.0.0.1:4848/syncsoap FROM http://127.0.0.1:4000/syncsoap SYNC ok WITH_PROPERTYSET base+webservices TEXT "CO_AMB_PR_XML_1: HTTP Response must be HTTP 200"
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test2 CHAIN SYNC respexists TEXT "CO_AMB_PR_XML_2: &quot;SimpleMessageResponse&quot; must exist in the SOAP Body"
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test3 CHAIN SYNC respok TEXT "CO_AMB_PR_XML_3: &quot;SimpleMessageResponse&quot; should contain &quot;OK&quot;"
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test4 CHAIN SYNC CO_AMB_PR_XML___actioncorrect TEXT "CO_AMB_PR_XML_4: Action element must equal input Action suffixed with response"
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test5 CHAIN SYNC synctimestampok TEXT "CO_AMB_PR_XML_5: Created Timestamp must not be less than the Created Timestamp Element in the input message"
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_test6 CHAIN SYNC syncmessageidsok TEXT "CO_AMB_PR_XML_6: The MessageID in the SOAP Header must not equal the MessageID in the input message"
END TESTS

BEGIN MESSAGES
CO_AMB_PR_XML USING CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_template WITH NULL SOAPACTION urn:nhs-itk:services:201005:SendCDADocument-v2-0
END MESSAGES

BEGIN TEMPLATES
CO_AMB_PR_XML_urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0_template /mnt/encrypted/home/simonfarrow/Documents/TKW5.0Dev/TKW/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml
END TEMPLATES

BEGIN PROPERTYSETS
webservices
	SET tks.HttpTransport.listenport 4000
	SET tks.HttpTransport.listenaddr  127.0.0.1
END PROPERTYSETS

BEGIN DATASOURCES
END DATASOURCES

BEGIN PASSFAIL
CO_AMB_PR_XML___actioncorrect synchronousxpath /soap:Envelope/soap:Header/wsa:Action matches "^urn:nhs-itk:services:201005:SendCDADocument-v2-0Response$"
ok httpok
respexists synchronousxpath /soap:Envelope/soap:Body/itk:SimpleMessageResponse exists
respok synchronousxpath /soap:Envelope/soap:Body/itk:SimpleMessageResponse/text() matches "^(OK|TEST_HARNESS_ID.*)$"
#syncmessageidsok syncmessageidsdiffer
synctimestampok synctimestampislater
END PASSFAIL

