SCRIPT TKWAutotestManager_v117
# Generated automatically on: 20160404183408696
# Merged Files:
# /home/riro/TKW-5.0.5/TKW/contrib/TKWAutotestManager/tstp/WebServices/host/CO/DE_EMPPID.tstp

# NB this references the *internal* autotest simulator rules applied when listening for async messages not the rule set autotest applies which are referenced in the main properties file
SIMULATOR /home/riro/TKW-5.0.5/TKW/config/ITK_Correspondence/simulator_config/test_tks_rule_config.txt

VALIDATOR /home/riro/TKW-5.0.5/TKW/config/ITK_Correspondence/validator_config/validator.conf

STOP WHEN COMPLETE

INCLUDE src/test/resources/include.tst

BEGIN SCHEDULES
#DE_EMPPID TESTS DE_EMPPID_test1 DE_EMPPID_test2 DE_EMPPID_test3 DE_EMPPID_test4 DE_EMPPID_test5 DE_EMPPID_test6 DE_EMPPID_test7 DE_EMPPID_test8 DE_EMPPID_test9 demodelay
DE_EMPPID TESTS DE_EMPPID_test1 DE_EMPPID_test2 DE_EMPPID_test3 DE_EMPPID_test4 DE_EMPPID_test6 DE_EMPPID_test7 DE_EMPPID_test8 DE_EMPPID_test9 demodelay
END SCHEDULES

BEGIN TESTS
DE_EMPPID_test1 SEND_TKW DE_EMPPID TO http://127.0.0.1:4848/syncsoap FROM http://127.0.0.1:4000/syncsoap  PRETRANSFORM /home/riro/TKW-5.0.5/TKW/contrib/TKWAutotestManager/transforms/extract_de.xslt+/home/riro/TKW-5.0.5/TKW/contrib/TKWAutotestManager/transforms/DE_EMPPID.xslt APPLYPRETRANSFORMTO data+final SYNC fail WITH_PROPERTYSET base+webservices WITH_HTTPHEADERS headerset1 TEXT "DE_EMPPID_1: HTTP Response must be HTTP 500"
DE_EMPPID_test2 CHAIN SYNC errorcodeexists TEXT "DE_EMPPID_2: ErrorCode in ITK Fault Block must exist"
DE_EMPPID_test3 CHAIN SYNC error_1000 TEXT "DE_EMPPID_3: ErrorCode in ITK Fault Block must equal 1000"
DE_EMPPID_test4 CHAIN SYNC messageidexists TEXT "DE_EMPPID_4: The MessageID in the SOAP Header must exist"
#DE_EMPPID_test5 CHAIN SYNC syncmessageidsok TEXT "DE_EMPPID_5: The MessageID in the SOAP Header must not equal the MessageID in the input message"
DE_EMPPID_test6 CHAIN SYNC actionok TEXT "DE_EMPPID_6: The Action must equal http://www.w3.org/2005/08/addressing/soap/fault"
DE_EMPPID_test7 CHAIN SYNC faultcodeok TEXT "DE_EMPPID_7: The faultcode in the SOAP Body must equal Client"
DE_EMPPID_test8 CHAIN SYNC errortextcheck TEXT "DE_EMPPID_8: The ErrorText should indicate that &quot;Profile ID is missing&quot;"
DE_EMPPID_test9 CHAIN SYNC httpheaderchecknonzerocontent TEXT "DE_EMPPID_9: "
demodelay FUNCTION delay 5000 1000
END TESTS

BEGIN MESSAGES
DE_EMPPID USING DE_EMPPID_urn:nhs-itk:services:201005:SendCDADocument-v2-0_template WITH NULL SOAPWRAP SOAPACTION urn:nhs-itk:services:201005:SendCDADocument-v2-0
END MESSAGES

BEGIN TEMPLATES
DE_EMPPID_urn:nhs-itk:services:201005:SendCDADocument-v2-0_template  /home/riro/TKW-5.0.5/TKW/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml
END TEMPLATES

BEGIN PROPERTYSETS
breaksignature
	SET tks.signer.break.signaturevalue Y
breakusername
	SET tks.signer.break.usernametoken Y 
increasetimeout
	SET tks.autotest.synchronous.log.delay 30000
invaliduser
	SET tks.signer.keyalias tks_client
nosigning
	SET tks.transmitter.includesigner N
soapaction
	SET tks.transmitter.httpheader.SOAPaction urn:nhs-itk:services:201005:SendCDADocument-v2-0
webservices
	SET tks.HttpTransport.listenport 4000
	SET tks.HttpTransport.listenaddr  127.0.0.1
	SET a.b function:uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.hello 1 2
END PROPERTYSETS

BEGIN HTTPHEADERS
headerset1
	header1 "text1"
	header2 function:uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.hello 1 2
END HTTPHEADERS

BEGIN DATASOURCES
# points at a trashable copy
    patients circularwritabletdv src/test/resources/test.tdv
END DATASOURCES

BEGIN EXTRACTORS
    extractor_name xpathextractor src/test/resources/extract.cfg
END EXTRACTORS

BEGIN PASSFAIL
actionok synchronousxpath /soap:Envelope/soap:Header/wsa:Action matches "^http://www.w3.org.2005/08/addressing/soap/fault$"
error_1000 synchronousxpath /soap:Envelope/soap:Body/soap:Fault/detail/itk:ToolkitErrorInfo/itk:ErrorCode/text() matches "^1000$"
error_1000or3000 synchronousxpath /soap:Envelope/soap:Body/soap:Fault/detail/itk:ToolkitErrorInfo/itk:ErrorCode/text() matches "^(1000|3000)$"
error_2200 synchronousxpath /soap:Envelope/soap:Body/soap:Fault/detail/itk:ToolkitErrorInfo/itk:ErrorCode/text() matches "^(1000|2200)$"
error_3000 synchronousxpath /soap:Envelope/soap:Body/soap:Fault/detail/itk:ToolkitErrorInfo/itk:ErrorCode/text() matches "^3000$"
errorcodeexists synchronousxpath /soap:Envelope/soap:Body/soap:Fault/detail/itk:ToolkitErrorInfo/itk:ErrorCode exists
errortextcheck synchronousxpath /soap:Envelope/soap:Body/soap:Fault/detail/itk:ToolkitErrorInfo/itk:ErrorText check
fail http500
faultcodeok synchronousxpath /soap:Envelope/soap:Body/soap:Fault/faultcode matches "^(.+:)?Client$"
faultstringok synchronousxpath /soap:Envelope/soap:Body/soap:Fault/faultstring matches ".+"
messageidexists synchronousxpath /soap:Envelope/soap:Header/wsa:MessageID exists
#syncmessageidsok syncmessageidsdiffer
andcheck and ( httpok ) ( httpok )
failandcheck and ( httpok ) ( http500 )
orcheck or ( httpok ) ( http500 )
impliescheck implies ( httpok ) ( http500 )
notcheck not ( httpok )
matchescheck synchronousxpath /el/text() matches "^xxx$"
phmatchescheck synchronousxpath /hl7v2:MSH/hl7v2:MSH.1/text() matches "^|$"
httpheaderchecknonzerocontent httpheadercheck Content-Length doesnotmatch "^0$"
httpstatuscheck206 httpstatuscheck 206
nullrq nullrequest "^Failed.*$"
nullresp nullresponse "^Failed.*$"
httpheadercorrelation httpheadercorrelationcheck Header1 Header2
END PASSFAIL

BEGIN SUBSTITUTION_TAGS
__TAG_1__ Literal "a b c d"
__TAG_2__ function:uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.hello
END SUBSTITUTION_TAGS
