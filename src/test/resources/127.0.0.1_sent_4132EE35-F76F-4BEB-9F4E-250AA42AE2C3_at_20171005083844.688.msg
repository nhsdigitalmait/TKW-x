POST /syncsoap HTTP/1.1
Content-Length: 4512
Server: HSCIC ITK Test Platform
SOAPaction: "urn:nhs-itk:services:201005:SendBusinessAck-v1-0"
Host: 127.0.0.1
Content-type: text/xml
Connection: close

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
	<soap:Header>
		<wsa:MessageID>4132EE35-F76F-4BEB-9F4E-250AA42AE2C3</wsa:MessageID>
		<wsa:Action>urn:nhs-itk:services:201005:SendBusinessAck-v1-0</wsa:Action>
		<wsa:To>http://127.0.0.1:4000/syncsoap</wsa:To>
		<wsse:Security>
			<wsu:Timestamp>
				<wsu:Created>2017-10-05T07:38:44Z</wsu:Created>
				<wsu:Expires>2017-10-05T07:48:44Z</wsu:Expires>			
			</wsu:Timestamp>
			<wsse:UsernameToken>
				<wsse:Username>Responder User</wsse:Username>
			</wsse:UsernameToken>
		</wsse:Security>
	</soap:Header>
	<soap:Body><itk:DistributionEnvelope xmlns:itk="urn:nhs-itk:ns:201005"><itk:header service="urn:nhs-itk:services:201005:SendBusinessAck-v1-0" trackingid="821F2F2C-512E-4641-BC04-A0EDA53F65F5"><itk:addresslist><itk:address type="2.16.840.1.113883.2.1.3.2.4.18.22" uri="urn:nhs-uk:addressing:ods:XZ901:XY7650987"/></itk:addresslist><itk:auditIdentity><itk:id type="2.16.840.1.113883.2.1.3.2.4.18.27" uri="urn:nhs-uk:identity:ods:test:cdaresponder"/></itk:auditIdentity><itk:manifest count="1"><itk:manifestitem mimetype="text/xml" profileid="urn:nhs-en:profile:ITKBusinessAcknowledgement-v2-0" base64="false" compressed="false" encrypted="false" id="uuid_BC8B2892-27DF-4C02-ABF8-029E50B662E7"/></itk:manifest><itk:senderAddress type="2.16.840.1.113883.2.1.3.2.4.18.22" uri="urn:nhs-uk:addressing:ods:test:cdaresponder"/><itk:handlingSpecification><itk:spec key="urn:nhs-itk:ns:201005:interaction" value="urn:nhs-itk:interaction:ITKBusinessAcknowledgement-v1-0"/></itk:handlingSpecification></itk:header><itk:payloads count="1"><itk:payload id="uuid_BC8B2892-27DF-4C02-ABF8-029E50B662E7"><hl7:BusinessResponseMessage xmlns:hl7="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<hl7:id root="96E2A935-43EE-40DC-A04C-C8DE26BD9A0A"/>
	<hl7:creationTime value="20171005083839"/>
	<hl7:interactionId root="2.16.840.1.113883.2.1.3.2.4.12" extension="urn:nhs-itk:interaction:primaryRecipientAmbulanceServicePatientReport-v1-0"/>
	<hl7:acknowledgedBy3 typeCode="AA">
		<hl7:conveyingTransmission>
			<hl7:id root="7D6F23E0-AE1A-11DB-8707-B18E1E0994EF"/>
		</hl7:conveyingTransmission>
	</hl7:acknowledgedBy3>
	<hl7:communicationFunction1 typeCode="SND">
		<hl7:entity classCode="ENT" determinerCode="INSTANCE">
			<hl7:id root="2.16.840.1.113883.2.1.3.2.4.18.22" extension="urn:nhs-uk:addressing:ods:XZ901:XY7650987"/>
		</hl7:entity>
	</hl7:communicationFunction1>
	<hl7:communicationFunction typeCode="RCV">
		<hl7:entity classCode="ENT" determinerCode="INSTANCE">
			<hl7:id root="2.16.840.1.113883.2.1.3.2.4.18.22" extension="urn:nhs-uk:addressing:ods:test:cdaresponder"/>
		</hl7:entity>
	</hl7:communicationFunction>
	<hl7:controlActEvent1 classCode="CACT" moodCode="EVN">
		<hl7:author typeCode="AUT" contextControlCode="OP">
			<hl7:COCD_TP145207GB01.AssignedAuthorDevice classCode="ASSIGNED">
				<hl7:id root="2.16.840.1.113883.2.1.3.2.4.18.24" extension="ITK-NICA-TKW"/>
				<hl7:templateId root="2.16.840.1.113883.2.1.3.2.4.18.2" extension="COCD_TP145207GB01#AssignedAuthorDevice"/>
				<hl7:assignedAuthoringDevice classCode="DEV" determinerCode="INSTANCE">
					<hl7:manufacturerModelName>ITK Accreditation NICA-TKW</hl7:manufacturerModelName>
					<hl7:softwareName>uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.RoutingActor</hl7:softwareName>
					<hl7:templateId root="2.16.840.1.113883.2.1.3.2.4.18.2" extension="COCD_TP145207GB01#assignedAuthoringDevice"/>
				</hl7:assignedAuthoringDevice>
				<hl7:representedOrganization classCode="ORG" determinerCode="INSTANCE">
					<hl7:id root="2.16.840.1.113883.2.1.3.2.4.19.1" extension="X09"/>
					<hl7:name>NICA-TKW</hl7:name>
					<hl7:templateId root="2.16.840.1.113883.2.1.3.2.4.18.2" extension="COCD_TP145207GB01#representedOrganization"/>
				</hl7:representedOrganization>
			</hl7:COCD_TP145207GB01.AssignedAuthorDevice>
		</hl7:author>
	</hl7:controlActEvent1>
	<hl7:acknowledgedBy2 typeCode="AA">
		<hl7:conveyingTransmission>
			<hl7:id root="uuid_D4059820-1ACB-400A-B61D-A0A900A3D11F"/>
		</hl7:conveyingTransmission>
	</hl7:acknowledgedBy2>
</hl7:BusinessResponseMessage>
</itk:payload></itk:payloads></itk:DistributionEnvelope></soap:Body>
</soap:Envelope>

**** END REQUEST ****
HTTP/1.1 200 OK
Content-Length: 719
Connection: close
Content-type: text/xml

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
	<soap:Header>
		<wsa:MessageID>37795756-A9A0-11E7-AD96-997BE3F57227</wsa:MessageID>
		<wsa:Action>urn:nhs-itk:services:201005:SendBusinessAck-v1-0Response</wsa:Action>
	</soap:Header>
	<soap:Body><itk:SimpleMessageResponse xmlns:itk="urn:nhs-itk:ns:201005">TEST_HARNESS_ID:821F2F2C-512E-4641-BC04-A0EDA53F65F5</itk:SimpleMessageResponse>
</soap:Body>
</soap:Envelope>
