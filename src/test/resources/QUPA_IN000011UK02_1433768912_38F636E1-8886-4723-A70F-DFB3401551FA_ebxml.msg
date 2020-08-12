POST /proxy HTTP/1.1
User-Agent: Mozilla/5.0 (compatible; pycurl)
Host: msgxmit-y90240.cfh-nic.nhs.uk
Accept-Encoding: none
SOAPAction: urn:nhs:names:services:pdsquery/QUPA_IN000011UK02
Accept: */*
Connection: close
Content-Type: multipart/related; boundary="--=_MIME-Boundary"; charset="UTF-8"; type="text/xml"; start="<ebXMLHeader@spine.nhs.uk>"
Content-Length: 28299

----=_MIME-Boundary
Content-Id: <ebXMLHeader@spine.nhs.uk>
Content-Type: text/xml
Content-Transfer-Encoding: 8bit

<?xml version='1.0' encoding='UTF-8'?>
<soap:Envelope xmlns:xsi="http://www.w3c.org/2001/XML-Schema-Instance" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:eb="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" xmlns:xlink="http://www.w3.org/1999/xlink"><soap:Header><eb:MessageHeader eb:version="2.0" soap:mustUnderstand="1"><eb:From><eb:PartyId eb:type="urn:nhs:names:partyType:ocs+serviceInstance">YEA-801248</eb:PartyId></eb:From><eb:To><eb:PartyId eb:type="urn:nhs:names:partyType:ocs+serviceInstance">RHM-801710</eb:PartyId></eb:To><eb:CPAId>S2030777A2138431</eb:CPAId><eb:ConversationId>7589C768-0DDF-11E5-82C2-4F9AE80A24B4</eb:ConversationId><eb:Service>urn:nhs:names:services:pdsquery</eb:Service><eb:Action>QUPA_IN000011UK02</eb:Action><eb:MessageData><eb:MessageId>38F636E1-8886-4723-A70F-DFB3401551FA</eb:MessageId><eb:Timestamp>2015-06-08T14:08:30Z</eb:Timestamp><eb:RefToMessageId>7589C768-0DDF-11E5-82C2-4F9AE80A24B4</eb:RefToMessageId></eb:MessageData></eb:MessageHeader><eb:SyncReply eb:version="2.0" soap:mustUnderstand="1" soap:actor="http://schemas.xmlsoap.org/soap/actor/next"/></soap:Header><soap:Body><eb:Manifest xmlns:hl7ebxml="urn:hl7-org:transport/ebXML/DSTUv1.0" eb:version="2.0"><eb:Reference xlink:href="cid:38F636E1-8886-4723-A70F-DFB3401551FA@spine.nhs.uk"><eb:Schema eb:location="urn:hl7-org:v3_QUPA_IN000011UK02.xsd" eb:version="02"/><eb:Description xml:lang="en">The HL7 Payload</eb:Description><hl7ebxml:Payload style="HL7" encoding="XML" version="3.0"/></eb:Reference></eb:Manifest></soap:Body></soap:Envelope>

----=_MIME-Boundary
Content-Id: <38F636E1-8886-4723-A70F-DFB3401551FA@spine.nhs.uk>
Content-Type: application/xml
Content-Transfer-Encoding: 8bit

<?xml version='1.0' encoding='UTF-8'?>
<QUPA_IN000011UK02 xmlns="urn:hl7-org:v3"><id root="38F636E1-8886-4723-A70F-DFB3401551FA"/><creationTime value="20150608130831"/><versionCode code="3NPfIT6.3.01"/><interactionId root="2.16.840.1.113883.2.1.3.2.4.12" extension="QUPA_IN000011UK02"/><processingCode code="P"/><processingModeCode code="T"/><acceptAckCode code="NE"/><acknowledgement typeCode="AA"><messageRef><id root="356A0E8B-F411-45B3-8D94-6F4D4F33091A"/></messageRef></acknowledgement><communicationFunctionRcv typeCode="RCV"><device classCode="DEV" determinerCode="INSTANCE"><id root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/></device></communicationFunctionRcv><communicationFunctionSnd typeCode="SND"><device classCode="DEV" determinerCode="INSTANCE"><id root="1.2.826.0.1285.0.2.0.107" extension="908232220511"/></device></communicationFunctionSnd><ControlActEvent classCode="CACT" moodCode="EVN"><author1 typeCode="AUT"><AgentSystemSDS classCode="AGNT"><agentSystemSDS classCode="DEV" determinerCode="INSTANCE"><id root="1.2.826.0.1285.0.2.0.107" extension="908232220511"/></agentSystemSDS></AgentSystemSDS></author1><reason typeCode="RSON"><justifyingDetectedIssueEvent classCode="ALRT" moodCode="EVN"><code code="3" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.42" displayName="Match - NHS Number and standard Service User details returned"><qualifier code="WG"/></code></justifyingDetectedIssueEvent></reason><subject typeCode="SUBJ"><PdsTraceMatch xmlns:hl7="urn:hl7-org:v3" classCode="OBS" moodCode="EVN">
	<subject typeCode="SBJ">
		<patientRole classCode="PAT">
			<id root="2.16.840.1.113883.2.1.4.1" extension="9401170622"/>
			<patientPerson classCode="PSN" determinerCode="INSTANCE">
				<administrativeGenderCode code="1"/>
				<birthTime value="19570108"/>
				
				
				<playedOtherProviderPatient classCode="PAT">
					<subjectOf typeCode="SBJ">
						<patientCareProvision classCode="PCPR" moodCode="EVN">
							<code codeSystem="2.16.840.1.113883.2.1.3.2.4.17.37" code="1"/>
							<effectiveTime>
								<low value="20120301"/>
							</effectiveTime>
							<performer typeCode="PRF">
								<assignedEntity classCode="ASSIGNED">
									<id root="2.16.840.1.113883.2.1.4.3" extension="P81014"/>
								</assignedEntity>
							</performer>
							<subjectOf1 typeCode="SUBJ">
                                <sourceSourceIdentified classCode="OBS" moodCode="EVN">
                                    <code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
                                    <value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
                                </sourceSourceIdentified>
                            </subjectOf1>
                            <subjectOf2 typeCode="SUBJ">
                                <systemEffectiveDate classCode="OBS" moodCode="EVN">
                                    <code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
                                    <value>
                                        <low value="20121115121619"/>
                                    </value>
                                </systemEffectiveDate>
                            </subjectOf2>
						</patientCareProvision>
					</subjectOf>
				</playedOtherProviderPatient>
                <playedOtherProviderPatient classCode="PAT">
                    <subjectOf typeCode="SBJ">
                        <patientCareProvision classCode="PCPR" moodCode="EVN">
                            <code codeSystem="2.16.840.1.113883.2.1.3.2.4.17.37" code="1"/>
                            <effectiveTime>
                                <low value="19930904"/>
                                <high value="19981129"/>
                            </effectiveTime>
                            <performer typeCode="PRF">
                                <assignedEntity classCode="ASSIGNED">
                                    <id root="2.16.840.1.113883.2.1.4.3" extension="C81014"/>
                                </assignedEntity>
                            </performer>
                            <subjectOf1 typeCode="SUBJ">
		                        <sourceSourceIdentified classCode="OBS" moodCode="EVN">
		                            <code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
		                            <value root="1.2.826.0.1285.0.2.0.107" extension="111111111111"/>
		                        </sourceSourceIdentified>
		                    </subjectOf1>
		                    <subjectOf2 typeCode="SUBJ">
		                        <systemEffectiveDate classCode="OBS" moodCode="EVN">
		                            <code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
		                            <value>
		                                <low value="20080115225227"/>
		                                <high value="20081105015938"/>
		                            </value>
		                        </systemEffectiveDate>
		                    </subjectOf2>
                        </patientCareProvision>
                    </subjectOf>
                </playedOtherProviderPatient>
                <playedOtherProviderPatient classCode="PAT">
                    <subjectOf typeCode="SBJ">
                        <patientCareProvision classCode="PCPR" moodCode="EVN">
                            <code codeSystem="2.16.840.1.113883.2.1.3.2.4.17.37" code="1"/>
                            <effectiveTime>
                                <low value="20070809"/>
                                <high value="20120301"/>
                            </effectiveTime>
                            <performer typeCode="PRF">
                                <assignedEntity classCode="ASSIGNED">
                                    <id root="2.16.840.1.113883.2.1.4.3" extension="G84001"/>
                                </assignedEntity>
                            </performer>
                            <subjectOf1 typeCode="SUBJ">
		                        <sourceSourceIdentified classCode="OBS" moodCode="EVN">
		                            <code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
		                            <value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
		                        </sourceSourceIdentified>
		                    </subjectOf1>
		                    <subjectOf2 typeCode="SUBJ">
		                        <systemEffectiveDate classCode="OBS" moodCode="EVN">
		                            <code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
		                            <value>
		                                <low value="20120221161100"/>
		                                <high value="20121115121619"/>
		                            </value>
		                        </systemEffectiveDate>
		                    </subjectOf2>
                        </patientCareProvision>
                    </subjectOf>
                </playedOtherProviderPatient>
				<COCT_MT000201UK02.PartOfWhole classCode="PART">
					<addr use="H">
						<streetAddressLine>10 RADBOURNE</streetAddressLine>
						<streetAddressLine>DOCK STREET</streetAddressLine>
						<streetAddressLine/>
						<streetAddressLine/>
						<streetAddressLine>DERBYSHIRE</streetAddressLine>
						<postalCode>DE22 3HB</postalCode>
                        <addressKey>12345678</addressKey>
                        
                          <useablePeriod>
                          <low value="20100501"/>
                           </useablePeriod>
						
					</addr>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20121115104605"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000201UK02.PartOfWhole>	
				<COCT_MT000201UK02.PartOfWhole classCode="PART">
					<addr use="H">
						<streetAddressLine/>
						<streetAddressLine>188 UTTOXETER NEW ROAD</streetAddressLine>
						<streetAddressLine/>
						<streetAddressLine>DERBY</streetAddressLine>
						<streetAddressLine/>
						<postalCode>DE22 3JD</postalCode>
                        
                        
                          <useablePeriod>
                          <low value="20071207"/>
                           <high value="20071207"/>
                           </useablePeriod>
						
					</addr>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="111111111111"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20080118052749"/>
								<high value="20091015112249"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000201UK02.PartOfWhole>	
				<COCT_MT000201UK02.PartOfWhole classCode="PART">
					<addr use="H">
						<streetAddressLine/>
						<streetAddressLine>186 UTTOXETER NEW ROAD</streetAddressLine>
						<streetAddressLine/>
						<streetAddressLine>DERBY</streetAddressLine>
						<streetAddressLine/>
						<postalCode>DE22 3JD</postalCode>
                        
                        
                          <useablePeriod>
                          <low value="20071207"/>
                           <high value="20100501"/>
                           </useablePeriod>
						
					</addr>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="555712270035"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20091015112249"/>
								<high value="20121115090511"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000201UK02.PartOfWhole>	
				<COCT_MT000201UK02.PartOfWhole classCode="PART">
					<addr use="H">
						<streetAddressLine>LIVER BUILDING</streetAddressLine>
						<streetAddressLine>DOCK STREET</streetAddressLine>
						<streetAddressLine>DOCKSIDE</streetAddressLine>
						<streetAddressLine>LIVERPOOL</streetAddressLine>
						<streetAddressLine>MERSEYSIDE</streetAddressLine>
						<postalCode>L1 4AZ</postalCode>
                        <addressKey>12345678</addressKey>
                        
                          <useablePeriod>
                          <low value="20100501"/>
                           <high value="20100501"/>
                           </useablePeriod>
						
					</addr>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20121115090511"/>
								<high value="20121115104605"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000201UK02.PartOfWhole>	
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HV" value="mailto:MATT.WHITTAKER@TESTDATA.NHS.UK">
                                <useablePeriod>
                                    <low value="20061025"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="069050241830"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20080213041456"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="H" value="tel:01133333333">
                                <useablePeriod>
                                    <low value="20090910"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="046841411039"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20090910130702"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:22222222222">
  
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20110916093351"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:01132222222">
  
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="555712270035"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20090910125755"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:33333333333">
                                <useablePeriod>
                                    <low value="20200101"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20110916093351"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:01131112222">
                                <useablePeriod>
                                    <high value="20090910"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="555712270035"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20090910111201"/>
								<high value="20090910125755"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:01131111111">
                                <useablePeriod>
                                    <high value="20110916"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="555712270035"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20090910125755"/>
								<high value="20110916090346"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:11111111111">
                                <useablePeriod>
                                    <high value="20110916"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20110916090346"/>
								<high value="20110916093351"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000202UK02.PartOfWhole classCode="PART">
					<telecom use="HP" value="tel:01133334444">
                                <useablePeriod>
                                    <high value="20090910"/>
                                </useablePeriod>
                              
					
					</telecom>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="555712270035"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20090910111557"/>
								<high value="20090910125755"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000202UK02.PartOfWhole>
				<COCT_MT000203UK02.PartOfWhole classCode="PART">
					<partPerson classCode="PSN" determinerCode="INSTANCE">
						<name use="L">		
							<family>O'LEARY</family>
                            <given>PHILIP</given>
                            <prefix>MR</prefix>
                                    <validTime>
                                        <low value="20121109"/>
                                    </validTime>
                      
							
						</name>
					</partPerson>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20121113124736"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000203UK02.PartOfWhole>
				<COCT_MT000203UK02.PartOfWhole classCode="PART">
					<partPerson classCode="PSN" determinerCode="INSTANCE">
						<name use="L">		
							<family>WHITTAKER</family>
                            <given>MATT</given>
                            <prefix>MR</prefix>
                                    <validTime>
                                        <low value="19760129"/>
                                        <high value="20091211"/>
                                    </validTime>
                      
							
						</name>
					</partPerson>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="111111111111"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20080115225227"/>
								<high value="20091211150620"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000203UK02.PartOfWhole>
				<COCT_MT000203UK02.PartOfWhole classCode="PART">
					<partPerson classCode="PSN" determinerCode="INSTANCE">
						<name use="L">		
							<family>O'LEARY</family>
                            <given>MATT</given>
                            <prefix>MR</prefix>
                                    <validTime>
                                        <low value="20091211"/>
                                        <high value="20121109"/>
                                    </validTime>
                      
							
						</name>
					</partPerson>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="046841411039"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20091211150620"/>
								<high value="20121109175124"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000203UK02.PartOfWhole>
				<COCT_MT000203UK02.PartOfWhole classCode="PART">
					<partPerson classCode="PSN" determinerCode="INSTANCE">
						<name use="L">		
							<family>O'LEARY</family>
                            <given>GEORGE</given>
                            <prefix>MR</prefix>
                                    <validTime>
                                        <low value="20121109"/>
                                        <high value="20121109"/>
                                    </validTime>
                      
							
						</name>
					</partPerson>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20121109175124"/>
								<high value="20121113084046"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000203UK02.PartOfWhole>
				<COCT_MT000203UK02.PartOfWhole classCode="PART">
					<partPerson classCode="PSN" determinerCode="INSTANCE">
						<name use="L">		
							<family>O'LEARY</family>
                            <given>JOHN</given>
                            <prefix>MR</prefix>
                                    <validTime>
                                        <low value="20121109"/>
                                        <high value="20121109"/>
                                    </validTime>
                      
							
						</name>
					</partPerson>
					<subjectOf1 typeCode="SBJ">
						<sourceSourceIdentified classCode="OBS" moodCode="EVN">
							<code code="01" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value root="1.2.826.0.1285.0.2.0.107" extension="715373337545"/>
						</sourceSourceIdentified>
					</subjectOf1>
					<subjectOf2 typeCode="SBJ">
						<systemEffectiveDate classCode="OBS" moodCode="EVN">
							<code code="02" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.170"/>
							<value>
								<low value="20121113084046"/>
								<high value="20121113124736"/>
							</value>
						</systemEffectiveDate>
					</subjectOf2>
				</COCT_MT000203UK02.PartOfWhole>
			</patientPerson>
			<subjectOf1 typeCode="SBJ">
				<matchingLevel classCode="OBS" moodCode="EVN">
					<code code="12" codeSystem="2.16.840.1.113883.2.1.3.2.4.17.35"/>
					<value unit="%" value="100.00"/>
				</matchingLevel>
			</subjectOf1>
				
		</patientRole>
	</subject>
</PdsTraceMatch></subject><queryAck type="QueryAck"><queryResponseCode code="OK"/></queryAck></ControlActEvent></QUPA_IN000011UK02>
----=_MIME-Boundary--



HTTP/1.1 200 OK
Date: Mon, 08 Jun 2015 13:08:32 GMT
Server: Spine Proxy v2.3e
Content-Length: 1486
Connection: close
Content-Type: text/xml; charset=utf-8
Content-Language: en-GB

<?xml version="1.0" encoding="UTF-8"?>
<soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:eb="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" xsi:schemaLocation="http://schemas.xmlsoap.org/soap/envelope/ http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd">
<soap-env:Header><eb:MessageHeader soap-env:mustUnderstand="1" eb:version="2.0">
<eb:From><eb:PartyId eb:type="urn:nhs:names:partyType:ocs+serviceInstance">RHM-801710</eb:PartyId></eb:From><eb:To><eb:PartyId eb:type="urn:nhs:names:partyType:ocs+serviceInstance">YEA-801248</eb:PartyId></eb:To>
<eb:CPAId>oasis0001</eb:CPAId>
<eb:ConversationId>7589C768-0DDF-11E5-82C2-4F9AE80A24B4</eb:ConversationId>
<eb:Service>urn:oasis:names:tc:ebxml-msg:service</eb:Service>
<eb:Action>Acknowledgment</eb:Action>
<eb:MessageData>
<eb:MessageId>0A4F2FC8-806A-4EE9-A23F-B6900A7756F2</eb:MessageId>
<eb:Timestamp>2015-06-08T13:08:32Z</eb:Timestamp>
<eb:RefToMessageId>38F636E1-8886-4723-A70F-DFB3401551FA</eb:RefToMessageId>
</eb:MessageData>
</eb:MessageHeader>
<eb:Acknowledgment  soap-env:mustUnderstand="1" eb:version="2.0"  soap-env:actor="urn:oasis:names:tc:ebxml-msg:actor:nextMSH">
<eb:Timestamp>2015-06-08T13:08:32Z</eb:Timestamp>
<eb:RefToMessageId>38F636E1-8886-4723-A70F-DFB3401551FA</eb:RefToMessageId>
</eb:Acknowledgment>
</soap-env:Header>
<soap-env:Body/>
</soap-env:Envelope>




TIME Connection received: 1433768911.270619
TIME To proxy TLS start: 1433768911.270642
TIME To proxy TLS complete: 1433768911.300352
TIME To proxy read start: 1433768912.103475
TIME To proxy read end: 1433768912.105665
TIME To sender write start: 1433768912.105769
TIME To sender write end: 1433768912.106096
TIME To proxy TLS close start: 1433768912.106097
TIME To proxy TLS close complete: 1433768912.106114
TIME From receiver TLS close start: 1433768912.106115
TIME From receiver TLS close end: 1433768912.106116
