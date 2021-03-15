<?xml version="1.0" encoding="UTF-8"?>
<!-- Validate FHIR Encounter resource details. Expandable for future Encounter validations-->
<!-- Currently this code ONLY validates the "additional population guidance" aka "business rules" for DCH Mesaging - Chris Brown July 2019-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:resource/fhir:Encounter">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS:  Encounter Id '</xsl:text>
						<xsl:value-of select="./fhir:id/@value"/>
						<xsl:text>'.</xsl:text>
					</td>
				</tr>
				
				<!-- When IDENTIFIER id Present SYSTEM and VALUE are Mandatory -->
				<xsl:for-each select="./fhir:identifier">
					<xsl:variable name="identifier_system" select="./fhir:system/@value"/>
					<xsl:variable name="identifier_value" select="./fhir:value/@value"/>
					<xsl:choose>
						<xsl:when test="count(./fhir:system/@value) &gt; 1">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one Identifier SYSTEM can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:system/@value)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:system[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="$identifier_system and (string-length($identifier_system) &gt; 0)">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Encounter Identifier System is mandatory '</xsl:text>
									<xsl:value-of select="$identifier_system"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:system"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  Encounter Identifier System is mandatory '</xsl:text>
									<xsl:value-of select="$identifier_system"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="count(./fhir:value/@value) &gt; 1">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one Identifier VALUE can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:value/@value)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:value[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="$identifier_value and (string-length($identifier_value) &gt; 0)">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Encounter Identifier Value is mandatory '</xsl:text>
									<xsl:value-of select="$identifier_value"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:value"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  Encounter Identifier Value is mandatory '</xsl:text>
									<xsl:value-of select="$identifier_value"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
					
					<!-- If Ref to Assigner (0..1) is present it must be to an Organization  -->
					<xsl:call-template name="Optional_Resource_0_or_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'Organization' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Assigner' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:assigner"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:assigner/fhir:reference/@value"/>
					</xsl:call-template>
				
				</xsl:for-each>
				
				<!-- check mandatory Encounter STATUS is present and valid-->
				<xsl:variable name="status" select="./fhir:status/@value"/>
				<xsl:choose>
					<xsl:when test="count(./fhir:status/@value) &gt; 1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  No more than one STATUS can be present. Here there are '</xsl:text>
								<xsl:value-of select="count(./fhir:status/@value)"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:status[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="$status=('planned', 'arrived', 'in-progress', 'onleave', 'finished', 'cancelled' )">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Mandatory Encounter status value is valid;  '</xsl:text>
								<xsl:value-of select="$status"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:status/@value"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Encounter status value is not valid;   '</xsl:text>
								<xsl:value-of select="$status"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:status/@value"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
				
				<!-- IF STATUSHISTORY is present then its elements STATUS and PERIOD are mandatory -->
				<!--<xsl:if test="./fhir:statusHistory">-->
				<xsl:for-each select="./fhir:statusHistory">
					<!-- validate STATUS -->
					<xsl:choose>
						<xsl:when test="count(./fhir:status/@value) &gt; 1">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one statusHistory STATUS can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:status/@value)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:status[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="not(./fhir:status/@value)">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  if statusHistory exists, status is mandatory;   '</xsl:text>
									<!-- <xsl:value-of select="$status"/> -->
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="string-length(./fhir:status/@value)=0">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  if statusHistory exists, status is mandatory and must be populated;   '</xsl:text>
									<!-- <xsl:value-of select="$status"/> -->
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:status/@value"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="./fhir:status/@value =('planned', 'arrived', 'in-progress', 'onleave', 'finished', 'cancelled')">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Encounter statusHistory status value is valid;  '</xsl:text>
									<xsl:value-of select="./fhir:status/@value"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:status/@value"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  Encounter statusHistory status value is not valid;   '</xsl:text>
									<xsl:value-of select="./fhir:status/@value"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:status/@value"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
					
					<!-- validate PERIOD -->
					<xsl:choose>
						<xsl:when test="count(./fhir:period) &gt; 1">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one statusHistory PERIOD can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:period)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:period[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="(not(./fhir:period/fhir:start/@value)) or (not(./fhir:period/fhir:end/@value))">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  if statusHistory exists, period start and end dates are madatory;   </xsl:text>
									<xsl:text> in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="(string-length(./fhir:period/fhir:start/@value)=0) or (string-length(./fhir:period/fhir:end/@value)=0)">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  if statusHistory exists, period start and end datesmust be populated;  '</xsl:text>
									<xsl:text> in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:period"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="((not ((./fhir:period/fhir:start/@value) castable as xs:dateTime)) and (not ((./fhir:period/fhir:start/@value) castable as xs:date)) and (not ((./fhir:period/fhir:start/@value) castable as xs:gYearMonth)) and (not ((./fhir:period/fhir:start/@value) castable as xs:gYear)))">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  statusHistory exists but period start date is invalid;  '</xsl:text>
									<xsl:text> in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:period"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="((not ((./fhir:period/fhir:end/@value) castable as xs:dateTime)) and (not ((./fhir:period/fhir:end/@value) castable as xs:date)) and (not ((./fhir:period/fhir:end/@value) castable as xs:gYearMonth)) and (not ((./fhir:period/fhir:end/@value) castable as xs:gYear)))">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  statusHistory exists but period end date is invalid;  '</xsl:text>
									<xsl:text> in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:period"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Encounter statusHistory period date values are valid;  '</xsl:text>
									<xsl:value-of select="./fhir:period"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:period"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				
				<!-- If Ref to Patient (0..1) is present it must be to an Patient  -->
					<xsl:call-template name="Optional_Resource_0_or_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'Patient' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Patient' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:patient"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:patient/fhir:reference/@value"/>
					</xsl:call-template>
			
								
				<!-- If Ref to EpisodeOfCare (0..*) is present it must be to an EpisodeOfCare  -->
				<xsl:for-each select="./fhir:episodeOfCare">
					<xsl:call-template name="Test_Resource_Type">
						<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
						<xsl:with-param name="Param_Valid_Resource" select=" 'EpisodeOfCare' "/>
					</xsl:call-template>
				</xsl:for-each>
				
				<!-- If Ref to IncomingReferral (0..*) is present it must be to an ReferralRequest  -->
				<xsl:for-each select="./fhir:incomingReferral">
					<xsl:call-template name="Test_Resource_Type">
						<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
						<xsl:with-param name="Param_Valid_Resource" select=" 'ReferralRequest' "/>
					</xsl:call-template>
				</xsl:for-each>
				
				<!-- For each Participant (0..*) and individual (0..1) may be present of types RelatedPerson Practitioner -->
				<xsl:for-each select="./fhir:participant">
					<xsl:call-template name="Optional_Resource_0_or_1">
							<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
							<xsl:with-param name="Param_Resource_Target" select=" 'RelatedPerson Practitioner' "/>
							<xsl:with-param name="Param_Field_Name" select=" 'Individual' "/>
							<xsl:with-param name="Param_Resource_Path" select="./fhir:individual"/>
							<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:individual/fhir:reference/@value"/>
						</xsl:call-template>
				</xsl:for-each>
				
				<!-- For each Appointment (0..1) present test for type  'Appointment' -->
				<xsl:call-template name="Optional_Resource_0_or_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Appointment' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'Appointment' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:appointment"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:appointment/fhir:reference/@value"/>
				</xsl:call-template>
		
				
				<!--    For each Indication (0..*) present test for type  'Condition Procedure'  -->
				<xsl:for-each select="./fhir:indication">
					<xsl:call-template name="Test_Resource_Type">
						<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
						<xsl:with-param name="Param_Valid_Resource" select=" 'Condition Procedure' "/>
					</xsl:call-template>
				</xsl:for-each>
				
				<!-- When HOSPITALIZATION PREADMISSIONIDENTIFIER is present then STATUS and VALUE are mandatory-->
				<xsl:for-each select="./fhir:hospitalization">
					<xsl:choose>
						<xsl:when test="count(./fhir:hospitalization) &gt; 1">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one HOSPITALISATION can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:hospitalization)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:hospitalization[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="./fhir:hospitalization">
							<xsl:for-each select="./fhir:preAdmissionIdentifier">
								<xsl:variable name="pia_system" select="./fhir:system/@value"/>
								<xsl:variable name="pia_value" select="./fhir:value/@value"/>
								<xsl:choose>
									<xsl:when test="count(./fhir:preAdmissionIdentifier) &gt; 1">
										<tr>
											<td style="color:#900000">
												<xsl:text>ERROR:  No more than one Hospitalization PREADMISSIONIDENTIFIER can be present. Here there are '</xsl:text>
												<xsl:value-of select="count(./fhir:preAdmissionIdentifier)"/>
												<xsl:text>' in xpath '</xsl:text>
												<xsl:call-template name="plotPath">
													<xsl:with-param name="forNode" select="./fhir:preAdmissionIdentifier[1]"/>
												</xsl:call-template>
												<xsl:text>'.</xsl:text>
											</td>
										</tr>
									</xsl:when>
									<xsl:when test="$pia_system and (string-length($pia_system) &gt; 0)">
										<tr>
											<td style="color:#008000">
												<xsl:text>PASS:  Encounter preAdmissionIdentifier System is mandatory '</xsl:text>
												<xsl:value-of select="$pia_system"/>
												<xsl:text>' in xpath '</xsl:text>
												<xsl:call-template name="plotPath">
													<xsl:with-param name="forNode" select="./fhir:hospitalization/fhir:preAdmissionIdentifier/fhir:system"/>
												</xsl:call-template>
												<xsl:text>'.</xsl:text>
											</td>
										</tr>
									</xsl:when>
									<xsl:otherwise>
										<tr>
											<td style="color:#900000">
												<xsl:text>ERROR:  Encounter preAdmissionIdentifier System is mandatory '</xsl:text>
												<xsl:value-of select="$pia_system"/>
												<xsl:text>' in xpath '</xsl:text>
												<xsl:call-template name="plotPath">
													<xsl:with-param name="forNode" select="./fhir:hospitalization/fhir:preAdmissionIdentifier"/>
												</xsl:call-template>
												<xsl:text>'.</xsl:text>
											</td>
										</tr>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:choose>
									<xsl:when test="$pia_value and (string-length($pia_value) &gt; 0)">
										<tr>
											<td style="color:#008000">
												<xsl:text>PASS:  Encounter preAdmissionIdentifier Value is mandatory '</xsl:text>
												<xsl:value-of select="$pia_value"/>
												<xsl:text>' in xpath '</xsl:text>
												<xsl:call-template name="plotPath">
													<xsl:with-param name="forNode" select="./fhir:hospitalization/fhir:preAdmissionIdentifier/fhir:value"/>
												</xsl:call-template>
												<xsl:text>'.</xsl:text>
											</td>
										</tr>
									</xsl:when>
									<xsl:otherwise>
										<tr>
											<td style="color:#900000">
												<xsl:text>ERROR:  Encounter preAdmissionIdentifier Value is mandatory '</xsl:text>
												<xsl:value-of select="$pia_value"/>
												<xsl:text>' in xpath '</xsl:text>
												<xsl:call-template name="plotPath">
													<xsl:with-param name="forNode" select="./fhir:hospitalization/fhir:preAdmissionIdentifier"/>
												</xsl:call-template>
												<xsl:text>'.</xsl:text>
											</td>
										</tr>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
				
				<xsl:for-each select="./fhir:hospitalization">
					<xsl:for-each select="./fhir:preAdmissionIdentifier">
						<!-- if a Hospitalisation / PreAdminionIdentifier / Assigner (0..1) is present it is of type 'Organization'  -->
						<xsl:call-template name="Optional_Resource_0_or_1">
							<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
							<xsl:with-param name="Param_Resource_Target" select=" 'Organization' "/>
							<xsl:with-param name="Param_Field_Name" select=" 'Assigner' "/>
							<xsl:with-param name="Param_Resource_Path" select="./fhir:assigner"/>
							<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:assigner/fhir:reference/@value"/>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:for-each>
				
				<xsl:for-each select="./fhir:hospitalization">
					<!-- if a Hospitalisation / Origin (0..1) is present it is of type 'Location'  -->
					<xsl:call-template name="Optional_Resource_0_or_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'Location' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Origin' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:origin"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:origin/fhir:reference/@value"/>
					</xsl:call-template>
					
					
					<xsl:for-each select="./fhir:admittingDiagnosis">
						<!-- if a Hospitalisation /AdmittingDiagnosis (0..*) is present it is of type 'Condition'  -->
						<xsl:call-template name="Test_Resource_Type">
							<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
							<xsl:with-param name="Param_Valid_Resource" select=" 'Condition' "/>
						</xsl:call-template>
					</xsl:for-each>
					
					<xsl:for-each select="./fhir:dischargeDiagnosis">
						<!-- if a Hospitalisation /DischargeDiagnosis (0..*) is present it is of type 'Condition'  -->
						<xsl:call-template name="Test_Resource_Type">
							<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
							<xsl:with-param name="Param_Valid_Resource" select=" 'Condition' "/>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:for-each>
				
				<!-- IF LOCATION (0..*) is present then its element Ref to LOCATION is mandatory (1..1) with a type of 'Location' -->
				<!-- Mandatory under DCH Business rules. Code moved down into DCH section -->
				<!--<xsl:for-each select="./fhir:location">
					<xsl:call-template name="Mandatory_Resource_1_to_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'Location' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Location' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:location"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:location/fhir:reference/@value"/>
					</xsl:call-template>
				</xsl:for-each>-->
				
			<!-- add DCH NEMS Stuff below here  Chris Brown 01Aug2019  -->
			<xsl:variable name="type_code" select="./fhir:type/fhir:coding/fhir:code/@value"/>
			
			<xsl:variable name="T_Code" select="document('./DCH-EncounterType-1-valueSet_DCH_1.0.0-Beta.xml')"/>
			<xsl:variable name="reffile_t_code" select="$T_Code//fhir:CodeSystem/fhir:concept/fhir:code[@value=$type_code]/@value"/>
			<xsl:variable name="reffile_t_display" select="$T_Code//fhir:CodeSystem/fhir:concept/fhir:code[@value=$type_code]/../fhir:display/@value"/>	
			<!--<xsl:value-of select="$reffile_t_code"/>
			<xsl:value-of select="$reffile_t_display"/>-->
			
			<!-- validate encounter.type.coding is present (1..1) and correct (Business Rule)   -->
				<xsl:choose>
					<xsl:when test="count(./fhir:type/fhir:coding) != 1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory Encounter Type Coding does not have cardinality of 1..1 in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="."/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="./fhir:type/fhir:coding/fhir:code/@value =$reffile_t_code and ./fhir:type/fhir:coding/fhir:display/@value=$reffile_t_display ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  MandatoryEncounter Type Coding Code and Display values are present and correct '</xsl:text>
								<xsl:value-of select="./fhir:type/fhir:coding/fhir:code/@value"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="./fhir:type/fhir:coding/fhir:display/@value"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:type[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory Encounter Type Coding is not correct '</xsl:text>
								<xsl:value-of select="./fhir:type/fhir:coding/fhir:code/@value"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="./fhir:type/fhir:coding/fhir:display/@value"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:type[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			
			<!-- validate encounter.reason.coding is present (1..1) and correct (Business Rule)   -->
			
			
			<!-- validate encounter.serviceProvider is present (1..1) and correct (Business Rule)   -->
				<xsl:call-template name="Mandatory_Resource_1_to_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Organization' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'Organization' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:serviceProvider"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:serviceProvider/fhir:reference/@value"/>
				</xsl:call-template>
			
			<!-- validate encounter.location is present (1..1) and correct (Business Rule)   -->
			<!-- Mandatory under DCH Business rules. Code moved down into DCH section -->
				<xsl:choose>
					<xsl:when test="count(./fhir:location) != 1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory Encounter Location does not have cardinality of 1..1 in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="."/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="Mandatory_Resource_1_to_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'Location' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Location' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:location/fhir:location"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:location/fhir:location/fhir:reference/@value"/>
					</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				
				
			<!-- element Ref to Practitioner is (1..1) (Business Rule) with a type of 'Practitioner' -->
				<xsl:call-template name="Mandatory_Resource_1_to_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'PractitionerRole' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Practitioner' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'practitioner' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:practitioner"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:practitioner/fhir:reference/@value"/>
				</xsl:call-template>
			
			<!-- validate encounter.subject is present (1..1) and correct (Business Rule)   -->
			<!-- element Ref to Subject is (1..1) (Business Rule) with a type of 'Patient' -->
				<xsl:call-template name="Mandatory_Resource_1_to_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'Encounter' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Patient' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'Patient' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:subject"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:subject/fhir:reference/@value"/>
				</xsl:call-template>
						
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:include href="FHIR_Generic_Reference_Check_v1.0.xslt"/>				
	<xsl:include href="FHIR_Generic_Get_Xpath_v1.0.xslt"/>
	<xsl:include href="FHIR_Generic_Resource_Type_Check_v1.0.xslt"/>
</xsl:stylesheet>
