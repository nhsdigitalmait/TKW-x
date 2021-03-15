<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
 <xsl:variable name="vDigits" select="'0123456789'"/>
<xsl:strip-space elements="*"/>
<xsl:template match="/*">
	
	<xsl:for-each select="//nHSNumber/value/@extension">
	<xsl:choose>
   <xsl:when test="0 = string-length(translate(.,$vDigits,''))">
		 <xsl:call-template name="mod11"/>
	</xsl:when>
	<xsl:otherwise>
    	<xsl:text> ERROR NhsNo contains non numeric values</xsl:text>
   </xsl:otherwise>
  </xsl:choose>
	</xsl:for-each>
		<xsl:for-each select="//*[local-name()='patient']/*[local-name()='id']/@extension">
	<xsl:choose>
   <xsl:when test="0 = string-length(translate(.,$vDigits,''))">
		 <xsl:call-template name="mod11"/>
	</xsl:when>
	<xsl:otherwise>
    	<xsl:text> ERROR NhsNo contains non numeric values</xsl:text>
   </xsl:otherwise>
  </xsl:choose>
	</xsl:for-each>
		<xsl:for-each select="//*[local-name()='person.NHSNumber']/*[local-name()='value']/@extension">
	<xsl:choose>
   <xsl:when test="0 = string-length(translate(.,$vDigits,''))">
		 <xsl:call-template name="mod11"/>
	</xsl:when>
	<xsl:otherwise>
    	<xsl:text> ERROR NhsNo contains non numeric values</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
	</xsl:for-each>
<xsl:for-each select="//*[local-name()='parameter']/*[local-name()='name'][@value='NHSNumber']/../*[local-name()='valueString']/@value">
	<xsl:choose>
   <xsl:when test="0 = string-length(translate(.,$vDigits,''))">
		 <xsl:call-template name="mod11"/>
	</xsl:when>
	<xsl:otherwise>
    	<xsl:text> ERROR NhsNo contains non numeric values</xsl:text>
   </xsl:otherwise>
  </xsl:choose>
	</xsl:for-each>
</xsl:template>

<xsl:template name="mod11">
<xsl:param name="pN1" select="substring(.,1,1)"/>
<xsl:param name="pN2" select="substring(.,2,1)"/>
<xsl:param name="pN3" select="substring(.,3,1)"/>
<xsl:param name="pN4" select="substring(.,4,1)"/>
<xsl:param name="pN5" select="substring(.,5,1)"/>
<xsl:param name="pN6" select="substring(.,6,1)"/>
<xsl:param name="pN7" select="substring(.,7,1)"/>
<xsl:param name="pN8" select="substring(.,8,1)"/>
<xsl:param name="pN9" select="substring(.,9,1)"/>
<xsl:param name="pN10" select="substring(.,10,1)"/>
<xsl:param name="Product" select="($pN1*10)+($pN2*9)+($pN3*8)+($pN4*7)+($pN5*6)+($pN6*5)+($pN7*4)+($pN8*3)+($pN9*2)"/>
<xsl:param name="ActualMod11" select="11-($Product mod 11)"/>


<xsl:if test="$ActualMod11 = 11">
	<xsl:if test="$pN10 != 0">
		<xsl:text> ERROR NhsNo has incorrect Mod11 value, for number: </xsl:text>
		<xsl:value-of select="."/>
		<xsl:text> last digit should be:</xsl:text>
		<xsl:value-of select="0"/>
	</xsl:if>
</xsl:if>

<xsl:if test="$ActualMod11 = 10">
	<xsl:if test="$pN10 != 1">
		<xsl:text> ERROR NhsNo has incorrect Mod11 value, for number: </xsl:text>
		<xsl:value-of select="."/>
		<xsl:text> last digit should be:</xsl:text>
		<xsl:value-of select="1"/>
	</xsl:if>
	</xsl:if>

<xsl:if test="$ActualMod11 &lt;10">
	<xsl:if test="$pN10 != $ActualMod11">
		<xsl:text> ERROR NhsNo has incorrect Mod11 value, for number: </xsl:text>
		<xsl:value-of select="."/>
		<xsl:text> last digit should be:</xsl:text>
		<xsl:value-of select="$ActualMod11"/>
	</xsl:if>
</xsl:if>  
  
</xsl:template>
</xsl:stylesheet>