<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/10/xpath-functions" xmlns:xdt="http://www.w3.org/2004/10/xpath-datatypes" xmlns:hl7="urn:hl7-org:v3" xmlns:npfitlc="NPFIT:HL7:Localisation">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:variable name="test_data" select="document(/files/file[1]/@href, /)"/>
	<xsl:variable name="supp_data" select="document(/files/file[2]/@href, /)"/>

	<xsl:template match="/">

<!--BASIC_CHECKS-->
<xsl:choose>
	<xsl:when test="count($test_data/*[local-name()='File']/*[local-name()='record']) = count($supp_data/*[local-name()='File']/*[local-name()='record'])">
		<xsl:text>Record Count Match: </xsl:text>
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>Record Count Mismatch: </xsl:text>
	</xsl:otherwise>
</xsl:choose>

<xsl:text>TestData: </xsl:text>
<xsl:value-of select="count($test_data/*[local-name()='File']/*[local-name()='record'])"/>
<xsl:text> Evidence: </xsl:text>
<xsl:value-of select="count($supp_data/*[local-name()='File']/*[local-name()='record'])"/>

		<xsl:for-each select="$test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']">
			<xsl:variable name="TD.PayloadNum" select= "position()" />
			<xsl:variable name="TD.cPPStartDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component']/*[local-name()='cPPStartDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.cPPEndDatePath" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component1']/*[local-name()='cPPEndDate']"></xsl:variable>			
			<xsl:variable name="TD.cPPEndDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component1']/*[local-name()='cPPEndDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.cPPDeleteDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component2']/*[local-name()='cPPDeleteDate']/*[local-name()='value']/@value"/>
     		<xsl:variable name="TD.lACStartDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component']/*[local-name()='lACStartDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.lACEndDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component1']/*[local-name()='lACEndDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.lACDeleteDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component1']/*[local-name()='lACDeleteDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.uCPStartDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component']/*[local-name()='uCPStartDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.uCPEndDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component1']/*[local-name()='uCPEndDate']/*[local-name()='value']/@value"/>
			<xsl:variable name="TD.uCPDeleteDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component2']/*[local-name()='uCPDeleteDate']/*[local-name()='value']/@value"/>
     		<xsl:variable name="TD.Child.NHSNo" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='id'][1]/@extension"/>
     		<xsl:variable name="TD.Child.LocalID" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='id'][2]/@extension"/>     		
     		<xsl:variable name="TD.Child.birthTime" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='childPerson']/*[local-name()='birthTime']/@value"/>
			<xsl:variable name="TD.Child.given" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='childPerson']/*[local-name()='name']/*[local-name()='given']"/>
			<xsl:variable name="TD.Child.family" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='childPerson']//*[local-name()='name']/*[local-name()='family']"/>
   			<xsl:variable name="TD.Mother.NHSNo" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][1]/@extension"/>
			<xsl:variable name="TD.Mother.LocalID" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][2]/@extension"/>
			<xsl:variable name="TD.LA.ODS" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][2]/@assigningAuthorityName"/>
			<xsl:variable name="TD.Mother.birthTime" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='birthTime']/@value"/>					
			<xsl:variable name="TD.Mother.given" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='name']/*[local-name()='given']"/>
			<xsl:variable name="TD.Mother.family" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='name']/*[local-name()='family']"/>
			
			
			<xsl:for-each select="$supp_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']">
				<xsl:variable name="SD.PayloadNum" select= "position()" />	
				<xsl:variable name="SD.cPPStartDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component']/*[local-name()='cPPStartDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.cPPEndDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component']/*[local-name()='cPPEndDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.cPPDeleteDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component']/*[local-name()='cPPDeleteDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.lACStartDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component']/*[local-name()='lACStartDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.lACEndDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component']/*[local-name()='lACEndDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.lACDeleteDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component']/*[local-name()='lACDeleteDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.uCPStartDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component']/*[local-name()='uCPStartDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.uCPEndDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component1']/*[local-name()='uCPEndDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.uCPDeleteDate" select="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component2']/*[local-name()='uCPDeleteDate']/*[local-name()='value']/@value"/>
				<xsl:variable name="SD.Child.NHSNo" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='id'][1]/@extension"/>
				<xsl:variable name="SD.Child.LocalID" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='id'][2]/@extension"/>    				
				<xsl:variable name="SD.Child.birthTime" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='childPerson']/*[local-name()='birthTime']/@value"/>
				<xsl:variable name="SD.Child.given" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='childPerson']/*[local-name()='name']/*[local-name()='given']"/>
				<xsl:variable name="SD.Child.family" select="./*[local-name()='subject']/*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='childPerson']//*[local-name()='name']/*[local-name()='family']"/>
				<xsl:variable name="SD.Mother.NHSNo" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][1]/@extension"/>
				<xsl:variable name="SD.Mother.LocalID" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][2]/@extension"/>
				<xsl:variable name="SD.LA.ODS" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][2]/@assigningAuthorityName"/>
				<xsl:variable name="SD.Mother.birthTime" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='birthTime']/@value"/>					
				<xsl:variable name="SD.Mother.given" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='name']/*[local-name()='given']"/>
				<xsl:variable name="SD.Mother.family" select="./*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='name']/*[local-name()='family']"/>

<!--CPPSTARTSHERECPPSTARTSHERECPPSTARTSHERECPPSTARTSHERECPPSTARTSHERECPPSTARTSHERECPPSTARTSHERECPPSTARTSHERE-->				
<xsl:choose>
	<xsl:when test="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']">

<xsl:choose>	
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component']/*[local-name()='cPPStartDate'])">
	<xsl:text>Missing Mandatory cPPStartDate</xsl:text>
	</xsl:when>											
	<xsl:otherwise>
	<xsl:if test="$SD.cPPStartDate = $TD.cPPStartDate">
	<xsl:text>
CCP Matches,</xsl:text>
			<xsl:value-of select="$TD.PayloadNum"/>
			<xsl:text>,</xsl:text>
			<xsl:value-of select="$SD.PayloadNum"/>
			<xsl:text>,</xsl:text>
			<xsl:value-of select="$SD.cPPStartDate"/>
			<xsl:text>,</xsl:text>
	</xsl:if>
	</xsl:otherwise>
</xsl:choose>
		
<xsl:choose>											
	<xsl:when test="not($TD.cPPEndDate)">
<!--			<xsl:text>,</xsl:text>-->
</xsl:when>
<xsl:otherwise>
	<xsl:if test="$SD.cPPEndDate = $TD.cPPEndDate">		
		<xsl:value-of select="$SD.cPPEndDate"/>
		<xsl:text>,</xsl:text>
	</xsl:if>	
</xsl:otherwise>
</xsl:choose>
		
<xsl:choose>											
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146101GB01.CPPInformation']/*[local-name()='component2']/*[local-name()='cPPDeleteDate'])">
<!--				<xsl:text>,</xsl:text>-->
		</xsl:when>
		<xsl:otherwise>
		<xsl:if test="$SD.cPPDeleteDate = $TD.cPPDeleteDate">		
		<xsl:value-of select="$SD.cPPDeleteDate"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
		</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.NHSNo)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
	<xsl:if test="$TD.Child.NHSNo = $SD.Child.NHSNo">		
		<xsl:value-of select="$SD.Child.NHSNo"/>
	<xsl:text>,</xsl:text>
	</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.LocalID)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
	<xsl:if test="$TD.Child.LocalID = $SD.Child.LocalID">		
		<xsl:value-of select="$SD.Child.LocalID"/>
	<xsl:text>,</xsl:text>
	</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.birthTime)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
	<xsl:if test="$TD.Child.birthTime = $SD.Child.birthTime">		
		<xsl:value-of select="$SD.Child.birthTime"/>
	<xsl:text>,</xsl:text>
	</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.given)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
<xsl:otherwise>
	<xsl:if test="$TD.Child.given = $SD.Child.given">		
		<xsl:value-of select="$SD.Child.given"/>
		<xsl:text>,</xsl:text>
	</xsl:if>	
</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.family)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Child.family = $SD.Child.family">		
		<xsl:value-of select="$SD.Child.family"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>	
</xsl:when>				    
<!--LACSTARTSHERELACSTARTSHERELACSTARTSHERELACSTARTSHERELACSTARTSHERELACSTARTSHERELACSTARTSHERELACSTARTSHERE-->
				<xsl:otherwise>
						<xsl:choose>	
							<xsl:when test="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']">
<xsl:choose>
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component']/*[local-name()='lACStartDate'])">
<!--		<xsl:text> no </xsl:text>-->
	</xsl:when>											
	<xsl:otherwise>
				<xsl:if test="$SD.lACStartDate = $TD.lACStartDate">
					<xsl:text>
					LAC Matches,</xsl:text>
					<xsl:value-of select="$TD.PayloadNum"/>
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$SD.PayloadNum"/>
					<xsl:text>,</xsl:text>
					<xsl:value-of select="$SD.lACStartDate"/>
					<xsl:text>,</xsl:text>
				</xsl:if>
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>											
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component1']/*[local-name()='lACEndDate'])">
<!--		<xsl:text> no </xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$SD.lACEndDate = $TD.lACEndDate">		
			<xsl:value-of select="$SD.lACEndDate"/>
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146100GB01.LACInformation']/*[local-name()='component2']/*[local-name()='lACDeleteDate'])">
<!--			<xsl:text> no </xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
	<xsl:text> yes </xsl:text>
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.NHSNo)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
	<xsl:if test="$TD.Child.NHSNo = $SD.Child.NHSNo">		
		<xsl:value-of select="$SD.Child.NHSNo"/>
		<xsl:text>,</xsl:text>
	</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.LocalID)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
	<xsl:if test="$TD.Child.LocalID = $SD.Child.LocalID">		
		<xsl:value-of select="$SD.Child.LocalID"/>
		<xsl:text>,</xsl:text>
	</xsl:if>	
	</xsl:otherwise>
</xsl:choose>								

<xsl:choose>
		<xsl:when test="not($TD.Child.birthTime)">
		<!--			<xsl:text>,</xsl:text>-->
		</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Child.birthTime = $SD.Child.birthTime">		
			<xsl:value-of select="$SD.Child.birthTime"/>
			<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.given)">
	<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
<xsl:otherwise>
	<xsl:if test="$TD.Child.given = $SD.Child.given">		
		<xsl:value-of select="$SD.Child.given"/>
		<xsl:text>,</xsl:text>
	</xsl:if>	
</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Child.family)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Child.family = $SD.Child.family">		
		<xsl:value-of select="$SD.Child.family"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
	</xsl:choose>	
	</xsl:when>
<xsl:otherwise>

<!--UCPSTARTSHEREUCPSTARTSHEREUCPSTARTSHEREUCPSTARTSHEREUCPSTARTSHEREUCPSTARTSHEREUCPSTARTSHEREUCPSTARTSHERE-->
<xsl:choose>	
	<xsl:when test="./*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']">

<xsl:choose>
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component']/*[local-name()='uCPStartDate'])">
	<!--<xsl:text> no </xsl:text>-->
	</xsl:when>											
	<xsl:otherwise>
		<xsl:if test="$SD.uCPStartDate = $TD.uCPStartDate">
		<xsl:text>
UCP Matches,</xsl:text>
		<xsl:value-of select="$TD.PayloadNum"/>
		<xsl:text>,</xsl:text>
		<xsl:value-of select="$SD.PayloadNum"/>
		<xsl:text>,</xsl:text>
		<xsl:value-of select="$SD.uCPStartDate"/>
		<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:otherwise>
</xsl:choose>
		
<xsl:choose>											
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component1']/*[local-name()='uCPEndDate'])">
	<!--<xsl:text> no </xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$SD.uCPEndDate = $TD.uCPEndDate">		
		<xsl:value-of select="$SD.uCPEndDate"/>
		<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:otherwise>
</xsl:choose>
		
<xsl:choose>
	<xsl:when test="not($test_data/*[local-name()='File']/*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='pertinentInformation']/*[local-name()='COCT_TP146102GB01.UCPInformation']/*[local-name()='component2']/*[local-name()='uCPDeleteDate'])">
	<!--<xsl:text> no </xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$SD.uCPDeleteDate = $TD.uCPDeleteDate">		
		<xsl:value-of select="$SD.uCPDeleteDate"/>
		<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Mother.NHSNo)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Mother.NHSNo = $SD.Mother.NHSNo">		
		<xsl:value-of select="$SD.Mother.NHSNo"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Mother.LocalID)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Mother.LocalID = $SD.Mother.LocalID">		
		<xsl:value-of select="$SD.Mother.LocalID"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.LA.ODS)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.LA.ODS = $SD.LA.ODS">		
<!--		SAME FOR ALL!!!!-->
<!--		<xsl:value-of select="$SD.LA.ODS"/>-->
<!--		<xsl:text>,</xsl:text>-->
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Mother.birthTime)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Mother.birthTime = $SD.Mother.birthTime">		
		<xsl:value-of select="$SD.Mother.birthTime"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Mother.given)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Mother.given = $SD.Mother.given">		
		<xsl:value-of select="$SD.Mother.given"/>
		<xsl:text>,</xsl:text>
		</xsl:if>	
	</xsl:otherwise>
</xsl:choose>

<xsl:choose>
	<xsl:when test="not($TD.Mother.family)">
<!--			<xsl:text>,</xsl:text>-->
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="$TD.Mother.family = $SD.Mother.family">		
		<xsl:value-of select="$SD.Mother.family"/>
		<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:otherwise>
</xsl:choose>

									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>		
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
