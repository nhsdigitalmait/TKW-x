POST /reliablemessaging/queryrequest HTTP/1.1
Host: 10.97.89.163
SOAPAction: urn:nhs:names:services:pdsquery/QUPA_IN000006UK02
Content-Length: 6357
Content-Type: multipart/related; boundary="--=_MIME-Boundary"; type="text/xml"; start="<ebXMLHeader@spine.nhs.uk>"
Connection: close

----=_MIME-Boundary
Content-Id: <ebXMLHeader@spine.nhs.uk>
Content-Type: text/xml; charset=UTF-8
Content-Transfer-Encoding: 8bit

<?xml version="1.0" encoding="UTF-8"?>
<SOAP:Envelope xmlns:xsi="http://www.w3c.org/2001/XML-Schema-Instance" xmlns:SOAP="http://schemas.xmlsoap.org/soap/envelope/" xmlns:eb="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" xmlns:hl7ebxml="urn:hl7-org:transport/ebxml/DSTUv1.0" xmlns:xlink="http://www.w3.org/1999/xlink">
<SOAP:Header>
	<eb:MessageHeader SOAP:mustUnderstand="1" eb:version="2.0">
		<eb:From>
			<eb:PartyId eb:type="urn:nhs:names:partyType:ocs+serviceInstance">RHM-801710</eb:PartyId>
		</eb:From>
		<eb:To>
			<eb:PartyId eb:type="urn:nhs:names:partyType:ocs+serviceInstance">YEA-801248</eb:PartyId>
		</eb:To>
		<eb:CPAId>S2001924A2012004</eb:CPAId>
		<eb:ConversationId>7589C768-0DDF-11E5-82C2-4F9AE80A24B4</eb:ConversationId>
		<eb:Service>urn:nhs:names:services:pdsquery</eb:Service>
		<eb:Action>QUPA_IN000006UK02</eb:Action>
		<eb:MessageData>
			<eb:MessageId>7589C768-0DDF-11E5-82C2-4F9AE80A24B4</eb:MessageId>
			<eb:Timestamp>2015-06-08T14:08:30Z</eb:Timestamp>
		</eb:MessageData>
		
	</eb:MessageHeader>
	
	<eb:SyncReply SOAP:mustUnderstand="1" eb:version="2.0" SOAP:actor="http://schemas.xmlsoap.org/soap/actor/next"/>
</SOAP:Header>
<SOAP:Body>
	<eb:Manifest SOAP:mustUnderstand="1" eb:version="2.0">
		<eb:Reference xlink:href="cid:7589c769-0ddf-11e5-82c2-4f9ae80a24b4@spine.nhs.uk">
			<eb:Schema eb:location="http://www.nhsia.nhs.uk/schemas/HL7-Message.xsd" eb:version="1.0"/>
			<eb:Description xml:lang="en">HL7 payload</eb:Description> 
			<hl7ebxml:Payload style="HL7" encoding="XML" version="3.0"/>
		</eb:Reference>
		
	</eb:Manifest>
</SOAP:Body>
</SOAP:Envelope>

----=_MIME-Boundary
Content-Id: <7589c769-0ddf-11e5-82c2-4f9ae80a24b4@spine.nhs.uk>
Content-Type: application/xml; charset=UTF-8
Content-Transfer-Encoding: 8bit

