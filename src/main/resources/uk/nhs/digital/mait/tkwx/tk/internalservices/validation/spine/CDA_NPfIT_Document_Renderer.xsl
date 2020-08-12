<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="urn:hl7-org:v3" xmlns:npfitlc="NPFIT:HL7:Localisation" xmlns:n2="urn:hl7-org:v3/meta/voc" xmlns:voc="urn:hl7-org:v3/voc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="n1 n2 voc npfitlc xsi">
	<xsl:output method="html" indent="yes" version="4.01" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
	<!-- CDA document -->
	<xsl:variable name="title">
		<xsl:choose>
			<xsl:when test="/n1:ClinicalDocument/n1:title">
				<xsl:value-of select="/n1:ClinicalDocument/n1:title"/>
			</xsl:when>
			<xsl:otherwise>Clinical Document</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:param name="debug" select="'no'"/>
	<xsl:param name="p_doc" select="n1:ClinicalDocument"/>
	<xsl:template match="/">
		<xsl:apply-templates select="n1:ClinicalDocument"/>
	</xsl:template>
	<xsl:template match="n1:ClinicalDocument">
		<html>
			<head>
				<xsl:comment>$transformVersion=1.2 $updateDate=21/11/2012 Interoperability Team</xsl:comment>	
				<!-- <meta name='Generator' content='&CDA-Stylesheet;'/> -->
				<xsl:comment> Do NOT edit this HTML directly, it was generated via an XSLT transformation from the original release 2 CDA
          Document. </xsl:comment>
				<title>
					<xsl:value-of select="$title"/>
				</title>
				<style type="text/css">
						body { color: #000000; font-size: 10pt; line-height: normal; font-family: Verdana, Arial, sans-serif; margin: 10px; }
						a { color: #0099ff; text-decoration: none }
						.input { color: #003366; font-size: 10pt; font-family: Verdana, Arial, sans-serif; background-color: #ffffff; border: solid 1px }
						div.titlebar { background-color: #eeeeff; border: 1px solid #000000; padding: 3px; margin-bottom: 20px;}
						div.doctitle { font-size: 14pt; font-weight: bold; margin-bottom: 10px;}
						div.header { font-size: 8pt; margin-bottom: 30px; border: 1px solid #000000; background-color: #ffffee;}
						div.textsection { font-size: 8pt; margin-bottom: 2px; border: 1px solid #000000; background-color:  #ffffff;}
						div.footer { font-size: 8pt; margin-top: 30px; border: 1px solid #000000; background-color: #ffffee;}
						p {margin-top: 2px; margin-bottom: 6px;}
						h1 { font-size: 14pt; font-weight: bold; color: #000000; margin-top: 15px; margin-bottom: 20px;}
						h2 { font-size: 12pt; font-weight: bold; color: #000000; margin-top: 10px; margin-bottom: 15px; }
						h3 { font-size: 10pt; font-weight: bold; color: #000000; margin-top: 8px; margin-bottom: 10px; }
						h4 { font-size: 10pt; font-weight: bold; text-decoration: underline; color: #000000; margin-top: 6px; margin-bottom: 6px; }
						h5 { font-size: 10pt; font-weight: normal; text-decoration: underline;  color: #000000; margin-top: 5px; margin-bottom: 4px; }
						h6 { font-size: 10pt; font-weight: normal; color: #000000; margin-top: 2px; margin-bottom: 2px; }
						table { border: 1px solid #000000; }
						th.default {padding: 3px; color: #000000; background-color: #dddddd; text-align: left;}
						th {padding: 3px; color: #000000; background-color: #dddddd;}
						td {padding: 3px; background-color: #eeeeee;}
						table.titlebar { border: 0px; background-color: #eeeeff; }
						td.titlebar {color: #000000; background-color: #eeeeff; font-weight: bold; }
						th.titlebar {color: #000000; background-color: #eeeeff; font-weight: normal; font-style: italic; text-align: left;}
						th.sectitle {color: #000000; background-color: #ffffee; font-weight: bold; text-align: left;}
						th.participant {color: #000000; background-color: #ffffee; font-weight: bold; font-style: italic; text-align: left;}
						table.header { border: 0px; background-color: #ffffee; }
						table.textsection { border: 0px; background-color: #ffffff; }
						td.header {color: #000000; background-color: #ffffee; font-weight: bold;}
						th.header {color: #000000; background-color: #ffffee; font-weight: normal; text-align: left; font-style:italic;}
						td.textsection {color: #000000; background-color: #ffffff; font-weight: bold; font-size: 10pt;}
						th.textsection {color: #000000; background-color: #ffffff; font-weight: normal; text-align: left; font-style:italic; font-size: 10pt;}						
						/*Classes below map to CDA styleCodes*/
						.Bold {font-weight: bold;}
						.Underline {text-decoration:underline;}
						.Italics {font-style:italic;}
						.Emphasis {font-style:italic;}
						.Rrule {border-right: 1px solid black;}
						.Lrule {border-left: 1px solid black;}
						.Toprule {border-top: 1px solid black;}
						.Botrule {border-right: 1px solid black;}
						/*Banner styles*/
						div.banner { font-size: 8pt; margin-bottom: 30px; border: 1px solid #000000; background-color: #ffffee;}
						div.banner TABLE { border: 0px; background-color: #ffffee; font-weight: bold; }
						div.banner TD { background-color: #ffffee; vertical-align: top; padding-right: 1em;}
						div.banner TABLE P {margin: 0;}
						.label {font-style:italic; font-weight: normal;}
				</style>
			</head>
			<xsl:comment>Derived from HL7 Finland R2 Tyylitiedosto: Tyyli_R2_B3_01.xslt</xsl:comment>
			<xsl:comment>Updated by Calvin E. Beebe, Mayo Clinic - Rochester Mn. </xsl:comment>
			<xsl:comment>Updated by Keith W. Boone, Dictaphone - Burlington, MA </xsl:comment>
			<xsl:comment>Updated by Kai U. Heitmann, Heitmann Consulting &amp; Service, NL for VHitG, Germany </xsl:comment>
			<xsl:comment>Updated by Rene Spronk, translate back to english-language labels</xsl:comment>
			<xsl:comment>Updated by Dick Donker, Philips Medical Systems include linkHtml</xsl:comment>
			<xsl:comment>Updated by Tim Pilkington - NHS CFH</xsl:comment>
			<xsl:comment>Updated by Aled Greenhalgh - NHS CFH</xsl:comment>
			<xsl:comment>Updated by Prashant Trivedi - NHS CFH</xsl:comment>
			<body>
				<div style="color:red; font-weight:bold;">***This HTML is created using transform which is provided on as-is basis and content of this HTML is not clinically validated. The transform used to create this HTML can be modified as per local needs.***</div>
				<xsl:call-template name="patientBanner"/>
				<xsl:if test="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:relatedPerson/n1:name">
					<xsl:call-template name="informantBanner"/>
					<!--PRTR1 This banner should appears if informant is present-->
				</xsl:if>
				<xsl:call-template name="header"/>
				<xsl:call-template name="titlebar"/>
				<xsl:if test="/n1:ClinicalDocument/n1:participant">
					<xsl:call-template name="participantBanner"/>
					<!--PRTR1 This banner should appears if participant is present-->
				</xsl:if>
				<xsl:apply-templates select="n1:component/n1:structuredBody|n1:component/n1:nonXMLBody"/>
				<xsl:call-template name="footer"/>
			</body>
		</html>
	</xsl:template>
	<!-- Get a Name  -->
	<xsl:template name="getName">
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$name/n1:family">
				<xsl:if test="$name/n1:prefix">
					<xsl:value-of select="$name/n1:prefix"/>
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:value-of select="$name/n1:given"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="$name/n1:family"/>
				<xsl:if test="$name/n1:suffix">
					<xsl:text> </xsl:text>
					<xsl:value-of select="$name/n1:suffix"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Format name as per CUI guidance  -->
	<xsl:template name="cuiName">
		<xsl:param name="name"/>
		<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'"/>
		<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
		<xsl:choose>
			<xsl:when test="$name/n1:family">
				<xsl:value-of select="translate($name/n1:family, $smallcase, $uppercase)"/>
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$name/n1:given"/>
				<xsl:if test="$name/n1:suffix">
					<xsl:text> </xsl:text>
					<xsl:value-of select="$name/n1:suffix"/>
				</xsl:if>
				<xsl:if test="$name/n1:prefix">
					<xsl:text> </xsl:text>(<xsl:value-of select="$name/n1:prefix"/>)
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Format NHS number as per CUI guidance: ### ### #### -->
	<xsl:template name="cuiNHSNo">
		<xsl:param name="nhsNo"/>
		<xsl:value-of select="substring($nhsNo, 1, 3)"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="substring($nhsNo, 4, 3)"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="substring($nhsNo, 7)"/>
	</xsl:template>
	<!--  Format Date 
    outputs a date in Month Day, Year form
    -->
	<xsl:template name="formatDate">
		<xsl:param name="date"/>
		<xsl:variable name="month" select="substring ($date, 5, 2)"/>
		<xsl:value-of select="substring ($date, 7, 2)"/>
		<xsl:if test="substring ($date, 7, 2)">
			<xsl:text>-</xsl:text>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$month='01'">
				<xsl:text>Jan</xsl:text>
			</xsl:when>
			<xsl:when test="$month='02'">
				<xsl:text>Feb</xsl:text>
			</xsl:when>
			<xsl:when test="$month='03'">
				<xsl:text>Mar</xsl:text>
			</xsl:when>
			<xsl:when test="$month='04'">
				<xsl:text>Apr </xsl:text>
			</xsl:when>
			<xsl:when test="$month='05'">
				<xsl:text>May</xsl:text>
			</xsl:when>
			<xsl:when test="$month='06'">
				<xsl:text>Jun</xsl:text>
			</xsl:when>
			<xsl:when test="$month='07'">
				<xsl:text>Jul</xsl:text>
			</xsl:when>
			<xsl:when test="$month='08'">
				<xsl:text>Aug</xsl:text>
			</xsl:when>
			<xsl:when test="$month='09'">
				<xsl:text>Sep</xsl:text>
			</xsl:when>
			<xsl:when test="$month='10'">
				<xsl:text>Oct</xsl:text>
			</xsl:when>
			<xsl:when test="$month='11'">
				<xsl:text>Nov</xsl:text>
			</xsl:when>
			<xsl:when test="$month='12'">
				<xsl:text>Dec</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text>-</xsl:text>
		<xsl:value-of select="substring ($date, 1, 4)"/>
		<xsl:if test="string-length($date)>8">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="substring ($date, 9, 2)"/>
			<xsl:text>:</xsl:text>
			<xsl:value-of select="substring ($date, 11, 2)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="n1:component/n1:nonXMLBody">
		<xsl:choose>
			<!-- if there is a reference, use that in an IFRAME -->
			<xsl:when test="n1:text/n1:reference">
				<IFRAME name="nonXMLBody" id="nonXMLBody" WIDTH="100%" HEIGHT="66%" src="{n1:text/n1:reference/@value}"/>
			</xsl:when>
			<xsl:when test="n1:text/@mediaType='text/plain'">
				<pre>
					<xsl:value-of select="n1:text/text()"/>
				</pre>
			</xsl:when>
			<xsl:otherwise>
				<CENTER>Cannot display the text</CENTER>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- StructuredBody -->
	<xsl:template match="n1:component/n1:structuredBody">
		<xsl:apply-templates select="n1:component/n1:section"/>
		<xsl:if test="$debug='yes'">
			<hr/>
			<h1>Coded Entries</h1>
			<xsl:for-each select="//n1:entry">
				<xsl:call-template name="CodedEntryMatch">
					<xsl:with-param name="temp_1" select="."/>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="//n1:entryRelationship">				
				<xsl:call-template name="CodedEntryMatch">
					<xsl:with-param name="temp_1" select="."/>
				</xsl:call-template>			
			</xsl:for-each>
			<xsl:for-each select="//n1:component">				
				<xsl:call-template name="CodedEntryMatch">
					<xsl:with-param name="temp_1" select="."/>
				</xsl:call-template>			
			</xsl:for-each>			
			<!--<xsl:apply-templates select="n1:component/n1:section/n1:entry"/>
			<xsl:apply-templates select="n1:component/n1:section/n1:entry/n1:observation/n1:entryRelationship"/>-->
		</xsl:if>
	</xsl:template>
	
	<!-- Component/Section -->
	<xsl:template match="n1:component/n1:section">
		<xsl:if test="n1:author != ''">
				<hr/>
				<xsl:apply-templates select="n1:author"/>				
		</xsl:if>
		<xsl:if test="n1:title != ''">
			<xsl:apply-templates select="n1:title"/>
		</xsl:if>
		<xsl:if test="n1:text != ''">		
			<xsl:apply-templates select="n1:text"/>
		</xsl:if>
		<xsl:apply-templates select="n1:component/n1:section"/>
	</xsl:template>
	
	
	<!--<xsl:template match="n1:entry"> --> 
	<xsl:template name="CodedEntryMatch">
		<xsl:param name="temp_1"/>
		<xsl:variable name="v_codedentry" select="$temp_1/child::node()[child::n1:templateId[contains(@extension,'146')]]"/>
		<xsl:if test="$v_codedentry/n1:code | $v_codedentry/n1:value ">
		<p>
			<xsl:value-of select="substring-after($v_codedentry/n1:templateId/@extension,'#')"/> - <xsl:value-of select="substring-before($v_codedentry/n1:templateId/@extension,'#')"/>
		</p>
		<p>Code : <xsl:value-of select="$v_codedentry/n1:code/@code"/>
		</p>
		<p>Displayname : <xsl:value-of select="$v_codedentry/n1:code/@displayName"/>
		</p>
		<xsl:if test="$v_codedentry/n1:value/@code">
		<p>Value Code : <xsl:value-of select="$v_codedentry/n1:value/@code"/>
		</p>
		<p>Value Displayname : <xsl:value-of select="$v_codedentry/n1:value/@displayName"/>
		</p>
		</xsl:if>				
		<xsl:if test="$v_codedentry/n1:value/@value">
		<p>Value : <xsl:value-of select="$v_codedentry/n1:value/@value"/> 
				<xsl:if test="$v_codedentry/n1:value/@unit">
					- <xsl:value-of select="$v_codedentry/n1:value/@unit"/>
				</xsl:if>
		</p>
		</xsl:if>
		<xsl:for-each select="$v_codedentry/n1:code/n1:qualifier">
			<p>qualifier</p>
			<p>name code=&quot;<xsl:value-of select="n1:name/@code"/>&quot; displayName=&quot;<xsl:value-of select="n1:name/@displayName"/>&quot;</p>
			<p>value code=&quot;<xsl:value-of select="n1:value/@code"/>&quot; displayName=&quot;<xsl:value-of select="n1:value/@displayName"/>&quot;</p>
		</xsl:for-each>
		<xsl:if test="$v_codedentry/n1:code/n1:originalText/n1:reference">
			<p>Code Reference : 
				<xsl:choose>
					<xsl:when test="contains($v_codedentry/n1:code/n1:originalText/n1:reference/@value,'#')">
						<a>
							<xsl:attribute name="id"><xsl:value-of select="substring-after($v_codedentry/n1:code/n1:originalText/n1:reference/@value,'#')"/></xsl:attribute>
							<xsl:value-of select="$v_codedentry/n1:code/n1:originalText/n1:reference/@value"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$v_codedentry/n1:code/n1:originalText/n1:reference/@value"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		<xsl:if test="$v_codedentry/n1:value/n1:originalText/n1:reference">
			<p>Value Reference : 
				<xsl:choose>
					<xsl:when test="contains($v_codedentry/n1:value/n1:originalText/n1:reference/@value,'#')">
						<a>
							<xsl:attribute name="id"><xsl:value-of select="substring-after($v_codedentry/n1:value/n1:originalText/n1:reference/@value,'#')"/></xsl:attribute>
							<xsl:value-of select="$v_codedentry/n1:value/n1:originalText/n1:reference/@value"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$v_codedentry/n1:value/n1:originalText/n1:reference/@value"/>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		<xsl:if test="$v_codedentry/n1:statusCode/@code">		
		<p>Status: <xsl:value-of select="$v_codedentry/n1:statusCode/@code"/>
		</p>
		</xsl:if>
		<xsl:if test="$v_codedentry/n1:effectiveTime">
		<p>Time:
		<xsl:if test="$v_codedentry/n1:effectiveTime/n1:low/@value">
				<xsl:call-template name="formatDate">
					<xsl:with-param name="date" select="$v_codedentry/n1:effectiveTime/n1:low/@value"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$v_codedentry/n1:effectiveTime/n1:high/@value">
				<xsl:text> to </xsl:text>
				<xsl:call-template name="formatDate">
					<xsl:with-param name="date" select="$v_codedentry/n1:effectiveTime/n1:high/@value"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$v_codedentry//n1:effectiveTime/n1:center/@value">
				<xsl:call-template name="formatDate">
					<xsl:with-param name="date" select="$v_codedentry/n1:effectiveTime/n1:center/@value"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$v_codedentry//n1:effectiveTime/@value">
				<xsl:call-template name="formatDate">
					<xsl:with-param name="date" select="$v_codedentry/n1:effectiveTime/@value"/>
				</xsl:call-template>
			</xsl:if>			
		</p>
		</xsl:if>
		<hr/>
		</xsl:if>
	</xsl:template>
	<xsl:template name="RenderSectionCode">
		<xsl:param name="p_title"/>
		<xsl:if test="$debug='yes' and $p_title/../n1:code/@code">
			<font color="red">
				<xsl:text>  [code=&quot;</xsl:text>
				<xsl:value-of select="$p_title/../n1:code/@code"/>
				<xsl:text>&quot;]</xsl:text>
			</font>
		</xsl:if>
	</xsl:template>
	<!--   Title  -->
	<xsl:template match="n1:title">
		<xsl:variable name="v_current" select="."/>
		<xsl:variable name="sectionNestingLevel" select="count($v_current/ancestor::n1:section)"/>
		<xsl:choose>
			<xsl:when test="contains($v_current/../n1:templateId/@extension,'Section1') or $sectionNestingLevel = 1">
				<h1>
					<!--Only when author is not present at this level else hr will come via author PRTR1-->
					<xsl:if test = "not($v_current/../n1:author)">
						<hr/>
					</xsl:if>	
					<xsl:value-of select="$v_current"/>
					<xsl:call-template name="RenderSectionCode">
						<xsl:with-param name="p_title" select="$v_current"/>
					</xsl:call-template>
				</h1>
			</xsl:when>
			<xsl:when test="contains($v_current/../n1:templateId/@extension,'section2') or  contains($v_current/../n1:templateId/@extension,'Section2')or $sectionNestingLevel = 2 ">
				<h2>				
					<xsl:value-of select="$v_current"/>
					<xsl:call-template name="RenderSectionCode">
						<xsl:with-param name="p_title" select="$v_current"/>
					</xsl:call-template>
				</h2>
			</xsl:when>
			<xsl:when test="contains($v_current/../n1:templateId/@extension,'section3') or contains($v_current/../n1:templateId/@extension,'Section3') or $sectionNestingLevel = 3 ">
				<h3>
					<xsl:value-of select="$v_current"/>
					<xsl:call-template name="RenderSectionCode">
						<xsl:with-param name="p_title" select="$v_current"/>
					</xsl:call-template>
				</h3>
			</xsl:when>
			<xsl:when test="contains($v_current/../n1:templateId/@extension,'section4') or $sectionNestingLevel = 4 ">
				<h4>
					<xsl:value-of select="$v_current"/>
					<xsl:call-template name="RenderSectionCode">
						<xsl:with-param name="p_title" select="$v_current"/>
					</xsl:call-template>
				</h4>
			</xsl:when>
			<xsl:when test="contains($v_current/../n1:templateId/@extension,'section5') or $sectionNestingLevel = 5 ">
				<h5>
					<xsl:value-of select="$v_current"/>
					<xsl:call-template name="RenderSectionCode">
						<xsl:with-param name="p_title" select="$v_current"/>
					</xsl:call-template>
				</h5>
			</xsl:when>
			<xsl:when test="contains($v_current/../n1:templateId/@extension,'section6') or $sectionNestingLevel = 6  ">
				<h6>
					<xsl:value-of select="$v_current"/>
					<xsl:call-template name="RenderSectionCode">
						<xsl:with-param name="p_title" select="$v_current"/>
					</xsl:call-template>
				</h6>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$v_current"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--   Text  Section Author -->
	<xsl:template match="n1:author">
		<xsl:variable name="v_current" select="."/>
		<!--PRTR1 code to render author of text section-->
		<div class="textsection">
		<table class="textsection">
			<tr>
				<th class="textsection">
					<xsl:text>Authored by</xsl:text>
				</th>
				<td class="textsection">
					<xsl:choose>
						<xsl:when test="$v_current/n1:assignedAuthor/n1:assignedPerson/n1:name">
							<xsl:call-template name="getName">
								<xsl:with-param name="name" select="$v_current/n1:assignedAuthor/n1:assignedPerson/n1:name"/>
							</xsl:call-template>
							<xsl:if test="$v_current/n1:assignedAuthor/n1:code/@displayName">
								<xsl:text> - </xsl:text>
								<xsl:value-of select="$v_current/n1:assignedAuthor/n1:code/@displayName"/>
							</xsl:if>
							<xsl:if test="$v_current/n1:assignedAuthor/n1:representedOrganization/n1:name">
								<xsl:text>, </xsl:text>
								<xsl:value-of select="$v_current/n1:assignedAuthor/n1:representedOrganization/n1:name"/>
							</xsl:if>
						</xsl:when>
						<xsl:when test="$v_current/n1:assignedAuthor/n1:assignedAuthoringDevice/n1:manufacturerModelName">
							<xsl:value-of select="$v_current/n1:assignedAuthor/n1:assignedAuthoringDevice/n1:manufacturerModelName"/>
							<xsl:if test="$v_current/n1:assignedAuthor/n1:representedOrganization/n1:name">
								<xsl:text> at </xsl:text>
								<xsl:value-of select="$v_current/n1:assignedAuthor/n1:representedOrganization/n1:name"/>
							</xsl:if>
						</xsl:when>
						<xsl:when test="$v_current/n1:assignedAuthor/n1:representedOrganization/n1:name">
							<xsl:value-of select="$v_current/n1:assignedAuthor/n1:representedOrganization/n1:name"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>Unknown</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text> on </xsl:text>
					<xsl:call-template name="formatDate">
						<xsl:with-param name="date" select="$v_current/n1:time/@value"/>
					</xsl:call-template>
				</td>
			</tr>	
		</table>
		</div>	
	</xsl:template>	
	
	<!--   Text   -->
	<xsl:template match="n1:text">
		<xsl:apply-templates/>
	</xsl:template>	
	
	<!-- Link Html -->
	<xsl:template match="n1:linkHtml">
		<xsl:element name="a">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates/>
			<!--<xsl:for-each select="l@*">
   			<xsl:if test="not(name()=&quot;originator&quot; or name()=&quot;confidentiality&quot;)">
				<xsl:attribute name="{name()}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:if>
		</xsl:for-each> -->
			<xsl:value-of select="link_html"/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="render_content">
		<xsl:param name="p_ID"/>
		<p>
			<xsl:choose>
				<xsl:when test="$p_doc//descendant::n1:reference[contains(@value,$p_ID)]">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="concat('#',$p_ID)"/></xsl:attribute>
						<b>
							<font color="red">
								<xsl:value-of select="$p_ID"/>
							</font>
						</b>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<b>
						<font color="red">
							<xsl:value-of select="$p_ID"/>
						</font>
					</b>
				</xsl:otherwise>
			</xsl:choose>
		</p>
	</xsl:template>
	<!--   paragraph  -->
	<xsl:template match="n1:paragraph">
		<xsl:if test="$debug='yes' and @ID">
			<xsl:call-template name="render_content">
				<xsl:with-param name="p_ID" select="@ID"/>
			</xsl:call-template>
		</xsl:if>
		<p>
			<xsl:if test="@styleCode">
				<xsl:attribute name="class"><xsl:value-of select="@styleCode"/></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</p>
	</xsl:template>
	<!--   paragraph  -->
	<xsl:template match="n1:br">
		<br/>
	</xsl:template>
	<!--     Content w/ deleted text is hidden -->
	<xsl:template match="n1:content[@revised='delete']"/>
	<!--   content  -->
	<xsl:template match="n1:content">
		<xsl:if test="$debug='yes' and @ID">
			<xsl:call-template name="render_content">
				<xsl:with-param name="p_ID" select="@ID"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>
	<!--   list  -->
	<xsl:template match="n1:list">
		<xsl:if test="n1:caption">
			<span style="font-weight:bold; ">
				<xsl:apply-templates select="n1:caption"/>
			</span>
		</xsl:if>
		<ul>
			<xsl:for-each select="n1:item">
				<li>
					<xsl:apply-templates/>
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>
	<xsl:template match="n1:list[@listType='ordered']">
		<xsl:if test="n1:caption">
			<span style="font-weight:bold; ">
				<xsl:apply-templates select="n1:caption"/>
			</span>
		</xsl:if>
		<ol>
			<xsl:for-each select="n1:item">
				<li>
					<xsl:apply-templates/>
				</li>
			</xsl:for-each>
		</ol>
	</xsl:template>
	<!--   caption  -->
	<xsl:template match="n1:caption">
		<xsl:apply-templates/>
		<xsl:text>: </xsl:text>
	</xsl:template>
	<!--      Tables   -->
	<xsl:variable name="debugRowLabelWidth">150px</xsl:variable>
	<xsl:template match="n1:table">
		<table>
			<xsl:if test="$debug='yes' and count(descendant::n1:tr[@ID]|child::n1:thead[@ID]|child::n1:tfoot[@ID]|child::n1:tbody[@ID]|child::n1:thead[@ID]) > 0">
				<xsl:attribute name="style">margin-left: <xsl:value-of select="$debugRowLabelWidth"/>;</xsl:attribute>
			</xsl:if>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="n1:thead">
		<thead>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</thead>
	</xsl:template>
	<xsl:template match="n1:tfoot">
		<tfoot>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</tfoot>
	</xsl:template>
	<xsl:template match="n1:tbody">
		<tbody>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</tbody>
	</xsl:template>
	<xsl:template match="n1:colgroup">
		<colgroup>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</colgroup>
	</xsl:template>
	<xsl:template match="n1:col">
		<col>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</col>
	</xsl:template>
	<xsl:template match="n1:tr">
		<tr>
			<xsl:copy-of select="@*"/>
			<xsl:if test="$debug='yes'">
				<xsl:attribute name="style">position:relative;</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</tr>
	</xsl:template>
	<xsl:template match="n1:th">
		<th>
			<!--@* except @styleCode is better but is 2.0 only -->
			<xsl:copy-of select="@*[not(local-name()='styleCode')]"/>
			<xsl:attribute name="class"><xsl:value-of select="@styleCode"/></xsl:attribute>
			<xsl:if test="not(@align)">
				<xsl:attribute name="class">default</xsl:attribute>
			</xsl:if>
			<xsl:if test="$debug='yes' and position() = 1">
				<xsl:call-template name="RenderRowDebugLabel"/>
			</xsl:if>
			<xsl:apply-templates/>
		</th>
	</xsl:template>
	<xsl:template match="n1:td">
		<td>
			<xsl:copy-of select="@*"/>
			<xsl:if test="$debug='yes' and @ID">
				<xsl:call-template name="render_content">
					<xsl:with-param name="p_ID" select="@ID"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$debug='yes' and position() = 1">
				<xsl:call-template name="RenderRowDebugLabel"/>
			</xsl:if>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	<xsl:template match="n1:table/n1:caption">
		<span style="font-weight:bold; ">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template name="RenderRowDebugLabel">
		<xsl:if test="ancestor::n1:*[@ID]">
			<div style="position: relative;">
				<div>
					<xsl:attribute name="style">
						position:absolute; left: -<xsl:value-of select="$debugRowLabelWidth"/>;
						color: red; font-weight: bold;
					</xsl:attribute>
					<xsl:value-of select="ancestor::n1:*/@ID"/>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	<!--   RenderMultiMedia 
    
    this currently only handles GIF's and JPEG's.  It could, however,
    be extended by including other image MIME types in the predicate
    and/or by generating <object> or <applet> tag with the correct
    params depending on the media type  @ID  =$imageRef  referencedObject
    -->
	<xsl:template match="n1:renderMultiMedia">
		<xsl:variable name="imageRef" select="@referencedObject"/>
		<xsl:choose>
			<xsl:when test="//n1:regionOfInterest[@ID=$imageRef]">
				<!-- Here is where the Region of Interest image referencing goes -->
				<xsl:if test="//n1:regionOfInterest[@ID=$imageRef]//n1:observationMedia/n1:value[@mediaType='image/gif'           or
          @mediaType='image/jpeg']">
					<br clear="all"/>
					<xsl:element name="img">
						<xsl:attribute name="src"><xsl:value-of select="//n1:regionOfInterest[@ID=$imageRef]//n1:observationMedia/n1:value/n1:reference/@value"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<!-- Here is where the direct MultiMedia image referencing goes -->
				<xsl:if test="//n1:observationMedia[@ID=$imageRef]/n1:value[@mediaType='image/gif' or           @mediaType='image/jpeg']">
					<br clear="all"/>
					<xsl:element name="img">
						<xsl:attribute name="src"><xsl:value-of select="//n1:observationMedia[@ID=$imageRef]/n1:value/n1:reference/@value"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="//n1:content[@styleCode]">
		<xsl:if test="$debug='yes' and @ID">
			<xsl:call-template name="render_content">
				<xsl:with-param name="p_ID" select="@ID"/>
			</xsl:call-template>
		</xsl:if>
		<span>
			<xsl:attribute name="class"><xsl:value-of select="@styleCode"/></xsl:attribute>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<!--    Superscript or Subscript   -->
	<xsl:template match="n1:sup">
		<xsl:element name="sup">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="n1:sub">
		<xsl:element name="sub">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<!-- Patient Banner as per CUI guidelines -->
	<xsl:template name="patientBanner">
		<div id="patientBanner" class="banner">
			<table>
				<tbody>
					<tr>
						<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:name">
							<td>
								<xsl:call-template name="cuiName">
									<xsl:with-param name="name" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:name"/>
								</xsl:call-template>
							</td>
						</xsl:if>
						<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:birthTime">
							<td align="right">
								<span class="label"> Born </span>
								<xsl:call-template name="formatDate">
									<xsl:with-param name="date" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:birthTime/@value"/>
								</xsl:call-template>
							</td>
						</xsl:if>
						<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode">
							<td align="right">
								<span class="label"> Gender </span>
								<xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@displayName"/>
							</td>
						</xsl:if>
						<td>
							<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id/@root='2.16.840.1.113883.2.1.4.1'">
								<!-- Verified NHS Number -->
								<span class="label"> NHS No. </span>
								<xsl:call-template name="cuiNHSNo">
									<xsl:with-param name="nhsNo" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id[@root='2.16.840.1.113883.2.1.4.1']/@extension"/>
								</xsl:call-template>
								<br/>
							</xsl:if>
							<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id/@root='2.16.840.1.113883.2.1.3.2.4.18.23'">
								<!-- Unverified NHS Number -->
								<span class="label"> Unverified NHS No. </span>
								<xsl:call-template name="cuiNHSNo">
									<xsl:with-param name="nhsNo" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id[@root='2.16.840.1.113883.2.1.3.2.4.18.23']/@extension"/>
								</xsl:call-template>
								<br/>
							</xsl:if>
							<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id[@root='2.16.840.1.113883.2.1.3.2.4.18.24']">
								<!-- Local ID -->
								<span class="label"> Local Patient ID </span>
								<xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id[@root='2.16.840.1.113883.2.1.3.2.4.18.24']/@extension"/>
								<br/>
							</xsl:for-each>
						</td>
					</tr>
					<!--<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:languageCommunication">
					<tr>
						<td>
						<span class="label"> Patient's Language </span>
								<xsl:call-template name="translateLanguageCode">
									<xsl:with-param name="languageCode" select="./n1:languageCode/@code"/>
								</xsl:call-template>
								<xsl:if test="./n1:proficiencyLevelCode">
									<xsl:text> (</xsl:text> 
										<xsl:value-of select="./n1:proficiencyLevelCode/@displayName"/>	  
									<xsl:text>)</xsl:text>								
								</xsl:if>
						</td>						
					</tr>
					</xsl:for-each>-->
					<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:languageCommunication">
						<tr>
							<td>
							<p class="label"> Communication Language </p>
							<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:languageCommunication">
							<p> 
								<xsl:call-template name="translateLanguageCode">
									<xsl:with-param name="languageCode" select="./n1:languageCode/@code"/>
								</xsl:call-template> 
								<xsl:if test="./n1:proficiencyLevelCode">
									<xsl:text> (</xsl:text> 
										<xsl:value-of select="./n1:proficiencyLevelCode/@displayName"/>	  
									<xsl:text>)</xsl:text>								
								</xsl:if>								
							</p>
							</xsl:for-each>
							</td>
						</tr>
					</xsl:if>
				</tbody>
			</table>
			<table class="header">
				<tbody>
					<tr>
						<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:addr">
							<td>
								<p class="label">
									<xsl:choose>
										<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
										<xsl:when test="@use='H'">Home Address</xsl:when>
										<xsl:when test="@use='WP'">Work Address</xsl:when>
										<xsl:when test="@use='PST'">Postal Address</xsl:when>
										<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
										<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
										<xsl:when test="@use='DIR'">Direct Address</xsl:when>
										<xsl:when test="@use='PUB'">Public Address</xsl:when>
										<xsl:when test="@use='BAD'">Bad Address</xsl:when>
										<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
										<xsl:otherwise>Address</xsl:otherwise>
									</xsl:choose>
								</p>
								<p>
									<xsl:for-each select="n1:*">
										<xsl:value-of select="."/>
										<xsl:if test="not(position() = last())">
											<br/>
										</xsl:if>
									</xsl:for-each>
								</p>
							</td>
						</xsl:for-each>
						<td>
							<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:telecom">
								<span class="label">
									<xsl:choose>
										<xsl:when test="@use='H'">Home </xsl:when>
										<xsl:when test="@use='HP'">Home </xsl:when>
										<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
										<xsl:when test="@use='WP'">Work </xsl:when>
										<xsl:when test="@use='DIR'">Direct </xsl:when>
										<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
										<xsl:when test="@use='BAD'">Bad </xsl:when>
										<xsl:when test="@use='TMP'">Temporary </xsl:when>
										<xsl:when test="@use='AS'">Answer </xsl:when>
										<xsl:when test="@use='EC'">Emergency </xsl:when>
										<xsl:when test="@use='MC'">Mobile </xsl:when>
										<xsl:when test="@use='PG'">Pager </xsl:when>
										<xsl:otherwise/>
									</xsl:choose>
									<xsl:choose>
										<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
										<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
										<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
										<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
										<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
										<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
										<xsl:otherwise/>
									</xsl:choose>
								</span>
								<xsl:choose>
									<!-- don't display 'tel:' part of value -->
									<xsl:when test="contains(@value, ':')">
										<xsl:value-of select="substring-after(@value, ':')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@value"/>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:if test="not(position() = last())">
									<br/>
								</xsl:if>
							</xsl:for-each>
						</td>
						<xsl:if test="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:providerOrganization">
							<td>
								<p class="label">
									<xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:providerOrganization/n1:standardIndustryClassCode/@displayName"/>
								</p>
								<p>
									<xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:providerOrganization/n1:name"/>
									<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:providerOrganization/n1:addr/n1:*">
										<br/>
										<xsl:value-of select="."/>
									</xsl:for-each>
								</p>
								<xsl:for-each select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:providerOrganization/n1:telecom">
									<p>
										<span class="label">
											<xsl:choose>
												<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
												<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
												<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
												<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
												<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
												<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
												<xsl:otherwise/>
											</xsl:choose>
										</span>
										<xsl:choose>
											<!-- don't display 'tel:' part of value -->
											<xsl:when test="contains(@value, ':')">
												<xsl:value-of select="substring-after(@value, ':')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="@value"/>
											</xsl:otherwise>
										</xsl:choose>
									</p>
								</xsl:for-each>
							</td>
						</xsl:if>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>
	<!-- Informant Banner-->
	<xsl:template name="informantBanner">
		<div id="informantBanner" class="banner">
			<table>
				<tbody>
					<tr>
						<xsl:if test="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:relatedPerson/n1:name">
							<th class="header">
								<xsl:text>Informant</xsl:text>
							</th>
							<td>
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:relatedPerson/n1:name"/>
								</xsl:call-template>
								<!--PRTR1 Get display name as well here-->
								<xsl:if test="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:code/@displayName"/>
								</xsl:if>								
							</td>
						</xsl:if>
					</tr>
				</tbody>
			</table>
			<table class="header">
				<tbody>
					<tr>
						<xsl:for-each select="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:addr">
							<td>
								<p class="label">
									<xsl:choose>
										<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
										<xsl:when test="@use='H'">Home Address</xsl:when>
										<xsl:when test="@use='WP'">Work Address</xsl:when>
										<xsl:when test="@use='PST'">Postal Address</xsl:when>
										<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
										<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
										<xsl:when test="@use='DIR'">Direct Address</xsl:when>
										<xsl:when test="@use='PUB'">Public Address</xsl:when>
										<xsl:when test="@use='BAD'">Bad Address</xsl:when>
										<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
										<xsl:otherwise>Address</xsl:otherwise>
									</xsl:choose>
								</p>
								<p>
									<xsl:for-each select="n1:*">
										<xsl:value-of select="."/>
										<xsl:if test="not(position() = last())">
											<br/>
										</xsl:if>
									</xsl:for-each>
								</p>
							</td>
						</xsl:for-each>
						<td>
							<xsl:for-each select="/n1:ClinicalDocument/n1:informant/n1:relatedEntity/n1:telecom">
								<span class="label">
									<xsl:choose>
										<xsl:when test="@use='H'">Home </xsl:when>
										<xsl:when test="@use='HP'">Home </xsl:when>
										<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
										<xsl:when test="@use='WP'">Work </xsl:when>
										<xsl:when test="@use='DIR'">Direct </xsl:when>
										<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
										<xsl:when test="@use='BAD'">Bad </xsl:when>
										<xsl:when test="@use='TMP'">Temporary </xsl:when>
										<xsl:when test="@use='AS'">Answer </xsl:when>
										<xsl:when test="@use='EC'">Emergency </xsl:when>
										<xsl:when test="@use='MC'">Mobile </xsl:when>
										<xsl:when test="@use='PG'">Pager </xsl:when>
										<xsl:otherwise/>
									</xsl:choose>
									<xsl:choose>
										<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
										<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
										<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
										<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
										<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
										<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
										<xsl:otherwise/>
									</xsl:choose>
								</span>
								<xsl:choose>
									<!-- don't display 'tel:' part of value -->
									<xsl:when test="contains(@value, ':')">
										<xsl:value-of select="substring-after(@value, ':')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@value"/>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:if test="not(position() = last())">
									<br/>
								</xsl:if>
							</xsl:for-each>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>
	<!--  Header  -->
	<xsl:template name="header">
		<div class="header">
			<table class="header">
				<tr>
					<th class="header">
						<xsl:text>Document Created</xsl:text>
					</th>
					<td class="header">
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date" select="/n1:ClinicalDocument/n1:effectiveTime/@value"/>
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<th class="header">
						<xsl:text>Document Owner</xsl:text>
					</th>
					<td class="header">
						<xsl:value-of select="/n1:ClinicalDocument/n1:custodian/n1:assignedCustodian/n1:representedCustodianOrganization/n1:name"/>
					</td>
				</tr>
				<xsl:for-each select="/n1:ClinicalDocument/n1:author">
					<tr>
						<th class="header">
							<xsl:text>Authored by</xsl:text>
						</th>
						<td class="header">
							<xsl:choose>
								<xsl:when test="n1:assignedAuthor/n1:assignedPerson/n1:name">
									<xsl:call-template name="getName">
										<xsl:with-param name="name" select="n1:assignedAuthor/n1:assignedPerson/n1:name"/>
									</xsl:call-template>
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:assignedAuthor/n1:code/@displayName"/>
									<xsl:text>, </xsl:text>
									<xsl:value-of select="n1:assignedAuthor/n1:representedOrganization/n1:name"/>
								</xsl:when>
								<xsl:when test="n1:assignedAuthor/n1:assignedAuthoringDevice/n1:manufacturerModelName">
									<xsl:value-of select="n1:assignedAuthor/n1:assignedAuthoringDevice/n1:manufacturerModelName"/>
									<xsl:if test="n1:assignedAuthor/n1:representedOrganization/n1:name">
										<xsl:text> at </xsl:text>
										<xsl:value-of select="n1:assignedAuthor/n1:representedOrganization/n1:name"/>
									</xsl:if>
								</xsl:when>
								<xsl:when test="n1:assignedAuthor/n1:representedOrganization/n1:name">
									<xsl:value-of select="n1:assignedAuthor/n1:representedOrganization/n1:name"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>Unknown</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text> on </xsl:text>
							<xsl:call-template name="formatDate">
								<xsl:with-param name="date" select="n1:time/@value"/>
							</xsl:call-template>
						</td>
					</tr>
				</xsl:for-each>
				<xsl:for-each select="/n1:ClinicalDocument/n1:authenticator">
					<tr>
						<th class="header">
							<xsl:text>Authenticated by</xsl:text>
						</th>
						<td class="header">
							<xsl:if test="n1:assignedEntity/n1:assignedPerson/n1:name">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:text> - </xsl:text>
								<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>
							</xsl:if>
							<xsl:text> on </xsl:text>
							<xsl:call-template name="formatDate">
								<xsl:with-param name="date" select="n1:time/@value"/>
							</xsl:call-template>
						</td>
					</tr>
				</xsl:for-each>
				<xsl:for-each select="/n1:ClinicalDocument/n1:dataEnterer">
					<tr>
						<th class="header">
							<xsl:text>Entered by</xsl:text>
						</th>
						<td class="header">
							<xsl:if test="n1:assignedEntity/n1:assignedPerson/n1:name">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
								</xsl:call-template>
								<!--<xsl:text> - </xsl:text>
								<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>-->
							</xsl:if>
							<xsl:if test="n1:assignedEntity/n1:code">
								<xsl:text> - </xsl:text>
								<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>
							</xsl:if>
							<xsl:if test="n1:assignedEntity/n1:representedOrganization/n1:name">
								<xsl:text> , </xsl:text>
								<xsl:value-of select="n1:assignedEntity/n1:representedOrganization/n1:name"/>
							</xsl:if>
							<xsl:if test="n1:time">
								<xsl:text> on </xsl:text>
								<xsl:call-template name="formatDate">
									<xsl:with-param name="date" select="n1:time/@value"/>
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</xsl:for-each>
				<xsl:call-template name="performer"/>
				<!--PRTR1 Get consent here-->
				<xsl:for-each select="/n1:ClinicalDocument/n1:authorization">
					<xsl:sort select="@typeCode"/>
					<tr>
						<th class="header" valign="top">
							<xsl:choose>
								<xsl:when test="@typeCode='AUTH' and not(preceding-sibling::*/@typeCode='AUTH')">
									<xsl:choose>
										<xsl:when test="count(following-sibling::*[@typeCode='AUTH'])=0">
											<xsl:text>Consent Status</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>Consent Statuses</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text/>
								</xsl:otherwise>
							</xsl:choose>
						</th>
						<td class="header">
								<xsl:value-of select="n1:consent/n1:code/@displayName"/>
						</td>
					</tr>
				</xsl:for-each>				
			</table>
			<!--<xsl:call-template name="performer"/>-->
		</div>
	</xsl:template>
	<!--  Title Bar  -->
	<xsl:template name="titlebar">
		<div class="titlebar">
			<div class="doctitle">
				<xsl:value-of select="$title"/>
			</div>
			<table class="titlebar">
				<xsl:for-each select="/n1:ClinicalDocument/n1:componentOf">
					<xsl:if test="n1:encompassingEncounter">
						<tr>
							<th class="titlebar">Encounter Type</th>
							<td class="titlebar">
								<xsl:value-of select="n1:encompassingEncounter/n1:code/@displayName"/>
							</td>
						</tr>
						<tr>
							<th class="titlebar">Encounter Time</th>
							<td class="titlebar">
								<xsl:choose>
									<xsl:when test="//n1:encompassingEncounter/n1:effectiveTime/n1:low/@value and //n1:encompassingEncounter/n1:effectiveTime/n1:high/@value">
										<xsl:call-template name="formatDate">
											<xsl:with-param name="date" select="n1:encompassingEncounter/n1:effectiveTime/n1:low/@value"/>
										</xsl:call-template>
										<xsl:text> to </xsl:text>
										<xsl:call-template name="formatDate">
											<xsl:with-param name="date" select="n1:encompassingEncounter/n1:effectiveTime/n1:high/@value"/>
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="//n1:encompassingEncounter/n1:effectiveTime/n1:low/@value and not(n1:encompassingEncounter/n1:effectiveTime/n1:high/@value)">
										<xsl:text> From </xsl:text>
										<xsl:call-template name="formatDate">
											<xsl:with-param name="date" select="n1:encompassingEncounter/n1:effectiveTime/n1:low/@value"/>
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="//n1:encompassingEncounter/n1:effectiveTime/n1:high/@value and not(n1:encompassingEncounter/n1:effectiveTime/n1:low/@value)">
										<xsl:text> Up To </xsl:text>
										<xsl:call-template name="formatDate">
											<xsl:with-param name="date" select="n1:encompassingEncounter/n1:effectiveTime/n1:high/@value"/>
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="//n1:encompassingEncounter/n1:effectiveTime/n1:center/@value">
										<xsl:call-template name="formatDate">
											<xsl:with-param name="date" select="//n1:encompassingEncounter/n1:effectiveTime/n1:center/@value"/>
										</xsl:call-template>
									</xsl:when>
								</xsl:choose>
							</td>
						</tr>
						<!--PRTR1 Encounter identifier check for NHS 111 specific oids-->
						<xsl:for-each select="/n1:ClinicalDocument/n1:componentOf/n1:encompassingEncounter/n1:id">
							<xsl:choose>
								<xsl:when test="./@root = '2.16.840.1.113883.2.1.3.2.4.18.34'">
									<tr>
										<th class="titlebar">Case Reference</th>
										<td class="titlebar">
											<xsl:value-of select="./@extension"/>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./@root = '2.16.840.1.113883.2.1.3.2.4.18.35'">
									<tr>
										<th class="titlebar">Case ID</th>
										<td class="titlebar">
											<xsl:value-of select="./@extension"/>
										</td>
									</tr>
								</xsl:when>
								<!--<xsl:when test="./@root = 'UUID'">
									<xsl:text>This is UUID and should not be shown</xsl:text>
								</xsl:when>-->
								<xsl:otherwise>
									<xsl:if test="./@extension">
										<tr>
											<th class="titlebar">Encounter Identifier</th>
											<td class="titlebar">
												<xsl:value-of select="./@extension"/>
											</td>
										</tr>
									</xsl:if>	
								</xsl:otherwise>								
							</xsl:choose>	
						</xsl:for-each>
						<!--PRTR1 encounter disposition-->
						<xsl:if test="n1:encompassingEncounter/n1:dischargeDispositionCode/@displayName">
							<tr>
								<th class="titlebar">Encounter Disposition</th>
								<td class="titlebar">
									<xsl:value-of select="n1:encompassingEncounter/n1:dischargeDispositionCode/@displayName"/>
								</td>
							</tr>
						</xsl:if>						
						<xsl:if test="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:serviceProviderOrganization/n1:name">
							<tr>
								<th class="titlebar">Care Setting Organisation</th>
								<td class="titlebar">
									<xsl:value-of select="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:serviceProviderOrganization/n1:name"/>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:location/n1:name">
							<tr>
								<th class="titlebar">Care Setting Location</th>
								<td class="titlebar">
									<xsl:value-of select="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:location/n1:name"/>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:location/n1:addr">
							<tr>
								<th class="titlebar">Care Setting Address</th>
								<td class="titlebar">
									<xsl:for-each select="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:location/n1:addr">
										<p class="label">
											<xsl:choose>
												<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
												<xsl:when test="@use='H'">Home Address</xsl:when>
												<xsl:when test="@use='WP'">Work Address</xsl:when>
												<xsl:when test="@use='PST'">Postal Address</xsl:when>
												<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
												<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
												<xsl:when test="@use='DIR'">Direct Address</xsl:when>
												<xsl:when test="@use='PUB'">Public Address</xsl:when>
												<xsl:when test="@use='BAD'">Bad Address</xsl:when>
												<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
												<xsl:when test="@nullFlavor='NA'">Not Applicable</xsl:when>
												<xsl:when test="@nullFlavor='NI'">No Information</xsl:when>
												<xsl:when test="@nullFlavor='UNK'">Unknown</xsl:when>
												<xsl:when test="@nullFlavor='ASKU'">Asked but Unknownn</xsl:when>
												<xsl:when test="@nullFlavor='NAV'">Temporarily Unavailable</xsl:when>
												<xsl:when test="@nullFlavor='NASK'">Not Asked</xsl:when>
												<xsl:when test="@nullFlavor='MSK'">Masked</xsl:when>
												<xsl:when test="@nullFlavor='NP'">Not Present</xsl:when>
												<xsl:otherwise>Address</xsl:otherwise>
											</xsl:choose>
										</p>
										<p>
											<xsl:for-each select="n1:*">
												<xsl:value-of select="."/>
												<xsl:if test="not(position() = last())">
													<br/>
												</xsl:if>
											</xsl:for-each>
										</p>
									</xsl:for-each>
									<!--PRTR1-->
								</td>
							</tr>
						</xsl:if>
						<tr>
							<th class="titlebar">Care Setting Type</th>
							<td class="titlebar">
								<xsl:value-of select="n1:encompassingEncounter/n1:location/n1:healthCareFacility/n1:code/@displayName"/>
							</td>
						</tr>
						<xsl:for-each select="n1:encompassingEncounter/n1:responsibleParty">
							<tr>
								<th class="titlebar">
									<xsl:text>Responsible Party</xsl:text>
								</th>
								<td class="titlebar">
									<xsl:if test="n1:assignedEntity/n1:assignedPerson/n1:name">
										<xsl:call-template name="getName">
											<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
										</xsl:call-template>
										<xsl:if test="n1:assignedEntity/n1:code/@displayName">
											<xsl:text> - </xsl:text>
											<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>
										</xsl:if>
										<xsl:if test="n1:assignedEntity/n1:representedOrganization/n1:name"> <!--PRTR1 code added to get the org name rendered-->
											<xsl:text>, </xsl:text>
											<xsl:call-template name="getName">
												<xsl:with-param name="name" select="n1:assignedEntity/n1:representedOrganization/n1:name"/>
											</xsl:call-template>
										<br/>
										</xsl:if>										
									</xsl:if>
								</td>
							</tr>
						</xsl:for-each>
						<xsl:for-each select="n1:encompassingEncounter/n1:encounterParticipant">
							<tr>
								<th class="titlebar">
									<xsl:choose>
										<xsl:when test="@typeCode='REF'">
											<xsl:text>Referrer</xsl:text>
										</xsl:when>
										<xsl:when test="@typeCode='ADM'">
											<xsl:text>Admitter</xsl:text>
										</xsl:when>
										<xsl:when test="@typeCode='CON'">
											<xsl:text>Consultant</xsl:text>
										</xsl:when>
										<xsl:when test="@typeCode='DIS'">
											<xsl:text>Discharger</xsl:text>
										</xsl:when>
										<xsl:when test="@typeCode='ATND'">
											<xsl:text>Attender</xsl:text>
										</xsl:when>
									</xsl:choose>
								</th>
								<td class="titlebar">
									<xsl:if test="n1:assignedEntity/n1:assignedPerson/n1:name">
										<xsl:call-template name="getName">
											<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
										</xsl:call-template>
										<xsl:if test="n1:assignedEntity/n1:code/@displayName">
											<xsl:text> - </xsl:text>
											<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>
										</xsl:if>
									</xsl:if>
								</td>
							</tr>
						</xsl:for-each>
					</xsl:if>
				</xsl:for-each>
				<!--PRTR1 code commented to take all participants under new banner-->
				<!--				<xsl:for-each select="//n1:ClinicalDocument/n1:participant[@typeCode='REFB']"> 
					<tr>
						<th class="titlebar">
							<xsl:text>Referrer</xsl:text>
						</th>
						<td class="titlebar">
							<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
							</xsl:if>
						</td>
					</tr>
					<tr>
						<th class="titlebar">
							<xsl:text>Referrering Organization</xsl:text>
						</th>
						<td class="titlebar">
							<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</xsl:for-each>-->
			</table>
		</div>
	</xsl:template>
	<!--PRTR1 Adding Participants starts here-->
	<!-- Participant Banner-->
	<xsl:template name="participantBanner">
		<div id="participantBanner" class="banner">
			<table>
				<tbody>
					<tr>
						<xsl:if test="//n1:ClinicalDocument/n1:participant">
							<th class="sectitle">
								<xsl:text>Other participant(s) in this document</xsl:text> 
							</th>
						</xsl:if>
					</tr>
				</tbody>
			</table>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='IND']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Participant</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Participant Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Participant Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>			
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='REFB']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Referred By</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Referrer Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Referrering Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='CON']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Consultant</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>			
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='REFT']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Referred To</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Referred To Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Referred To Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>			
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='BBY']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Patient's Baby</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Location</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='REF']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Referred By</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Referred By Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Referred By Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>			
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='CALLBCK']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Call back Contact</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='DEV']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Device</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Location</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='DIST']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Distributed By</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='ELOC']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Data Entry Location</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text> <!--PRTR1 Obselete-->
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='NOT']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Urgent Notification</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Notified To</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Notified Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='VIA']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Document Sent Via</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='WIT']">
				<table>
					<tr>
						<th class="participant">
							<xsl:text>Witness</xsl:text>
						</th>
					</tr>
					<xsl:if test="n1:associatedEntity/n1:associatedPerson/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Name</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
								</xsl:call-template>
								<xsl:if test="n1:associatedEntity/n1:code/@displayName">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:associatedEntity/n1:code/@displayName"/>
								</xsl:if>
						</td>
					</tr>
					</xsl:if>
					<xsl:if test="n1:associatedEntity/n1:scopingOrganization/n1:name">
					<tr>
						<th class="header">
							<xsl:text>Organization</xsl:text>
						</th>
						<td class="titlebar">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:associatedEntity/n1:scopingOrganization/n1:name"/>
								</xsl:call-template>
						</td>
					</tr>
					</xsl:if>
				</table>
				<xsl:if test="n1:associatedEntity/n1:addr">
					<xsl:call-template name="participantBody"/>
				</xsl:if>
			</xsl:for-each>
		</div>
	</xsl:template>
	
	<xsl:template name="participantBody">
				<table class="header">
					<tbody>
						<tr>
							<xsl:for-each select="n1:associatedEntity/n1:addr">
								<td>
									<p class="label">
										<xsl:choose>
												<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
												<xsl:when test="@use='H'">Home Address</xsl:when>
												<xsl:when test="@use='WP'">Work Address</xsl:when>
												<xsl:when test="@use='PST'">Postal Address</xsl:when>
												<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
												<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
												<xsl:when test="@use='DIR'">Direct Address</xsl:when>
												<xsl:when test="@use='PUB'">Public Address</xsl:when>
												<xsl:when test="@use='BAD'">Bad Address</xsl:when>
												<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
												<xsl:when test="@nullFlavor='NA'">Not Applicable</xsl:when>
												<xsl:when test="@nullFlavor='NI'">No Information</xsl:when>
												<xsl:when test="@nullFlavor='UNK'">Unknown</xsl:when>
												<xsl:when test="@nullFlavor='ASKU'">Asked but Unknownn</xsl:when>
												<xsl:when test="@nullFlavor='NAV'">Temporarily Unavailable</xsl:when>
												<xsl:when test="@nullFlavor='NASK'">Not Asked</xsl:when>
												<xsl:when test="@nullFlavor='MSK'">Masked</xsl:when>
												<xsl:when test="@nullFlavor='NP'">Not Present</xsl:when>
												<xsl:otherwise>Address</xsl:otherwise>
										</xsl:choose>
									</p>
									<p>
										<xsl:for-each select="n1:*">
											<xsl:value-of select="."/>
											<xsl:if test="not(position() = last())">
												<br/>
											</xsl:if>
										</xsl:for-each>
									</p>
								</td>
							</xsl:for-each>
							<td>
								<xsl:for-each select="n1:associatedEntity/n1:telecom">
									<span class="label">
										<xsl:choose>
											<xsl:when test="@use='H'">Home </xsl:when>
											<xsl:when test="@use='HP'">Home </xsl:when>
											<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
											<xsl:when test="@use='WP'">Work </xsl:when>
											<xsl:when test="@use='DIR'">Direct </xsl:when>
											<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
											<xsl:when test="@use='BAD'">Bad </xsl:when>
											<xsl:when test="@use='TMP'">Temporary </xsl:when>
											<xsl:when test="@use='AS'">Answer </xsl:when>
											<xsl:when test="@use='EC'">Emergency </xsl:when>
											<xsl:when test="@use='MC'">Mobile </xsl:when>
											<xsl:when test="@use='PG'">Pager </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
											<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
											<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
											<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
											<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
											<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
									</span>
									<xsl:choose>
										<!-- don't display 'tel:' part of value -->
										<xsl:when test="contains(@value, ':')">
											<xsl:value-of select="substring-after(@value, ':')"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="@value"/>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="not(position() = last())">
										<br/>
									</xsl:if>
								</xsl:for-each>
							</td>
						</tr>
					</tbody>
				</table>
				<br/>	
	</xsl:template>	
	
	
<!--PRTR1 Participants ends here-->
	<!--  Footer  -->
	<xsl:template name="footer">
		<div class="footer">
			<table class="header">
				<tr>
					<th class="header">
						<xsl:text>Document ID</xsl:text>
					</th>
					<td class="header">
						<xsl:value-of select="/n1:ClinicalDocument/n1:id/@root"/>
					</td>
					<th class="header">
						<xsl:text>Version</xsl:text>
					</th>
					<td class="header">
						<xsl:value-of select="/n1:ClinicalDocument/n1:versionNumber/@value"/>
					</td>
				</tr>
				<!--PRTR1 get replacement of (related document) here-->
					<xsl:if test="/n1:ClinicalDocument/n1:relatedDocument/@typeCode='RPLC'">
						<tr>
							<th class="header">
								<xsl:text>Replacement of</xsl:text>
							</th>
							<td class="header">
								<xsl:value-of select="/n1:ClinicalDocument/n1:relatedDocument/n1:parentDocument/n1:id/@root"/>
							</td>
							<th class="header">
								<xsl:text>Version</xsl:text>
							</th>
							<td class="header">
								<xsl:value-of select="/n1:ClinicalDocument/n1:relatedDocument/n1:parentDocument/n1:versionNumber/@value"/>
							</td>
						</tr>	
					</xsl:if>
					<!--PRTR1 get addendum to (related document) here-->
					<xsl:if test="/n1:ClinicalDocument/n1:relatedDocument/@typeCode='APND'">
						<tr>
							<th class="header">
								<xsl:text>Addendum to</xsl:text>
							</th>
							<td class="header">
								<xsl:value-of select="/n1:ClinicalDocument/n1:relatedDocument/n1:parentDocument/n1:id/@root"/>
							</td>
							<th class="header">
								<xsl:text>Version</xsl:text>
							</th>
							<td class="header">
								<xsl:value-of select="/n1:ClinicalDocument/n1:relatedDocument/n1:parentDocument/n1:versionNumber/@value"/>
							</td>
						</tr>	
					</xsl:if>
					<!--PRTR1 get transformation of  (related document) here-->	
					<xsl:if test="/n1:ClinicalDocument/n1:relatedDocument/@typeCode='XFRM'">
						<tr>
							<th class="header">
								<xsl:text>Transformation of </xsl:text>
							</th>
							<td class="header">
								<xsl:value-of select="/n1:ClinicalDocument/n1:relatedDocument/n1:parentDocument/n1:id/@root"/>
							</td>
							<th class="header">
								<xsl:text>Version</xsl:text>
							</th>
							<td class="header">
								<xsl:value-of select="/n1:ClinicalDocument/n1:relatedDocument/n1:parentDocument/n1:versionNumber/@value"/>
							</td>
						</tr>	
					</xsl:if>														
				<xsl:for-each select="n1:informationRecipient">
					<xsl:sort select="@typeCode"/>
					<tr>
						<th class="header" valign="top">
							<xsl:choose>
								<xsl:when test="@typeCode='PRCP'and not(preceding-sibling::*/@typeCode='PRCP')">
									<xsl:choose>
										<xsl:when test="count(following-sibling::*[@typeCode='PRCP'])=0">
											<xsl:text>Primary Recipient</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>Primary Recipients</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="@typeCode='TRC'and not(preceding-sibling::*/@typeCode='TRC')">
									<xsl:choose>
										<xsl:when test="count(following-sibling::*[@typeCode='TRC'])=0">
											<xsl:text>Copy Recipient</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>Copy Recipients</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text/>
								</xsl:otherwise>
							</xsl:choose>
						</th>
						<td class="header">
							<xsl:if test="n1:intendedRecipient/n1:informationRecipient/n1:name">
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:intendedRecipient/n1:informationRecipient/n1:name"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="n1:intendedRecipient/npfitlc:recipientRoleCode/@displayName">
								<xsl:if test="n1:intendedRecipient/n1:informationRecipient/n1:name">
									<xsl:text> - </xsl:text>
								</xsl:if>
								<xsl:value-of select="n1:intendedRecipient/npfitlc:recipientRoleCode/@displayName"/>
							</xsl:if>
							<xsl:if test="n1:intendedRecipient/n1:receivedOrganization/n1:name">
								<xsl:if test="n1:intendedRecipient/n1:informationRecipient/n1:name or n1:intendedRecipient/npfitlc:recipientRoleCode/@displayName">
									<xsl:text>, </xsl:text>
								</xsl:if>
								<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:intendedRecipient/n1:receivedOrganization/n1:name"/>
								</xsl:call-template>
								<br/>
							</xsl:if>
						</td>
						<th class="header">
							<xsl:if test="n1:intendedRecipient/n1:telecom">
								<xsl:choose>
									<xsl:when test="contains(n1:intendedRecipient/n1:telecom/@value, 'mailto')">Email</xsl:when>
									<xsl:when test="contains(n1:intendedRecipient/n1:telecom/@value, 'tel')">Phone</xsl:when>
								</xsl:choose>
							</xsl:if>
						</th>
						<td class="header">
							<xsl:value-of select="substring-after(n1:intendedRecipient/n1:telecom/@value,':')"/>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</div>
	</xsl:template>
	<!-- 
  -->
	<xsl:template name="translateCode">
		<xsl:param name="code"/>
		<!--xsl:value-of select="document('voc.xml')/systems/system[@root=$code/@codeSystem]/code[@value=$code/@code]/@displayName"/-->
		<!--xsl:value-of select="document('codes.xml')/*/code[@code=$code]/@display"/-->
		<xsl:choose>
			<!-- lookup table Telecom URI -->
			<xsl:when test="$code='tel'">
				<xsl:text>Tel</xsl:text>
			</xsl:when>
			<xsl:when test="$code='fax'">
				<xsl:text>Fax</xsl:text>
			</xsl:when>
			<xsl:when test="$code='HP'">
				<xsl:text>Home</xsl:text>
			</xsl:when>
			<xsl:when test="$code='WP'">
				<xsl:text>Workplace</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>{$code}?</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- 
    Contact Information
  -->
	<xsl:template name="getContactInfo">
		<xsl:param name="contact"/>
		<xsl:apply-templates select="$contact/n1:addr"/>
		<xsl:apply-templates select="$contact/n1:telecom"/>
	</xsl:template>
	<xsl:template match="n1:addr">
		<xsl:for-each select="n1:streetAddressLine">
			<xsl:value-of select="."/>
			<br/>
		</xsl:for-each>
		<xsl:if test="n1:streetName">
			<xsl:value-of select="n1:streetName"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="n1:houseNumber"/>
			<br/>
		</xsl:if>
		<xsl:value-of select="n1:postalCode"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="n1:city"/>
		<xsl:if test="n1:state">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="n1:state"/>
		</xsl:if>
		<br/>
	</xsl:template>
	<xsl:template match="n1:telecom">
		<xsl:variable name="type" select="substring-before(@value, ':')"/>
		<xsl:variable name="value" select="substring-after(@value, ':')"/>
		<xsl:if test="$type">
			<xsl:call-template name="translateCode">
				<xsl:with-param name="code" select="$type"/>
			</xsl:call-template>
			<xsl:text>: </xsl:text>
			<xsl:text> </xsl:text>
			<xsl:value-of select="$value"/>
			<xsl:if test="@use">
				<xsl:text> (</xsl:text>
				<xsl:call-template name="translateCode">
					<xsl:with-param name="code" select="@use"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</xsl:if>
			<br/>
		</xsl:if>
	</xsl:template>
	<xsl:template name="support">
		<table width="100%">
			<xsl:for-each select="/n1:ClinicalDocument/n1:participant[@typeCode='IND']">
				<tr>
					<td>
						<b>
							<xsl:for-each select="n1:associatedEntity/n1:code">
								<xsl:call-template name="translateCode">
									<xsl:with-param name="code" select="."/>
								</xsl:call-template>
								<xsl:text> </xsl:text>
							</xsl:for-each>
						</b>
					</td>
					<td>
						<xsl:call-template name="getName">
							<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td/>
					<td>
						<xsl:call-template name="getContactInfo">
							<xsl:with-param name="contact" select="n1:associatedEntity"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!--PRTR1 This is for Service Event template [0..*] and each service event can have multiple performers [0..*]-->
	<xsl:template name="performer">
		<xsl:for-each select="//n1:documentationOf">
			<tr>
				<th class="header" valign="top">
					<xsl:choose>
						<xsl:when test="@typeCode='DOC' and not(preceding-sibling::*/@typeCode='DOC')">
							<xsl:choose>
								<xsl:when test="count(following-sibling::*[@typeCode='DOC'])=0">
									<xsl:text>Report of</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>Reports of</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text/>
						</xsl:otherwise>
					</xsl:choose>
				</th>
				<td class="header">
					<xsl:if test="n1:serviceEvent/n1:code">
						<xsl:value-of select="n1:serviceEvent/n1:code/@displayName"/>
					</xsl:if>
					<xsl:if test="n1:serviceEvent/n1:effectiveTime">
						<xsl:text> - </xsl:text>
						<xsl:text>From </xsl:text>
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date" select="n1:serviceEvent/n1:effectiveTime/n1:low/@value"/>
						</xsl:call-template>
						<xsl:text>  To </xsl:text>
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date" select="n1:serviceEvent/n1:effectiveTime/n1:high/@value"/>
						</xsl:call-template>
					</xsl:if>
					<!--<xsl:if test="n1:serviceEvent/n1:performer/n1:assignedEntity/n1:assignedPerson/n1:name">
						<xsl:text> , </xsl:text>
						<xsl:call-template name="getName">
							<xsl:with-param name="name" select="n1:serviceEvent/n1:performer/n1:assignedEntity/n1:assignedPerson/n1:name"/>
						</xsl:call-template>
					</xsl:if>-->
				<!--</td>-->
				<!--PRTR1 Get the performer loop here-->
				<p>
				<xsl:for-each select="n1:serviceEvent/n1:performer">
					<xsl:sort select="@typeCode"/>
							<xsl:choose>
								<xsl:when test="@typeCode='PRF' and not(preceding-sibling::*/@typeCode='PRF')">
									<xsl:choose>
										<xsl:when test="count(following-sibling::*[@typeCode='PRF'])=0">
										   <span class="label">
												<xsl:text>Performed by </xsl:text>
											</span> 
										</xsl:when>
										<xsl:otherwise>
										    <span class="label">
												<xsl:text>Performed by </xsl:text> <!--PRTR1 Changed with comment from KZS-->
											</span>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:call-template name="getName">
									<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
							</xsl:call-template>
							<xsl:if test="not(position() = last())">
								<xsl:text>, </xsl:text>
							</xsl:if>
					</xsl:for-each>
				</p>		
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
	<!--PRTR1 Support for translation of language code-->
   <xsl:template name="translateLanguageCode">
      <xsl:param name="languageCode"/>
      <xsl:choose>
		<!--PRTR1 Added Language Mapping-->
		<xsl:when test="$languageCode='aa'"><xsl:text>Afar</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ab'"><xsl:text>Abkhazian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='af'"><xsl:text>Afrikaans</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ak'"><xsl:text>Akan</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sq'"><xsl:text>Albanian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='am'"><xsl:text>Amharic</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ar'"><xsl:text>Arabic</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='an'"><xsl:text>Aragonese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='hy'"><xsl:text>Armenian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='as'"><xsl:text>Assamese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='av'"><xsl:text>Avaric</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ae'"><xsl:text>Avestan</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ay'"><xsl:text>Aymara</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='az'"><xsl:text>Azerbaijani</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ba'"><xsl:text>Bashkir</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bm'"><xsl:text>Bambara</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='eu'"><xsl:text>Basque</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='be'"><xsl:text>Belarusian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bn'"><xsl:text>Bengali</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bh'"><xsl:text>Bihari</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bi'"><xsl:text>Bislama</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bo'"><xsl:text>Tibetan</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bs'"><xsl:text>Bosnian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='br'"><xsl:text>Breton</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='bg'"><xsl:text>Bulgarian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='my'"><xsl:text>Burmese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ca'"><xsl:text>Catalan; Valencian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='cs'"><xsl:text>Czech</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ch'"><xsl:text>Chamorro</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ce'"><xsl:text>Chechen</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='zh'"><xsl:text>Chinese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='cu'"><xsl:text>Church Slavonic</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='cv'"><xsl:text>Chuvash</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kw'"><xsl:text>Cornish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='co'"><xsl:text>Corsican</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='cr'"><xsl:text>Cree</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='cy'"><xsl:text>Welsh</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='da'"><xsl:text>Danish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='de'"><xsl:text>German</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='dv'"><xsl:text>Dhivehi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='nl'"><xsl:text>Dutch</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='dz'"><xsl:text>Dzongkha</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='el'"><xsl:text>Greek</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='en'"><xsl:text>English</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='eo'"><xsl:text>Esperanto</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='et'"><xsl:text>Estonian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ee'"><xsl:text>Ewe</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='fo'"><xsl:text>Faroese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='fa'"><xsl:text>Persian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='fj'"><xsl:text>Fijian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='fi'"><xsl:text>Finnish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='fr'"><xsl:text>French</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='fy'"><xsl:text>Western Frisian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ff'"><xsl:text>Fulah</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ka'"><xsl:text>Georgian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='gd'"><xsl:text>Gaelic</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ga'"><xsl:text>Irish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='gl'"><xsl:text>Galician</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='gv'"><xsl:text>Manx</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='gn'"><xsl:text>Guarani</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='gu'"><xsl:text>Gujarati</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ht'"><xsl:text>Haitian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ha'"><xsl:text>Hausa</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='he'"><xsl:text>Hebrew</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='hz'"><xsl:text>Herero</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='hi'"><xsl:text>Hindi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ho'"><xsl:text>Hiri Motu</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='hr'"><xsl:text>Croatian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='hu'"><xsl:text>Hungarian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ig'"><xsl:text>Igbo</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='is'"><xsl:text>Icelandic</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='io'"><xsl:text>Ido</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ii'"><xsl:text>Sichuan Yi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='iu'"><xsl:text>Inuktitut</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ie'"><xsl:text>Interlingue</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ia'"><xsl:text>Interlingua</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='id'"><xsl:text>Indonesian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ik'"><xsl:text>Inupiaq</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='it'"><xsl:text>Italian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='jv'"><xsl:text>Javanese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ja'"><xsl:text>Japanese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kl'"><xsl:text>Greenlandic</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kn'"><xsl:text>Kannada</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ks'"><xsl:text>Kashmiri</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kr'"><xsl:text>Kanuri</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kk'"><xsl:text>Kazakh</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='km'"><xsl:text>Central Khmer</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ki'"><xsl:text>Kikuyu; Gikuyu</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='rw'"><xsl:text>Kinyarwanda</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ky'"><xsl:text>Kirghiz; Kyrgyz</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kv'"><xsl:text>Komi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kg'"><xsl:text>Kongo</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ko'"><xsl:text>Korean</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='kj'"><xsl:text>Kwanyama</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ku'"><xsl:text>Kurdish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='lo'"><xsl:text>Lao</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='la'"><xsl:text>Latin</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='lv'"><xsl:text>Latvian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='li'"><xsl:text>Limburgan</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ln'"><xsl:text>Lingala</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='lt'"><xsl:text>Lithuanian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='lb'"><xsl:text>Luxembourgish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='lu'"><xsl:text>Luba-Katanga</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='lg'"><xsl:text>Ganda</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mk'"><xsl:text>Macedonian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mh'"><xsl:text>Marshallese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ml'"><xsl:text>Malayalam</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mi'"><xsl:text>Maori</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mr'"><xsl:text>Marathi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ms'"><xsl:text>Malay</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mg'"><xsl:text>Malagasy</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mt'"><xsl:text>Maltese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mo'"><xsl:text>Moldavian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='mn'"><xsl:text>Mongolian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='na'"><xsl:text>Nauru</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='nv'"><xsl:text>Navajo</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='nr'"><xsl:text>South Ndebele</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='nd'"><xsl:text>North Ndebele</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ng'"><xsl:text>Ndonga</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ne'"><xsl:text>Nepali</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='nn'"><xsl:text>Norwegian Nynorsk</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='nb'"><xsl:text>Norwegian Bokmål</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='no'"><xsl:text>Norwegian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ny'"><xsl:text>Chichewa</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='oc'"><xsl:text>Occitan</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='oj'"><xsl:text>Ojibwa</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='or'"><xsl:text>Oriya</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='om'"><xsl:text>Oromo</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='os'"><xsl:text>Ossetian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='pa'"><xsl:text>Panjabi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='pi'"><xsl:text>Pali</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='pl'"><xsl:text>Polish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='pt'"><xsl:text>Portuguese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ps'"><xsl:text>Pushto</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='qu'"><xsl:text>Quechua</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='rm'"><xsl:text>Romansh</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ro'"><xsl:text>Romanian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='rn'"><xsl:text>Rundi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ru'"><xsl:text>Russian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sg'"><xsl:text>Sango</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sa'"><xsl:text>Sanskrit</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sr'"><xsl:text>Serbian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='si'"><xsl:text>Sinhalese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sk'"><xsl:text>Slovak</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sl'"><xsl:text>Slovenian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='se'"><xsl:text>Northern Sami</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sm'"><xsl:text>Samoan</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sn'"><xsl:text>Shona</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sd'"><xsl:text>Sindhi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='so'"><xsl:text>Somali</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='st'"><xsl:text>Sotho</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='es'"><xsl:text>Spanish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sc'"><xsl:text>Sardinian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ss'"><xsl:text>Swati</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='su'"><xsl:text>Sundanese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sw'"><xsl:text>Swahili</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='sv'"><xsl:text>Swedish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ty'"><xsl:text>Tahitian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ta'"><xsl:text>Tamil</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tt'"><xsl:text>Tatar</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='te'"><xsl:text>Telugu</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tg'"><xsl:text>Tajik</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tl'"><xsl:text>Tagalog</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='th'"><xsl:text>Thai</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ti'"><xsl:text>Tigrinya</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='to'"><xsl:text>Tonga</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tn'"><xsl:text>Tswana</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ts'"><xsl:text>Tsonga</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tk'"><xsl:text>Turkmen</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tr'"><xsl:text>Turkish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='tw'"><xsl:text>Twi</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ug'"><xsl:text>Uighur</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='uk'"><xsl:text>Ukrainian</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ur'"><xsl:text>Urdu</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='uz'"><xsl:text>Uzbek</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='ve'"><xsl:text>Venda</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='vi'"><xsl:text>Vietnamese</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='vo'"><xsl:text>Volapük</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='wa'"><xsl:text>Walloon</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='wo'"><xsl:text>Wolof</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='xh'"><xsl:text>Xhosa</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='yi'"><xsl:text>Yiddish</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='yo'"><xsl:text>Yoruba</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='za'"><xsl:text>Zhuang</xsl:text></xsl:when>		
		<xsl:when test="$languageCode='zu'"><xsl:text>Zulu</xsl:text></xsl:when>
         <xsl:otherwise>
            <xsl:text>{$languageCode='</xsl:text>
            <xsl:value-of select="$languageCode"/>
            <xsl:text>'?}</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
    </xsl:template>	
</xsl:stylesheet>
