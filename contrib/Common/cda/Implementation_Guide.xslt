<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hl7="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!--PatientGPDetails				
<xsl:variable name="GPprefix" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']//*[local-name()='name']/*[local-name()='prefix']"/>
<xsl:variable name="GPgiven" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']//*[local-name()='name']/*[local-name()='given']"/>
<xsl:variable name="GPfamily" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']//*[local-name()='name']/*[local-name()='family']"/>
<xsl:variable name="GPAddress" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']//*[local-name()='addr']/*[local-name()='streetAddressLine']"/>
<xsl:variable name="GPPostcode" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']//*[local-name()='addr']/*[local-name()='postalCode']"/>
<xsl:variable name="GPTelecom" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']//*[local-name()='addr']/*[local-name()='telecom']/@value"/>
<xsl:variable name="GPCode" select="//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP']/../*[local-name()='associatedEntity']/*[local-name()='id'][@root='1.2.826.0.1285.0.2.0.65']/@extension"/>

-->
		<!--ReferrerDetails-->
		<!--
<xsl:variable name="Refprefix" select="//*[local-name()='participant'][@typeCode='REFB']//../*[local-name()='associatedEntity']//*[local-name()='name']/*[local-name()='prefix']"/>
<xsl:variable name="Refgiven" select="//*[local-name()='participant'][@typeCode='REFB']//../*[local-name()='associatedEntity']//*[local-name()='name']/*[local-name()='given']"/>
<xsl:variable name="Reffamily" select="//*[local-name()='participant'][@typeCode='REFB']//../*[local-name()='associatedEntity']//*[local-name()='name']/*[local-name()='family']"/>
<xsl:variable name="Reftelecom" select="//*[local-name()='participant'][@typeCode='REFB']//../*[local-name()='associatedEntity']//*[local-name()='telecom']/@value"/>
<xsl:variable name="RefAddress" select="//*[local-name()='participant'][@typeCode='REFB']//../*[local-name()='associatedEntity']//*[local-name()='addr']/*[local-name()='streetAddressLine']"/>
<xsl:variable name="RefPostcode" select="//*[local-name()='participant'][@typeCode='REFB']//../*[local-name()='associatedEntity']//*[local-name()='addr']/*[local-name()='postalCode']"/>
<xsl:variable name="RefType" select="//*[local-name()='participant'][@typeCode='REFB']//*//../*[local-name()='associatedEntity']//*[local-name()='code']/@displayName"/>
-->
		<!--Mandatory Headings-->
		<xsl:variable name="GPPractice" select="boolean(//*[local-name()='participant'][@typeCode='IND']/*[@code='PCP'])"/>
		<xsl:variable name="PatientDemographics" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP145201GB02#PatientRole'])"/>
		<xsl:variable name="DischargeDetails" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000036GB01#DischargeDetailsSection'])"/>
		<xsl:variable name="ClinicalSummary" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000033GB01#ClinicalSummarySection'])"/>
		<xsl:variable name="Diagnosis" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000037GB01#DiagnosesSection'])"/>
		<xsl:variable name="AllergiesandAdversereactions" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000035GB01#AllergiesAndAdverseReactionsSection'])"/>
		<xsl:variable name="Distributionlist" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP145203GB03#IntendedRecipient'])"/>
		<xsl:variable name="Personcompletingrecord" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000046GB01#PersonCompletingRecordSection'])"/>
		<table border="0">
			<td style="color:#008000">
			</td>
			<xsl:choose>
				<xsl:when test="$GPPractice=true() and $PatientDemographics=true() and  $DischargeDetails=true() and $ClinicalSummary=true() and $Diagnosis=true() and $AllergiesandAdversereactions=true() and $Distributionlist=true() and $Personcompletingrecord=true()">
					<tr>
						<td style="color:#008000">
<br/>								
								<xsl:text>ALL MANDATORY HEADINGS ARE PRESENT</xsl:text>
							<br/>
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<td style="color:#900000">
							<br/>
							<b>
								<xsl:text >MANDATORY HEADINGS ERROR</xsl:text>
							</b>
							<br/>
							<xsl:text>These headings must be completed and sent to the recipient.</xsl:text>
							<br/>
							<xsl:text>This would include NOT KNOWN or NOT ASKED</xsl:text>
						</td>
					</tr>
					<br/>
					<xsl:if test="$GPPractice=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'GP Practice' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$PatientDemographics=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'Patient demographics' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$DischargeDetails=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'Discharge details' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$ClinicalSummary=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'Clinical summary' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$Diagnosis=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'Diagnosis' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$AllergiesandAdversereactions=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'Allergies and adverse reactions' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$Distributionlist=false()">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: Mandatory heading 'Distribution list' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$Personcompletingrecord=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>ERROR: Mandatory heading 'Person completing record' is not present</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</table>
		<!--#REQUIRED HEADINGS-->
		<xsl:variable name="AdmissionDetailsSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000034GB01#AdmissionDetailsSection'])"/>
		<xsl:variable name="ProceduresSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000038GB01#ProceduresSection'])"/>
		<xsl:variable name="SpecialRequirementsSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000042GB01#SpecialRequirementsSection'])"/>
		<xsl:variable name="MedicationsAndMedicalDevicesSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000040GB01#MedicationsAndMedicalDevicesSection'])"/>
		<xsl:variable name="SafetyAlertsSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000039GB01#SafetyAlertsSection'])"/>
		<xsl:variable name="PlanAndRequestedActionsSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000014GB01#PlanAndRequestedActionsSection'])"/>
		<xsl:variable name="Referraldetails" select="boolean(//*[local-name()='participant'][@typeCode='REFB'])"/>
		<table border="0">
			<xsl:choose>
				<xsl:when test="$AdmissionDetailsSection=true() and $ProceduresSection=true() and  $SpecialRequirementsSection=true() and $MedicationsAndMedicalDevicesSection=true() and $SafetyAlertsSection=true() and $PlanAndRequestedActionsSection=true() and $Referraldetails=true()">
					<tr>
						<td style="color:#008000">