<QUPA_IN000006UK02 xmlns="urn:hl7-org:v3">
    <id root="356A0E8B-F411-45B3-8D94-6F4D4F33091A" />
    <creationTime value="20150608140224" />
    <versionCode code="V3NPfIT3.0" />
    <interactionId root="2.16.840.1.113883.2.1.3.2.4.12" extension="QUPA_IN000006UK02" />
    <processingCode code="P" />
    <processingModeCode code="T" />
    <acceptAckCode code="NE" />
    <communicationFunctionRcv typeCode="RCV">
        <device classCode="DEV" determinerCode="INSTANCE">
            <!--Destination ASID derived from file C:/msgxmit1/yea_s2_int.xml for environment sp2.int-->
            <id root="1.2.826.0.1285.0.2.0.107" extension="908232220511" />
        </device>
    </communicationFunctionRcv>
    <communicationFunctionSnd typeCode="SND">
        <device classCode="DEV" determinerCode="INSTANCE">
            <!--Source ASID derived from file C:/msgxmit1/yea_s2_int.xml for environment sp2.int-->
            <id root="1.2.826.0.1285.0.2.0.107" extension="715373337545" />
        </device>
    </communicationFunctionSnd>
    <ControlActEvent classCode="CACT" moodCode="EVN">
        <author typeCode="AUT">
            <AgentPersonSDS classCode="AGNT">
                <id root="1.2.826.0.1285.0.2.0.67" extension="781733617547" />
                <agentPersonSDS classCode="PSN" determinerCode="INSTANCE">
                    <id root="1.2.826.0.1285.0.2.0.65" extension="102053919984" />
                </agentPersonSDS>
                <part typeCode="PART">
                    <partSDSRole classCode="ROL">
                        <id root="1.2.826.0.1285.0.2.1.104" extension="ABCD" />
                    </partSDSRole>
                </part>
            </AgentPersonSDS>
        </author>
        <author1 typeCode="AUT">
            <AgentSystemSDS classCode="AGNT">
                <agentSystemSDS classCode="DEV" determinerCode="INSTANCE">
                    <id root="1.2.826.0.1285.0.2.0.107" extension="715373337545" />
                </agentSystemSDS>
            </AgentSystemSDS>
        </author1>
        <query>
            <!-- mim 6,7 template -->
            <historicDataIndicator>
                <semanticsText>HistoricDataIndicator</semanticsText>
                <value code="1" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.36" />
            </historicDataIndicator>
            <person.address>
                <semanticsText>Person.address</semanticsText>
                <value>
                    <postalCode>DE22 3JD</postalCode>
                </value>
            </person.address>
            <person.administrativeGenderCode>
                <semanticsText>Person.administrativeGenderCode</semanticsText>
                <value code="1" />
            </person.administrativeGenderCode>
            <person.birthTime>
                <semanticsText>Person.birthTime</semanticsText>
                <!-- NB Example in mim has this totally wrong has value value="" not value center value="" -->
                <value>
                    <center value="19570108" />
                </value>
            </person.birthTime>
            <!-- Full date of death -->
            <!--
    <person.deceasedTime>
        <semanticsText>Person.deceasedTime</semanticsText>
        <value>
            <center value="20031202" />
        </value>
    </person.deceasedTime>
	-->
            <!-- NHS number -->
            <!--
    <person.id>
        <semanticsText>Person.id</semanticsText>
        <value root="2.16.840.1.113883.2.1.4.1" extension="9999999484" />
    </person.id>
	-->
            <person.name>
                <semanticsText>Person.name</semanticsText>
                <value use="L">
                    <family>O'LEARY</family>
                </value>
            </person.name>
            <!-- GP Practice Code -->
            <!--
	<primaryCare.id>
		<semanticsText>PrimaryCare.id</semanticsText>
		<value root="2.16.840.1.113883.2.1.4.2" extension="G1234567"/>
	</primaryCare.id>
	-->
            <!-- algorithmic (2) or alpha (1) -->
            <searchParameter>
                <semanticsText>SearchParameter</semanticsText>
                <value code="2" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.39" />
            </searchParameter>
        </query>
    </ControlActEvent>
</QUPA_IN000006UK02>
----=_MIME-Boundary--

HTTP/1.1 202 Accepted
Server: nginx/1.6.2
Date: Mon, 08 Jun 2015 13:08:31 GMT
Content-Type: text/xml
Content-Length: 0
Connection: close
SOAPAction: asyncExpressSyncAck





TIME Connection received: 1433768910.708864
TIME To proxy read start: 1433768910.951486
TIME To sender write start: 1433768911.102841
TIME To sender write end: 1433768911.102959
TIME To proxy TLS close start: 1433768911.102961
TIME To proxy TLS close complete: 1433768911.102961
TIME From proxy connection start: 1433768910.951845
TIME From proxy connection complete: 1433768910.969427
TIME From proxy TLS start: 1433768910.969442
TIME From proxy TLS complete: 1433768911.023218
TIME From proxy read start: 1433768911.102716
TIME From receiver write start: 1433768911.023303
TIME From receiver write end: 1433768911.023875
TIME From receiver TLS close start: 1433768911.102962
TIME From receiver TLS close end: 1433768911.103071