<br/>
								<xsl:text>ALL REQUIRED HEADINGS ARE PRESENT</xsl:text>
							<br/>
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<td style="color:#DF8719">
						<b>
							<br/>
							<xsl:text>REQUIRED HEADINGS OBSERVATION</xsl:text>
						</b>
						<br/>
<!--						<xsl:text>If there is information recorded it must be sent to the recipient</xsl:text>
						<br/>-->
					</td>
					<xsl:if test="$AdmissionDetailsSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Admission details' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$ProceduresSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Procedures' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$SpecialRequirementsSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Special requirements' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$MedicationsAndMedicalDevicesSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Medications and medical devices' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$SafetyAlertsSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Safety alerts' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$PlanAndRequestedActionsSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Plan and requested actions' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$Referraldetails=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Referral details' heading not found, if this information has been recorded it must be sent to the recipient</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</table>
		<xsl:variable name="AssessmentScalesSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000008GB01#AssessmentScalesSection'])"/>
		<xsl:variable name="SocialContextSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000043GB01#SocialContextSection'])"/>
		<xsl:variable name="ParticipationInResearchSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000001GB01#ParticipationInResearchSection'])"/>
		<xsl:variable name="InvestigationsAndProceduresRequestedSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000041GB01#InvestigationsAndProceduresRequestedSection'])"/>
		<xsl:variable name="PatientAndCarerConcernsSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000010GB01#PatientAndCarerConcernsSection'])"/>
		<xsl:variable name="LegalInformationSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000044GB01#LegalInformationSection'])"/>
		<xsl:variable name="InformationGivenSection" select="boolean(//*[local-name()='templateId'][@extension='COCD_TP000015GB01#InformationGivenSection'])"/>
		<table border="0">
			<xsl:choose>
				<xsl:when test="$AssessmentScalesSection=true() and $SocialContextSection=true() and  $ParticipationInResearchSection=true() and $InvestigationsAndProceduresRequestedSection=true() and $PatientAndCarerConcernsSection=true() and $LegalInformationSection=true() and $InformationGivenSection=true()">
					<tr>
						<td style="color:#008000">
										<br/>
							<xsl:text>ALL OPTIONAL HEADINGS ARE PRESENT</xsl:text>
							<br/>
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<td style="color:#DF8719">
						<b>
							<br/>
							<xsl:text>OPTIONAL HEADINGS OBSERVATION</xsl:text>
						</b>
						<br/>
<!--						<xsl:text></xsl:text>
						<br/>-->
					</td>
					<xsl:if test="$AssessmentScalesSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Assessment Scales' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$SocialContextSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Social Context' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$ParticipationInResearchSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text> 'Participation In Research' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$InvestigationsAndProceduresRequestedSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Investigations And Procedures Requested' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$PatientAndCarerConcernsSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Patient And Carer Concerns' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$LegalInformationSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Legal Information' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$InformationGivenSection=false()">
						<tr>
							<td style="color:#DF8719">
								<xsl:text>'Information Given' has NOT been included, ensure this omission is supported by a local decision</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</table>
		<!--		-->
		<!--****Option1****-->
		<!--
		<xsl:value-of select="$GPprefix"/>
		<xsl:value-of select="$GPgiven"/>
		<xsl:value-of select="$GPfamily"/>
		<xsl:value-of select="$GPAddress"/>
		<xsl:value-of select="$GPPostcode"/>
		<xsl:value-of select="$GPTelecom"/>
		<xsl:value-of select="$GPCode"/>
	
	
		<xsl:value-of select="$Refprefix"/>
		<xsl:value-of select="$Refgiven"/>
		<xsl:value-of select="$Reffamily"/>
		<xsl:value-of select="$RefAddress"/>
		<xsl:value-of select="$RefPostcode"/>
		<xsl:value-of select="$Reftelecom"/>
		<xsl:value-of select="$RefType"/>-->
	</xsl:template>
</xsl:stylesheet>
